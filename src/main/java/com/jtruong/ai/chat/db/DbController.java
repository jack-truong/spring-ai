package com.jtruong.ai.chat.db;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.jtruong.ai.chat.BaseChatController;
import com.jtruong.ai.chat.db.DbRecords.DbQueryResponse;
import com.jtruong.ai.chat.db.DbRecords.DbSchemaImageResponse;
import com.jtruong.ai.prompts.BeanPromptConverter;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/db")
public class DbController extends BaseChatController {

  private static final Logger logger = LoggerFactory.getLogger(DbController.class);

  @Value("classpath:/prompts/dbSystem.st")
  private Resource dbSystemPrompt;

  private String dbSystemPromptString;

  @Value("classpath:/prompts/dbQuery.st")
  private Resource dbQueryPrompt;

  @Value("classpath:/northwind_schema.sql")
  private Resource northwindSchema;

  private String northwindSchemaString;

  private final DbSchemaImageLoader dbSchemaImageLoader;

  private String northwindSchemaDiagramString;

  public DbController(ChatModel chatModel, DbSchemaImageLoader dbSchemaImageLoader) {
    super(chatModel);
    this.dbSchemaImageLoader = dbSchemaImageLoader;
  }

  @PostConstruct
  public void init() {
    loadDbSystemPromptString();
    loadDbSchemaString();
  }

  @GetMapping("/schema-image")
  public ResponseEntity<DbSchemaImageResponse> schemaImage() {
    return ResponseEntity.ok(new DbSchemaImageResponse(loadDbSchemaDiagramString()));
  }

  @GetMapping("/query")
  public ResponseEntity<DbQueryResponse> query(@RequestParam(value = "query") String query) {
    BeanPromptConverter<DbQueryResponse> beanPromptConverter = new BeanPromptConverter<>(
        DbQueryResponse.class,
        dbQueryPrompt,
        Map.of(
            "schema", northwindSchemaString,
            "query", query
        )
    );

    List<Message> messages = new ArrayList<>(beanPromptConverter.getPrompt().getInstructions());
    messages.add(new SystemMessage(dbSystemPromptString));

    OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
        .withFunction("queryResultsFromDatabase")
        .build();
    ChatResponse response = callAndLogMetadata(new Prompt(messages, chatOptions));

    String content = response.getResult().getOutput().getContent().replace("```", "")
        .replace("json", "");
    return ResponseEntity.ok(beanPromptConverter.convert(content));
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }

  private void loadDbSchemaString() {
    try (Reader reader = new InputStreamReader(northwindSchema.getInputStream(), UTF_8)) {
      northwindSchemaString = FileCopyUtils.copyToString(reader);
    } catch (IOException e) {
      logger.error("An error occurred trying to read the Northwind database schema", e);
    }
  }

  private void loadDbSystemPromptString() {
    try (Reader reader = new InputStreamReader(dbSystemPrompt.getInputStream(), UTF_8)) {
      dbSystemPromptString = FileCopyUtils.copyToString(reader);
    } catch (IOException e) {
      logger.error("An error occurred trying to read the DB system prompt", e);
    }
  }

  private String loadDbSchemaDiagramString() {
    try {
      if (northwindSchemaDiagramString == null) {
        northwindSchemaDiagramString = dbSchemaImageLoader.load();
      }
    } catch (IOException e) {
      logger.error("An error occurred trying to read the DB schema diagram", e);
    }
    return northwindSchemaDiagramString;
  }
}

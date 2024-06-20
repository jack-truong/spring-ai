package com.jtruong.ai.chat.db;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.jtruong.ai.chat.BaseChatController;
import com.jtruong.ai.chat.db.DbRecords.Customer;
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
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/db")
public class DbController extends BaseChatController {

  private static final Logger logger = LoggerFactory.getLogger(DbController.class);
  protected static final String GET_CUSTOMERS_QUERY = """
         SELECT first_name firstName, last_name lastName
         FROM customer
         ORDER BY lastName, firstName
      """;

  @Value("classpath:/prompts/dbSystem.st")
  private Resource dbSystemPrompt;

  private String dbSystemPromptString;

  @Value("classpath:/prompts/dbQuery.st")
  private Resource dbQueryPrompt;

  @Value("classpath:/northwind_schema.sql")
  private Resource northwindSchema;

  private String northwindSchemaString;

  private final JdbcClient jdbcClient;

  public DbController(ChatModel chatModel, JdbcClient jdbcClient) {
    super(chatModel);
    this.jdbcClient = jdbcClient;
  }

  @PostConstruct
  public void init() {
    try (Reader reader = new InputStreamReader(dbSystemPrompt.getInputStream(), UTF_8)) {
      dbSystemPromptString = FileCopyUtils.copyToString(reader);
    } catch (IOException e) {
      logger.error("An error occurred trying to read the DB system prompt", e);
    }
    try (Reader reader = new InputStreamReader(northwindSchema.getInputStream(), UTF_8)) {
      northwindSchemaString = FileCopyUtils.copyToString(reader);
    } catch (IOException e) {
      logger.error("An error occurred trying to read the Northwind database schema", e);
    }
  }

  @GetMapping("/customers")
  public ResponseEntity<List<Customer>> getCustomers() {
    List<Customer> customers = jdbcClient
        .sql(GET_CUSTOMERS_QUERY)
        .query(Customer.class).list();
    return ResponseEntity.ok(customers);
  }

  @GetMapping("/query")
  public ResponseEntity<Map<String, Object>> query(@RequestParam(value="query") String query ){
    MapOutputConverter mapOutputConverter = new MapOutputConverter();
    PromptTemplate promptTemplate = new PromptTemplate(dbQueryPrompt);
    Prompt chatPrompt = promptTemplate.create(
        Map.of(
            "schema", northwindSchemaString,
            "query", query,
            "format", mapOutputConverter.getFormat()
        )
    );

    List<Message> messages = new ArrayList<>(chatPrompt.getInstructions());
    messages.add(new SystemMessage(dbSystemPromptString));

    OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
        .withFunction("queryResultsFromDatabase")
        .build();
    ChatResponse response = callAndLogMetadata(new Prompt(messages, chatOptions));

    String content = response.getResult().getOutput().getContent().replace("```", "").replace("json", "");
    return ResponseEntity.ok(mapOutputConverter.convert(content));
  }


  @Override
  protected Logger getLogger() {
    return logger;
  }
}

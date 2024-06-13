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

  @Value("classpath:/chinook_schema.sql")
  private Resource chinookSchema;

  private String chinookSchemaString;

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
    try (Reader reader = new InputStreamReader(chinookSchema.getInputStream(), UTF_8)) {
      chinookSchemaString = FileCopyUtils.copyToString(reader);
    } catch (IOException e) {
      logger.error("An error occurred trying to read the Chinook database schema", e);
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
  public ResponseEntity<String> query(@RequestParam(value="query") String query ){
    PromptTemplate promptTemplate = new PromptTemplate(dbQueryPrompt);
    Prompt chatPrompt = promptTemplate.create(Map.of("schema", chinookSchemaString, "query", query));

    List<Message> messages = new ArrayList<>(chatPrompt.getInstructions());
    messages.add(new SystemMessage(dbSystemPromptString));
    ChatResponse response = callAndLogMetadata(new Prompt(messages));
    return ResponseEntity.ok(response.getResult().getOutput().getContent());
  }


  @Override
  protected Logger getLogger() {
    return logger;
  }
}

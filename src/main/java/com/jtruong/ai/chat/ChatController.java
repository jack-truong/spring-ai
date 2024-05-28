package com.jtruong.ai.chat;

import com.jtruong.ai.prompts.ListPromptParser;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class ChatController extends BaseChatController {
  private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

  @Value("classpath:/prompts/activities.st")
  private Resource activities;

  @Value("classpath:/prompts/environments.st")
  private Resource environments;

  @Value("classpath:/prompts/foods.st")
  private Resource foods;

  public ChatController(ChatClient chatClient) {
    super(chatClient);
  }

  @GetMapping("/chat")
  public ResponseEntity<String> getChatResponse(@RequestParam(value = "prompt") String prompt) {
    PromptTemplate promptTemplate = new PromptTemplate(prompt);
    Prompt chatPrompt = promptTemplate.create();

    ChatResponse response = callAndLogMetadata(chatPrompt);
    return ResponseEntity.ok(response.getResult().getOutput().getContent());
  }

  @GetMapping("/activities")
  public ResponseEntity<List<String>> getActivities() {
    ListPromptParser listPromptParser = new ListPromptParser(activities);

    ChatResponse response = callAndLogMetadata(listPromptParser.getPrompt());
    return ResponseEntity.ok(listPromptParser.parse(response.getResult().getOutput().getContent()));
  }

  @GetMapping("/environments")
  public ResponseEntity<List<String>> getEnvironments() {
    ListPromptParser listPromptParser = new ListPromptParser(environments);

    ChatResponse response = callAndLogMetadata(listPromptParser.getPrompt());
    return ResponseEntity.ok(listPromptParser.parse(response.getResult().getOutput().getContent()));
  }

  @GetMapping("/foods")
  public ResponseEntity<List<String>> getFoods() {
    ListPromptParser listPromptParser = new ListPromptParser(foods);

    ChatResponse response = callAndLogMetadata(listPromptParser.getPrompt());
    return ResponseEntity.ok(listPromptParser.parse(response.getResult().getOutput().getContent()));
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }
}

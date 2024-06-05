package com.jtruong.ai.chat;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
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
  private Resource activitiesPrompt;

  @Value("classpath:/prompts/environments.st")
  private Resource environmentsPrompt;

  @Value("classpath:/prompts/instruments.st")
  private Resource instrumentsPrompt;

  @Value("classpath:/prompts/foods.st")
  private Resource foodsPrompt;

  public ChatController(ChatModel chatModel) {
    super(chatModel);
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
    return getListResponse(activitiesPrompt);
  }

  @GetMapping("/environments")
  public ResponseEntity<List<String>> getEnvironments() {
    return getListResponse(environmentsPrompt);
  }

  @GetMapping("/instruments")
  public ResponseEntity<List<String>> getInstruments() {
    return getListResponse(instrumentsPrompt);
  }

  @GetMapping("/foods")
  public ResponseEntity<List<String>> getFoods() {
    return getListResponse(foodsPrompt);
  }

  @GetMapping("/colors")
  public ResponseEntity<List<String>> getColors() {
    List<String> colors = Arrays.stream(Color.values())
        .map(Color::name)
        .toList();
    return ResponseEntity.ok(colors);
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }
}

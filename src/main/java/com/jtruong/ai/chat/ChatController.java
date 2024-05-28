package com.jtruong.ai.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class ChatController extends BaseChatController {
  private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

  @Value("classpath:/prompts/breeds.st")
  private Resource breedsPrompt;

  public ChatController(ChatClient chatClient) {
    super(chatClient);
  }

  @GetMapping("/chat")
  public String getPrompt(@RequestParam(value = "prompt") String prompt) {
    PromptTemplate promptTemplate = new PromptTemplate(prompt);
    Prompt chatPrompt = promptTemplate.create();

    ChatResponse response = callAndLogMetadata(chatPrompt);
    return response.getResult().getOutput().getContent();
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }
}

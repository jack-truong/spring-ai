package com.jtruong.ai.chat;

import com.jtruong.ai.prompts.ListPromptParser;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public abstract class BaseChatController {
  private final ChatClient chatClient;

  protected BaseChatController(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  protected abstract Logger getLogger();

  protected ResponseEntity<List<String>> getListResponse(Resource resource) {
    ListPromptParser listPromptParser = new ListPromptParser(resource);

    ChatResponse response = callAndLogMetadata(listPromptParser.getPrompt());
    return ResponseEntity.ok(listPromptParser.parse(response.getResult().getOutput().getContent()));
  }

  protected ChatResponse callAndLogMetadata(Prompt prompt) {
    ChatResponse response = chatClient.call(prompt);
    getLogger().info("Request: {}, Usage: {}", ServletUriComponentsBuilder.fromCurrentRequest().build(), response.getMetadata().getUsage());
    return response;
  }
}

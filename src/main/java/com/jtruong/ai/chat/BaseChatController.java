package com.jtruong.ai.chat;

import org.slf4j.Logger;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public abstract class BaseChatController {
  private final ChatClient chatClient;

  protected BaseChatController(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  protected abstract Logger getLogger();

  protected ChatResponse callAndLogMetadata(Prompt prompt) {
    ChatResponse response = chatClient.call(prompt);
    getLogger().info("Request: {}, Usage: {}", ServletUriComponentsBuilder.fromCurrentRequest().build(), response.getMetadata().getUsage());
    return response;
  }
}

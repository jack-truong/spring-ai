package com.jtruong.ai.chat;

import org.slf4j.Logger;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;

public abstract class BaseChatController {
  private final ChatClient chatClient;

  protected BaseChatController(ChatClient chatClient) {
    this.chatClient = chatClient;
  }

  protected abstract Logger getLogger();

  protected ChatResponse callAndLogMetadata(Prompt prompt) {
    ChatResponse response = chatClient.call(prompt);
    getLogger().info("Usage: {}", response.getMetadata().getUsage());
    return response;
  }
}

package com.jtruong.ai.chat;

import com.jtruong.ai.prompts.ListPromptConverter;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public abstract class BaseChatController {
  protected final ChatModel chatModel;

  protected BaseChatController(ChatModel chatModel) {
    this.chatModel = chatModel;
  }

  protected abstract Logger getLogger();

  protected ResponseEntity<List<String>> getListResponse(Resource resource) {
    ListPromptConverter listPromptConverter = new ListPromptConverter(resource);

    ChatResponse response = callAndLogMetadata(listPromptConverter.getPrompt());
    return ResponseEntity.ok(listPromptConverter.convert(response.getResult().getOutput().getContent()));
  }

  protected ChatResponse callAndLogMetadata(Prompt prompt) {
    ChatResponse response = chatModel.call(prompt);
    getLogger().info("Request: {}, Usage: {}", ServletUriComponentsBuilder.fromCurrentRequest().build(), response.getMetadata().getUsage());
    return response;
  }
}

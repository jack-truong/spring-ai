package com.jtruong.ai.chat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.test.mock.mockito.MockBean;

public abstract class BaseChatControllerTest {
  @MockBean
  protected ChatClient chatClient;

  protected void setupMockChatResponse(String prompt, String result) {
    ChatResponse response = mock(ChatResponse.class);
    Generation generation = mock(Generation.class);
    AssistantMessage assistantMessage = mock(AssistantMessage.class);
    ChatResponseMetadata chatResponseMetadata = mock(ChatResponseMetadata.class);

    when(response.getResult()).thenReturn(generation);
    when(response.getMetadata()).thenReturn(chatResponseMetadata);
    when(chatResponseMetadata.getUsage()).thenReturn(new EmptyUsage());
    when(generation.getOutput()).thenReturn(assistantMessage);
    when(assistantMessage.getContent()).thenReturn(result);
    when(chatClient.call(new Prompt(prompt))).thenReturn(response);
  }

  protected void setupMockChatResponse(String result) {
    ChatResponse response = mock(ChatResponse.class);
    Generation generation = mock(Generation.class);
    AssistantMessage assistantMessage = mock(AssistantMessage.class);
    ChatResponseMetadata chatResponseMetadata = mock(ChatResponseMetadata.class);

    when(response.getResult()).thenReturn(generation);
    when(response.getMetadata()).thenReturn(chatResponseMetadata);
    when(chatResponseMetadata.getUsage()).thenReturn(new EmptyUsage());
    when(generation.getOutput()).thenReturn(assistantMessage);
    when(assistantMessage.getContent()).thenReturn(result);
    when(chatClient.call(Mockito.any(Prompt.class))).thenReturn(response);
  }
}

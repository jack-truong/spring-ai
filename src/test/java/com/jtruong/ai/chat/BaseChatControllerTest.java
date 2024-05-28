package com.jtruong.ai.chat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    ChatResponse mockResponse = mock(ChatResponse.class);
    Generation mockGeneration = mock(Generation.class);
    AssistantMessage mockAssistantMessage = mock(AssistantMessage.class);
    ChatResponseMetadata mockChatResponseMetadata = mock(ChatResponseMetadata.class);

    when(mockResponse.getResult()).thenReturn(mockGeneration);
    when(mockResponse.getMetadata()).thenReturn(mockChatResponseMetadata);
    when(mockChatResponseMetadata.getUsage()).thenReturn(new EmptyUsage());
    when(mockGeneration.getOutput()).thenReturn(mockAssistantMessage);
    when(mockAssistantMessage.getContent()).thenReturn(result);
    when(chatClient.call(new Prompt(prompt))).thenReturn(mockResponse);
  }

}

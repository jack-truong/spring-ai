package com.jtruong.ai.chat;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.Generation;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerIT extends BaseChatControllerTest {
  @Autowired
  private TestRestTemplate template;

  @Test
  public void getHello() throws Exception {
    // given
    setupMockChatResponse("hello", "greetings");

    // when
    ResponseEntity<String> response = template.getForEntity("/ai/chat?prompt=hello", String.class);

    // then
    assertThat(response.getBody()).isEqualTo("greetings");
  }
}

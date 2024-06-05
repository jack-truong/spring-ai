package com.jtruong.ai.chat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerIT extends BaseChatControllerTest {
  @Autowired
  private TestRestTemplate template;

  @Test
  public void testPrompt() throws Exception {
    // given
    setupMockChatResponse("hello", "greetings");

    // when
    ResponseEntity<String> response = template.getForEntity("/ai/chat?prompt=hello", String.class);

    // then
    assertThat(response.getBody()).isEqualTo("greetings");
  }

  @Test
  public void testPromptWithException() throws Exception {
    // given
    when(chatModel.call(Mockito.any(Prompt.class))).thenThrow(new RestClientException(""));

    // when
    ResponseEntity<String> response = template.getForEntity("/ai/chat?prompt=hello", String.class);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

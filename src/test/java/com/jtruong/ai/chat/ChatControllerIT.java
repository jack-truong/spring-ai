package com.jtruong.ai.chat;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.image.ImageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatControllerIT {

  @Autowired
  private TestRestTemplate template;

  @MockBean
  private ChatClient chatClient;

  @MockBean
  private ImageClient imageClient;

  @Test
  public void getHello() throws Exception {
    // given
    when(chatClient.call("foo")).thenReturn("bar");

    // when
    ResponseEntity<String> response = template.getForEntity("/ai/chat?prompt=foo", String.class);

    // then
    assertThat(response.getBody()).isEqualTo("bar");
  }
}

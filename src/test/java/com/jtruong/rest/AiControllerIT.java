package com.jtruong.rest;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AiControllerIT {

	@Autowired
	private TestRestTemplate template;

    @MockBean
    private ChatClient chatClient;

    @Test
    public void getHello() throws Exception {
        when(chatClient.call("foo")).thenReturn("bar");
        ResponseEntity<String> response = template.getForEntity("/ai/prompt?prompt=foo", String.class);
        assertThat(response.getBody()).isEqualTo("bar");
    }
}

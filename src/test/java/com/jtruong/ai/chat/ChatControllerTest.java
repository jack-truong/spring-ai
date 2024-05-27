package com.jtruong.ai.chat;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(ChatController.class)
public class ChatControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ChatClient chatClient;

	@Test
	public void chat() throws Exception {
		// given
		when(chatClient.call("foo")).thenReturn("bar");

		// when
		ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/ai/chat?prompt=foo").accept(MediaType.APPLICATION_JSON));

		// then
		result
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("bar")));
	}
}
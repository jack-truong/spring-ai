package com.jtruong.ai.chat;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestClientException;

@WebMvcTest(ChatController.class)
public class ChatControllerTest extends BaseChatControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void chat() throws Exception {
		// given
		setupMockChatResponse("hello", "greetings");

		// when
		ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/ai/chat?prompt=hello").accept(MediaType.APPLICATION_JSON));

		// then
		result
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("greetings")));
	}

	@Test
	public void testPromptWithException() throws Exception {
		// given
		when(chatModel.call(Mockito.any(Prompt.class))).thenThrow(new RestClientException(""));

		// when
		ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/ai/chat?prompt=hello").accept(MediaType.APPLICATION_JSON));

		// then
		result
				.andExpect(status().is5xxServerError());
	}
}

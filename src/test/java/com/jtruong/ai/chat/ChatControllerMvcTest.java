package com.jtruong.ai.chat;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.image.ImageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatControllerMvcTest extends BaseChatControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void prompt() throws Exception {
		// given
		setupMockChatResponse("hello", "greetings");

		// when
		ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/ai/chat?prompt=hello").accept(MediaType.APPLICATION_JSON));

		// then
		result
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo("greetings")));
	}
}

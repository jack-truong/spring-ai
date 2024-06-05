package com.jtruong.ai.chat;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestClientException;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatControllerMvcTest extends BaseChatControllerTest {

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
	public void activities() throws Exception {
		runListTest(List.of("football", "tennis"), "/ai/activities");
	}

	@Test
	public void environments() throws Exception {
		// given
		runListTest(List.of("in desert", "in space"), "/ai/environments");
	}

	@Test
	public void instruments() throws Exception {
		// given
		runListTest(List.of("banjo", "drums", "guitar"), "/ai/instruments");
	}

	@Test
	public void foods() throws Exception {
		// given
		runListTest(List.of("apple", "grapes", "tacos"), "/ai/foods");
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

	private void runListTest(List<String> items, String path) throws Exception {
		// given
		setupMockChatResponse(String.join(",", items));

		// when
		ResultActions result = mvc.perform(
				MockMvcRequestBuilders.get(path).accept(MediaType.APPLICATION_JSON));

		// then
		result
				.andExpect(status().isOk())
				.andExpect(content().string(equalTo(new ObjectMapper().writeValueAsString(items))));
	}
}

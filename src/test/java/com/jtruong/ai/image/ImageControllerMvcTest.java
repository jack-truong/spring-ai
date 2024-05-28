package com.jtruong.ai.image;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageGenerationMetadata;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestClientException;

@SpringBootTest
@AutoConfigureMockMvc
public class ImageControllerMvcTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  protected ImageClient imageClient;

  @Test
  public void image() throws Exception {
    // given
    setupMockImageResponse("hello", "greetings");

    // when
    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get("/ai/image?prompt=hello").accept(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo("greetings")));
  }

  @Test
  public void testPromptWithException() throws Exception {
    // given
    when(imageClient.call(Mockito.any(ImagePrompt.class))).thenThrow(new RestClientException(""));

    // when
    ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/ai/image?prompt=hello").accept(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().is5xxServerError());
  }

  private void setupMockImageResponse(String prompt, String result) {
    ImageResponse imageResponse = mock(ImageResponse.class);
    ImageGeneration imageGeneration = mock(ImageGeneration.class);
    ImageGenerationMetadata imageGenerationMetadata = mock(ImageGenerationMetadata.class);

    when(imageGeneration.getMetadata()).thenReturn(imageGenerationMetadata);
    when(imageResponse.getResult()).thenReturn(imageGeneration);
    when(imageClient.call(new ImagePrompt(prompt))).thenReturn(imageResponse);
  }
}
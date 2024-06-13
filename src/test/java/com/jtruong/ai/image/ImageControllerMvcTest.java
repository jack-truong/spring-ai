package com.jtruong.ai.image;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtruong.ai.chat.BaseChatControllerTest;
import com.jtruong.ai.chat.image.Images.ImageAnalysisResponse;
import com.jtruong.ai.chat.image.Images.ImageInfo;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.metadata.OpenAiImageGenerationMetadata;
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
public class ImageControllerMvcTest extends BaseChatControllerTest {

  @Autowired
  private MockMvc mvc;

  @MockBean
  protected ImageModel imageModel;

  @Test
  public void creation() throws Exception {
    // given
    String imageUrl = "https://imagepath.com";
    String revisedPrompt = "This is the revised prompt";
    setupMockImageResponse(imageUrl, revisedPrompt);

    ImageInfo expected = new ImageInfo(revisedPrompt, imageUrl, null);

    // when
    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get("/ai/image/creation?prompt=hello")
            .accept(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo(new ObjectMapper().writeValueAsString(expected))));
  }

  @Test
  public void analysis() throws Exception {
    // given
    ImageAnalysisResponse expectedResponse = new ImageAnalysisResponse(
        List.of(
            "Image contains a dog", "There's a dog"
        ),
        "The dog is panting"
    );

    setupMockChatResponse(new ObjectMapper().writeValueAsString(expectedResponse));

    // when
    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.post("/ai/image/analysis")
            .content(new ObjectMapper().writeValueAsString(
                Map.of("prompt", "this is the prompt", "b64Json",
                    Base64.getEncoder().encodeToString("the image".getBytes()))
            )).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(
            content().string(equalTo(new ObjectMapper().writeValueAsString(expectedResponse))));
  }

  @Test
  public void testPromptWithException() throws Exception {
    // given
    when(imageModel.call(Mockito.any(ImagePrompt.class))).thenThrow(new RestClientException(""));

    // when
    ResultActions result = mvc.perform(MockMvcRequestBuilders.get("/ai/image/creation?prompt=hello")
        .accept(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().is5xxServerError());
  }

  private void setupMockImageResponse(String url, String revisedPrompt) {
    ImageResponse imageResponse = mock(ImageResponse.class);
    ImageGeneration imageGeneration = mock(ImageGeneration.class);
    Image image = mock(Image.class);
    OpenAiImageGenerationMetadata imageGenerationMetadata = mock(
        OpenAiImageGenerationMetadata.class);

    when(imageGeneration.getOutput()).thenReturn(image);
    when(image.getUrl()).thenReturn(url);
    when(imageGenerationMetadata.getRevisedPrompt()).thenReturn(revisedPrompt);
    when(imageGeneration.getMetadata()).thenReturn(imageGenerationMetadata);
    when(imageResponse.getResult()).thenReturn(imageGeneration);
    when(imageModel.call(Mockito.any(ImagePrompt.class))).thenReturn(imageResponse);
  }
}

package com.jtruong.ai.chat.dog;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtruong.ai.chat.BaseChatControllerTest;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileCopyUtils;

@SpringBootTest
@AutoConfigureMockMvc
class DogChatControllerMvcTest extends BaseChatControllerTest {

  @Autowired
  private MockMvc mvc;

  @Value("classpath:/prompts/dogImage.st")
  private Resource dogImagePrompt;

  @Test
  void getBreeds() throws Exception {
    // given
    List<String> breeds = List.of("german shepherd", "pug");
    setupMockChatResponse(String.join(",", breeds));

    // when
    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get("/ai/dog/breeds").accept(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo(new ObjectMapper().writeValueAsString(breeds))));
  }

  @Test
  void getBreedCharacteristics() throws Exception {
    // given
    List<String> characteristics = Arrays.stream(Characteristic.values())
        .map(Characteristic::name)
        .toList();
    setupMockChatResponse(String.join(",", characteristics));

    // when
    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get("/ai/dog/characteristics").accept(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(
            content().string(equalTo(new ObjectMapper().writeValueAsString(characteristics))));
  }

  @Test
  void getBreedDetails() throws Exception {
    // given
    BreedInfo breedInfo = new BreedInfo(
        "pug",
        List.of(
            new CharacteristicInfo(Characteristic.Origin.name(), "Japan"),
            new CharacteristicInfo(Characteristic.Lifespan.name(), "12-15 years")
        )
    );
    String expectedBreedInfoString = new ObjectMapper().writeValueAsString(breedInfo);
    setupMockChatResponse(String.join(",", expectedBreedInfoString));

    // when
    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get("/ai/dog/details?breed=pug&characteristics=Origin,Cost")
            .accept(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(
            content().string(equalTo(expectedBreedInfoString)));
  }

  @Test
  void getBreedDetailsWithBadCharacteristicsPassedIn() throws Exception {
    // when
    MvcResult mvcResult = mvc.perform(
        MockMvcRequestBuilders.get("/ai/dog/details?breed=pug&characteristics=foo")
            .accept(MediaType.APPLICATION_JSON)).andReturn();

    // then
    assertThat(mvcResult.getResponse().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  void getImagePrompt() throws Exception {
    // given
    String dogImageTemplate;
    try (Reader reader = new InputStreamReader(dogImagePrompt.getInputStream(), UTF_8)) {
      dogImageTemplate = FileCopyUtils.copyToString(reader);
    }
    dogImageTemplate = dogImageTemplate
        .replace("{breed}", "pug")
        .replace("{environment}", "space")
        .replace("{activity}", "flying")
        .replace("{instrument}", "drums")
        .replace("{food}", "yogurt")
        .replace("{color}", "Orange")
    ;

    // when
    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get(
                "/ai/dog/image-prompt?breed=pug&environment=space&activity=flying&instrument=drums&food=yogurt&color=Orange")
            .accept(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo(dogImageTemplate)));
  }
}
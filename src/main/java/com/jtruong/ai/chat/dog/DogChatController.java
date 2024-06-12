package com.jtruong.ai.chat.dog;

import com.jtruong.ai.chat.BaseChatController;
import com.jtruong.ai.chat.Color;
import com.jtruong.ai.prompts.BeanPromptConverter;
import com.jtruong.ai.prompts.ListPromptConverter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/dog")
public class DogChatController extends BaseChatController {

  private static final Logger logger = LoggerFactory.getLogger(DogChatController.class);

  @Value("classpath:/prompts/breeds.st")
  private Resource breedsPrompt;

  @Value("classpath:/prompts/breedCharacteristics.st")
  private Resource breedCharacteristicsPrompt;

  @Value("classpath:/prompts/dogImage.st")
  private Resource dogImagePrompt;

  public DogChatController(ChatModel chatModel) {
    super(chatModel);
  }

  @GetMapping("/breeds")
  public ResponseEntity<List<String>> getBreeds() {
    ListPromptConverter listPromptConverter = new ListPromptConverter(breedsPrompt);

    ChatResponse response = callAndLogMetadata(listPromptConverter.getPrompt());
    return ResponseEntity.ok(listPromptConverter.convert(response.getResult().getOutput().getContent()));
  }

  @GetMapping("/characteristics")
  public ResponseEntity<List<String>> getBreedCharacteristics() {
    List<String> characteristics = Arrays.stream(Characteristic.values())
        .map(Characteristic::name)
        .toList();
    return ResponseEntity.ok(characteristics);
  }

  @GetMapping("/details")
  public ResponseEntity<BreedInfo> getBreedDetails(
      @RequestParam(value = "breed") String breed,
      @RequestParam(value = "characteristics") List<String> characteristics
  ) {
    validateCharacteristics(characteristics);

    BeanPromptConverter<BreedInfo> beanPromptConverter = new BeanPromptConverter<>(BreedInfo.class,
        breedCharacteristicsPrompt,
        Map.of(
            "breed", breed,
            "characteristics", characteristics
        )
    );
    ChatResponse response = callAndLogMetadata(beanPromptConverter.getPrompt());

    return ResponseEntity.ok(beanPromptConverter.convert(response.getResult().getOutput().getContent()));
  }

  @GetMapping("/image-prompt")
  public ResponseEntity<String> getImagePrompt(
      @RequestParam(value = "breed") String breed,
      @RequestParam(value = "environment") String environment,
      @RequestParam(value = "activity") String activity,
      @RequestParam(value = "instrument") String instrument,
      @RequestParam(value = "food") String food,
      @RequestParam(value = "color") String color
  ) {
    color = color.equals(Color.Default.name()) ? "" : color;
    PromptTemplate promptTemplate = new PromptTemplate(dogImagePrompt);
    Prompt prompt = promptTemplate.create(
        Map.of(
            "breed", breed,
            "environment", environment,
            "activity", activity,
            "instrument", instrument,
            "food", food,
            "color", color
        )
    );

    return ResponseEntity.ok(prompt.getContents());
  }

  private void validateCharacteristics(List<String> characteristics) {
    for (String characteristic : characteristics) {
      Characteristic.valueOf(characteristic);
    }
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }
}

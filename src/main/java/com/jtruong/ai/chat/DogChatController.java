package com.jtruong.ai.chat;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.ListOutputParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class DogChatController extends BaseChatController {
  private static final Logger logger = LoggerFactory.getLogger(DogChatController.class);

  @Value("classpath:/prompts/breeds.st")
  private Resource breedsPrompt;

  @Value("classpath:/prompts/breedCharacteristics.st")
  private Resource breedCharacteristicsPrompt;

  public DogChatController(ChatClient chatClient) {
    super(chatClient);
  }

  @GetMapping("/breeds")
  public ResponseEntity<List<String>> getBreeds() {
    ListOutputParser listOutputParser = new ListOutputParser(new DefaultConversionService());
    PromptTemplate promptTemplate = new PromptTemplate(breedsPrompt);
    Prompt prompt = promptTemplate.create(Map.of("format", listOutputParser.getFormat()));

    ChatResponse response = callAndLogMetadata(prompt);
    return ResponseEntity.ok(listOutputParser.parse(response.getResult().getOutput().getContent()));
  }

  @GetMapping("/breeds/info")
  public ResponseEntity<String> getBreedInfo(
      @RequestParam(value = "breed") String breed,
      @RequestParam(value = "characteristics") List<String> characteristics
  ) {
    validateCharacteristics(characteristics);
    PromptTemplate promptTemplate = new PromptTemplate(breedCharacteristicsPrompt);
    Prompt prompt = promptTemplate.create(Map.of("breed", breed, "characteristics", characteristics));

    ChatResponse response = callAndLogMetadata(prompt);
    return ResponseEntity.ok(response.getResult().getOutput().getContent());
  }

  private void validateCharacteristics(List<String> characteristics) {
    for (String characteristic : characteristics) {
      if (Characteristic.fromName(characteristic).isEmpty()) {
        throw new IllegalArgumentException("Unknown characteristic specified: " + characteristic);
      }
    }
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }
}

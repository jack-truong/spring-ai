package com.jtruong.ai.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.image.ImageGenerationMetadata;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.metadata.OpenAiImageGenerationMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class ImageController {

  private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

  private final ImageClient imageClient;

  public ImageController(ImageClient imageClient) {
    this.imageClient = imageClient;
  }

  @GetMapping("/image")
  public ResponseEntity<ImageInfo> getImage(@RequestParam(value = "prompt") String prompt,
      @RequestParam(value = "useB64Json", defaultValue = "false") Boolean useB64Json) {
    ImageResponse response = callAndLogMetadata(
        new ImagePrompt(prompt,
            OpenAiImageOptions.builder()
                .withResponseFormat(useB64Json ? "b64_json" : "url")
                .build()
        )
    );
    Image output = response.getResult().getOutput();
    String metadata = getImageMetadata(response.getResult().getMetadata());
    return ResponseEntity.ok(new ImageInfo(metadata, output.getUrl(), output.getB64Json()));
  }

  private static String getImageMetadata(ImageGenerationMetadata metadata) {
    // This is knowingly not ideal, but want to extract the prompt from the OpenAI revisedPrompt metadata and not return the whole string
    return metadata instanceof OpenAiImageGenerationMetadata ? ((OpenAiImageGenerationMetadata)metadata).getRevisedPrompt() : metadata.toString();
  }

  private ImageResponse callAndLogMetadata(ImagePrompt prompt) {
    ImageResponse response = imageClient.call(prompt);
    logger.info("Prompt metadata: {}", response.getResult().getMetadata().toString());
    return response;
  }
}

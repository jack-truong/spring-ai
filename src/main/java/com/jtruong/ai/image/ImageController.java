package com.jtruong.ai.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
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
  public String getImage(@RequestParam(value = "prompt") String prompt,
      @RequestParam(value = "useB64Json", defaultValue = "false") Boolean useB64Json) {
    ImageResponse response = callAndLogMetadata(
        new ImagePrompt(prompt,
            OpenAiImageOptions.builder()
                .withResponseFormat(useB64Json ? "b64_json" : "url")
                .build()
        )
    );
    return response.toString();
  }

  private ImageResponse callAndLogMetadata(ImagePrompt prompt) {
    ImageResponse response = imageClient.call(prompt);
    logger.info("Prompt metadata: {}", response.getResult().getMetadata().toString());
    return response;
  }
}

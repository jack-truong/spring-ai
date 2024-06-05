package com.jtruong.ai.image;

import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.Media;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageGenerationMetadata;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.metadata.OpenAiImageGenerationMetadata;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/ai/image")
public class ImageController {

  private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

  private final ImageModel imageModel;
  private final ChatModel chatModel;

  public ImageController(ImageModel imageModel, ChatModel chatModel) {
    this.imageModel = imageModel;
    this.chatModel = chatModel;
  }

  @GetMapping("/creation")
  public ResponseEntity<ImageInfo> createImage(@RequestParam(value = "prompt") String prompt,
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

  @GetMapping("/analysis")
  public ResponseEntity<String> analyzeImage(@RequestParam(value = "b64Json") String b64Json) {
    try {
      File file = new File("/Users/jtruong/Downloads/jordan.jpg");
      byte[] imageData = FileUtils.readFileToByteArray(file);

      // todo, use other Media constructor
      UserMessage userMessage = new UserMessage("What's in this image",
          List.of(new Media(MimeTypeUtils.IMAGE_JPEG, imageData)));
      ChatResponse call = chatModel.call(new Prompt(userMessage));
      return ResponseEntity.ok(call.getResult().getOutput().getContent());
    } catch (IOException e) {
      throw new RestClientException("An error occurred while reading image", e);
    }
  }

  private static String getImageMetadata(ImageGenerationMetadata metadata) {
    // This is knowingly not ideal, but want to extract the prompt from the OpenAI revisedPrompt metadata and not return the whole string
    return metadata instanceof OpenAiImageGenerationMetadata ? ((OpenAiImageGenerationMetadata)metadata).getRevisedPrompt() : metadata.toString();
  }

  private ImageResponse callAndLogMetadata(ImagePrompt prompt) {
    ImageResponse response = imageModel.call(prompt);
    logger.info("Request: {}, Prompt metadata: {}", ServletUriComponentsBuilder.fromCurrentRequest().build(), getImageMetadata(response.getResult().getMetadata()));
    return response;
  }
}

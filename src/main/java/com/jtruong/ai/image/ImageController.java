package com.jtruong.ai.image;

import com.jtruong.ai.chat.BaseChatController;
import com.jtruong.ai.image.Images.ImageAnalysisRequest;
import com.jtruong.ai.image.Images.ImageAnalysisResponse;
import com.jtruong.ai.image.Images.ImageInfo;
import com.jtruong.ai.prompts.BeanPromptConverter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
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
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/ai/image")
public class ImageController extends BaseChatController {

  private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

  private final ImageModel imageModel;

  public ImageController(ImageModel imageModel, ChatModel chatModel) {
    super(chatModel);
    this.imageModel = imageModel;
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

  @PostMapping(value = "/analysis", consumes = "application/json")
  public ResponseEntity<ImageAnalysisResponse> analyzeImage(@RequestBody ImageAnalysisRequest imageRequest) {
    byte[] decodedBytes = Base64.getDecoder().decode(imageRequest.b64Json());

    String promptWithFormat = String.format("%s {format}", imageRequest.prompt());

    BeanPromptConverter<ImageAnalysisResponse> beanPromptConverter = new BeanPromptConverter<>(ImageAnalysisResponse.class, new ByteArrayResource(promptWithFormat.getBytes()), Map.of());
    UserMessage userMessage = new UserMessage(beanPromptConverter.getPrompt().getContents(),
        List.of(new Media(MimeTypeUtils.IMAGE_JPEG, new ByteArrayResource(decodedBytes))));

    ChatResponse call = callAndLogMetadata(new Prompt(userMessage));
    return ResponseEntity.ok(beanPromptConverter.convert(call.getResult().getOutput().getContent()));
  }

  private static String getImageMetadata(ImageGenerationMetadata metadata) {
    // This is knowingly not ideal, but want to extract the prompt from the OpenAI revisedPrompt metadata and not return the whole string
    return metadata instanceof OpenAiImageGenerationMetadata
        ? ((OpenAiImageGenerationMetadata) metadata).getRevisedPrompt() : metadata.toString();
  }

  private ImageResponse callAndLogMetadata(ImagePrompt prompt) {
    ImageResponse response = imageModel.call(prompt);
    logger.info("Request: {}, Prompt metadata: {}",
        ServletUriComponentsBuilder.fromCurrentRequest().build(),
        getImageMetadata(response.getResult().getMetadata()));
    return response;
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }
}

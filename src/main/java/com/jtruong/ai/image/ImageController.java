package com.jtruong.ai.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageClient;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

@RestController
@RequestMapping("/ai")
public class ImageController {
    private static final Logger logger = LoggerFactory.getLogger(ImageController.class);

    private final ImageClient imageClient;

    public ImageController(ImageClient imageClient) {
      this.imageClient = imageClient;
    }

    @GetMapping("/image")
    public String getImage(@RequestParam(value = "prompt") String prompt) {
        ImageResponse response = callAndLogMetadata(new ImagePrompt(prompt));
        return response.toString();
    }

    @ExceptionHandler({RestClientException.class})
    public ProblemDetail handleException(RestClientException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
            "An error occurred trying to communicate with the AI endpoint.");
    }

    private ImageResponse callAndLogMetadata(ImagePrompt prompt) {
        ImageResponse response = imageClient.call(prompt);
        logger.info("Prompt metadata: {}", response.getResult().getMetadata().toString());
        return response;
    }
}

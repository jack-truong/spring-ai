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
public class ChatController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ChatClient chatClient;

    @Value("classpath:/prompts/breeds.st")
    private Resource breedsPrompt;

    public ChatController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/chat")
    public String getPrompt(@RequestParam(value = "prompt") String prompt) {
        PromptTemplate promptTemplate = new PromptTemplate(breedsPrompt);
        Prompt chatPrompt = promptTemplate.create();

        ChatResponse response = callAndLogMetadata(chatPrompt);
        return response.getResult().getOutput().getContent();
    }

    @GetMapping("/breeds")
    public ResponseEntity<List<String>> getBreeds() {
        ListOutputParser listOutputParser = new ListOutputParser(new DefaultConversionService());
        PromptTemplate promptTemplate = new PromptTemplate(breedsPrompt);
        Prompt prompt = promptTemplate.create(Map.of("format", listOutputParser.getFormat()));

        ChatResponse response = callAndLogMetadata(prompt);
        return ResponseEntity.ok(listOutputParser.parse(response.getResult().getOutput().getContent()));
    }

    private ChatResponse callAndLogMetadata(Prompt prompt) {
        ChatResponse response = chatClient.call(prompt);
        logger.info("Usage: {}", response.getMetadata().getUsage());
        return response;
    }
}

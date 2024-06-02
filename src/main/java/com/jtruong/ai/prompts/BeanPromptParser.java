package com.jtruong.ai.prompts;

import java.util.HashMap;
import java.util.Map;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.core.io.Resource;

public class BeanPromptParser<T> extends BeanOutputParser<T> {
  private final Resource prompt;
  private final Map<String, Object> promptMappings;

  public BeanPromptParser(Class<T> clazz, Resource prompt, Map<String, Object> promptMappings) {
    super(clazz);
    this.prompt = prompt;
    this.promptMappings = promptMappings;
  }

  public Prompt getPrompt() {
    PromptTemplate promptTemplate = new PromptTemplate(prompt);

    Map<String, Object> mappings = new HashMap<>(promptMappings);
    mappings.put("format", this.getFormat());
    return promptTemplate.create(mappings);
  }

  public Prompt getPrompt(OpenAiChatOptions chatOptions) {
    PromptTemplate promptTemplate = new PromptTemplate(prompt);

    Map<String, Object> mappings = new HashMap<>(promptMappings);
    mappings.put("format", this.getFormat());

    return new Prompt(promptTemplate.create(mappings).getInstructions(), chatOptions);
  }
}

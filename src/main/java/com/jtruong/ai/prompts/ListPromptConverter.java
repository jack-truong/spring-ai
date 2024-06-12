package com.jtruong.ai.prompts;

import java.util.Map;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;

public class ListPromptConverter extends ListOutputConverter {
  private final Resource prompt;

  public ListPromptConverter(Resource prompt) {
    super(new DefaultConversionService());
    this.prompt = prompt;
  }

  public Prompt getPrompt() {
    PromptTemplate promptTemplate = new PromptTemplate(prompt);
    return promptTemplate.create(Map.of("format", this.getFormat()));
  }
}

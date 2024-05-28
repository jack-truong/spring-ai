package com.jtruong.ai.prompts;

import java.util.Map;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.ListOutputParser;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.Resource;

public class ListPromptParser extends ListOutputParser {
  private final Resource prompt;

  public ListPromptParser(Resource prompt) {
    super(new DefaultConversionService());
    this.prompt = prompt;
  }

  public Prompt getPrompt() {
    PromptTemplate promptTemplate = new PromptTemplate(prompt);
    return promptTemplate.create(Map.of("format", this.getFormat()));
  }
}

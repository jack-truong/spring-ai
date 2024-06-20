package com.jtruong.ai.chat.db;

import java.io.IOException;
import java.util.Base64;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class DbSchemaImageLoader {
  @Value("classpath:/northwind_schema_diagram.png")
  private Resource northwindSchemaDiagram;

  public String load() throws IOException {
    byte[] fileContent = FileUtils.readFileToByteArray(northwindSchemaDiagram.getFile());
    return Base64.getEncoder().encodeToString(fileContent);
  }
}

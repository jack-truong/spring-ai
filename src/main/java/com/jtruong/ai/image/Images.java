package com.jtruong.ai.image;

import java.util.List;

public interface Images {
  record ImageInfo(String finalPrompt, String url, String b64Json) { }

  record Assertion(String assertion, String reasoning) {}

  record ImageAnalysisRequest(String prompt, String b64Json) {}
}

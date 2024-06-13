package com.jtruong.ai.chat.image;

import java.util.List;

public interface Images {
  record ImageInfo(String finalPrompt, String url, String b64Json) { }

  record ImageAnalysisRequest(String prompt, String b64Json) {}
  record ImageAnalysisResponse(List<String> verboseObservations, String finalConclusion) {}
}

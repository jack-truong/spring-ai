package com.jtruong.ai.rest;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class RestTemplateWrapper {
  public static <T> ResponseEntity<T> getForEntity(RestTemplate restTemplate, String url, Class<T> responseType,
      Map<String, ?> uriVariables) throws RestClientException {

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
    for (Map.Entry<String, ?> entry : uriVariables.entrySet()) {
      uriComponentsBuilder.queryParam(entry.getKey(), String.format("{%s}", entry.getKey()));
    }
    String urlTemplate = uriComponentsBuilder.encode().toUriString();

    return restTemplate.getForEntity(urlTemplate, responseType, uriVariables);
  }
}

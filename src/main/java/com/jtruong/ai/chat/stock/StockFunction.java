package com.jtruong.ai.chat.stock;

import com.jtruong.ai.chat.stock.Stocks.Request;
import com.jtruong.ai.chat.stock.Stocks.Response;
import com.jtruong.ai.chat.stock.Stocks.StockHistorical;
import com.jtruong.ai.rest.RestTemplateWrapper;
import java.util.Map;
import java.util.function.Function;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class StockFunction implements Function<Request, Response> {
  @Value("${spring.ai.stock.api-key}")
  private String stockApiKey;

  @Value("${spring.ai.stock.api-url}")
  private String stockApiUrl;

  private final RestTemplate restTemplate;

  public StockFunction(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public Response apply(Request request) {
    ResponseEntity<StockHistorical> results = RestTemplateWrapper.getForEntity(
        restTemplate,
        String.format("%s/historical-price-full/%s", stockApiUrl, request.symbol()),
        StockHistorical.class,
        Map.of(
            "apikey", stockApiKey,
            "from", request.startDate(),
            "to", request.endDate()
        )
    );
    return new Response(results.getBody());
  }
}

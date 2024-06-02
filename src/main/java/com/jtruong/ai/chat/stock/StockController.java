package com.jtruong.ai.chat.stock;

import com.jtruong.ai.chat.BaseChatController;
import com.jtruong.ai.chat.stock.Stocks.StockHistorical;
import com.jtruong.ai.rest.RestTemplateWrapper;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/ai/stock")
public class StockController extends BaseChatController {

  private static final Logger logger = LoggerFactory.getLogger(StockController.class);

  @Value("${spring.ai.stock.api-key}")
  private String stockApiKey;

  @Value("${spring.ai.stock.api-url}")
  private String stockApiUrl;

  @Value("classpath:/prompts/stocks.st")
  private Resource stocks;

  private final RestTemplate restTemplate;

  public StockController(ChatClient chatClient, RestTemplate restTemplate) {
    super(chatClient);
    this.restTemplate = restTemplate;
  }

  @GetMapping("/stocks")
  public ResponseEntity<List<String>> getStocks() {
    return getListResponse(stocks);
  }

  @GetMapping("/historical/{symbol}")
  public ResponseEntity<Stocks.StockHistorical> getHistorical(
      @PathVariable(value = "symbol") String symbol) {

    ResponseEntity<StockHistorical> results = RestTemplateWrapper.getForEntity(
        restTemplate,
        String.format("%s/historical-price-full/%s", stockApiUrl, symbol),
        StockHistorical.class,
        Map.of(
            "apikey", stockApiKey,
            "from", "2023-08-10",
            "to", "2023-09-10"
        )
    );
    return ResponseEntity.ok(results.getBody());
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }
}

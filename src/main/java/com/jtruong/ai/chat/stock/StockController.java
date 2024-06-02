package com.jtruong.ai.chat.stock;

import com.jtruong.ai.chat.BaseChatController;
import com.jtruong.ai.chat.stock.Stocks.StockHistorical;
import com.jtruong.ai.prompts.BeanPromptParser;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai/stock")
public class StockController extends BaseChatController {

  private static final Logger logger = LoggerFactory.getLogger(StockController.class);

  @Value("classpath:/prompts/stocks.st")
  private Resource stocksPrompt;

  @Value("classpath:/prompts/stocksHistorical.st")
  private Resource stocksHistoricalPrompt;

  public StockController(ChatClient chatClient) {
    super(chatClient);
  }

  @GetMapping("/stocks")
  public ResponseEntity<List<String>> getStocks() {
    return getListResponse(stocksPrompt);
  }

  @GetMapping("/historical/{symbol}")
  public ResponseEntity<StockHistorical> getHistorical(
      @PathVariable(value = "symbol") String symbol,
      @RequestParam(value = "numberOfDays") Integer numberOfDays
  ) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    BeanPromptParser<StockHistorical> beanPromptParser = new BeanPromptParser<>(
        StockHistorical.class,
        stocksHistoricalPrompt,
        Map.of(
            "symbol", symbol,
            "days", numberOfDays,
            "currentDate", simpleDateFormat.format(new Date())
        )
    );

    OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
        .withFunction("historicalStockPricesFunction")
        .build();
    ChatResponse response = callAndLogMetadata(beanPromptParser.getPrompt(chatOptions));

    return ResponseEntity.ok(beanPromptParser.parse(response.getResult().getOutput().getContent()));
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }
}

package com.jtruong.ai.chat.stock;

import com.jtruong.ai.chat.BaseChatController;
import com.jtruong.ai.chat.stock.Stocks.StockHistorical;
import com.jtruong.ai.prompts.BeanPromptParser;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
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
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  @Value("classpath:/prompts/stocks.st")
  private Resource stocksPrompt;

  @Value("classpath:/prompts/stocksHistorical.st")
  private Resource stocksHistoricalPrompt;

  @Value("classpath:/prompts/stocksHistoricalGains.st")
  private Resource stocksHistoricalGainsPrompt;

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
    BeanPromptParser<StockHistorical> beanPromptParser = new BeanPromptParser<>(
        StockHistorical.class,
        stocksHistoricalPrompt,
        Map.of(
            "symbol", symbol,
            "days", numberOfDays,
            "currentDate", DATE_TIME_FORMATTER.format(LocalDate.now())
        )
    );

    OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
        .withFunction("historicalStockPricesFunction")
        .build();
    ChatResponse response = callAndLogMetadata(beanPromptParser.getPrompt(chatOptions));

    return ResponseEntity.ok(beanPromptParser.parse(response.getResult().getOutput().getContent()));
  }

  @GetMapping("/historical/gains")
  public ResponseEntity<String> getHistoricalGains(
      @RequestParam(value = "symbols") List<String> symbols,
      @RequestParam(value = "numberOfDays") Integer numberOfDays
  ) {
    PromptTemplate promptTemplate = new PromptTemplate(stocksHistoricalGainsPrompt);

    OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
        .withFunction("historicalStockPricesFunction")
        .build();
    Prompt prompt = new Prompt(promptTemplate.create(Map.of(
        "symbols", String.join(",", symbols),
        "days", numberOfDays,
        "currentDate", DATE_TIME_FORMATTER.format(LocalDate.now())
    )).getInstructions(), chatOptions);
    ChatResponse response = callAndLogMetadata(prompt);

    return ResponseEntity.ok(response.getResult().getOutput().getContent());
  }

  @Override
  protected Logger getLogger() {
    return logger;
  }
}

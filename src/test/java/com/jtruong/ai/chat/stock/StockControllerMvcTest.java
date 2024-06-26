package com.jtruong.ai.chat.stock;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtruong.ai.chat.BaseChatControllerTest;
import com.jtruong.ai.chat.stock.Stocks.Stock;
import com.jtruong.ai.chat.stock.Stocks.StockGain;
import com.jtruong.ai.chat.stock.Stocks.StockHistorical;
import com.jtruong.ai.chat.stock.Stocks.StockRecommendation;
import com.jtruong.ai.chat.stock.Stocks.StockValue;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class StockControllerMvcTest extends BaseChatControllerTest {

  @Autowired
  private MockMvc mvc;

  @Test
  void getStocks() throws Exception {
    // given
    List<Stock> symbols = List.of(
        new Stock("AMZN", "Amazon"),
        new Stock("GOOGL", "Google")
    );
    String expectedResponse = new ObjectMapper().writeValueAsString(symbols);
    setupMockChatResponse(expectedResponse);

    // when
    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get("/ai/stock/stocks").accept(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo(expectedResponse)));
  }

  @Test
  void getHistorical() throws Exception {
    // given
    String symbol = "APPL";
    Stocks.StockHistorical historical = new StockHistorical(symbol, List.of(
        new StockValue("2024-01-01", 99.0),
        new StockValue("2024-01-02", 100.3)
    ));
    setupMockChatResponse(new ObjectMapper().writeValueAsString(historical));

    // when
    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get(String.format("/ai/stock/historical/%s?numberOfDays=30", symbol)).accept(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo(new ObjectMapper().writeValueAsString(historical))));
  }

  @Test
  void getHistoricalGains() throws Exception {
    // given
    StockRecommendation stockRecommendation = new StockRecommendation(
        "Pick APPL",
        List.of(
            new StockGain("APPL", new StockValue("2024-01-01", 99), new StockValue("2024-01-02", 101), new StockValue("2024-01-03", 101), 1.3),
            new StockGain("GOOGL", new StockValue("2024-01-01", 33), new StockValue("2024-01-02", 33.1), new StockValue("2024-01-03", 33.2), 0.12)
        )
    );
    setupMockChatResponse(new ObjectMapper().writeValueAsString(stockRecommendation));

    // when
    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get("/ai/stock/historical/gains?symbols=APPL,GOOGL&numberOfDays=30").accept(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo(new ObjectMapper().writeValueAsString(stockRecommendation))));
  }
}

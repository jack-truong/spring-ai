package com.jtruong.ai.chat.config;

import com.jtruong.ai.chat.db.DBFunction;
import com.jtruong.ai.chat.stock.StockFunction;
import com.jtruong.ai.chat.stock.Stocks.Request;
import com.jtruong.ai.chat.stock.Stocks.Response;
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class FunctionConfig {
  @Bean
  @Description("Get the historical stock prices for a specific stock symbol")
  public Function<Request, Response> historicalStockPricesFunction(RestTemplate restTemplate) {
    return new StockFunction(restTemplate);
  }

  @Bean
  @Description("Query the results from the database")
  public Function<com.jtruong.ai.chat.db.DbRecords.Request, com.jtruong.ai.chat.db.DbRecords.Response> queryResultsFromDatabase(
      JdbcClient jdbcClient) {
    return new DBFunction(jdbcClient);
  }
}

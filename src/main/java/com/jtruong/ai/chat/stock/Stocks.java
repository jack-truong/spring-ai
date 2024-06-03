package com.jtruong.ai.chat.stock;

import java.util.List;

public interface Stocks {
  record StockHistorical(String symbol, List<StockValue> historical) { }
  record StockValue(String date, double close) {}

  record Request(String symbol, String startDate, String endDate) {}
  record Response(StockHistorical stockHistorical) {}
}

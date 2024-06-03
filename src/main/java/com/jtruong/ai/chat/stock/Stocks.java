package com.jtruong.ai.chat.stock;

import java.util.List;

public interface Stocks {
  record StockHistorical(String symbol, List<StockValue> historical) { }
  record StockValue(String date, double close) {}

  record StockGain(String symbol, StockValue stockStart, StockValue stockEnd, double percentageGain) { }
  record StockRecommendation(String verboseRecommendation, List<StockGain> values) {}

  record Request(String symbol, String startDate, String endDate) {}
  record Response(StockHistorical stockHistorical) {}
}

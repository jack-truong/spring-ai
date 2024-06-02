package com.jtruong.ai.chat.stock;

import java.util.List;

public interface Stocks {
  record StockHistorical(String symbol, List<StockValue> historical) { }
  record StockValue(String date, String close) {}
}

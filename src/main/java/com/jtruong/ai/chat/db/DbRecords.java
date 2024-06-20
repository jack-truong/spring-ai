package com.jtruong.ai.chat.db;

import java.util.List;
import java.util.Map;

public interface DbRecords {
  record Customer(String firstName, String lastName) { }

  record Request(String query) {}
  record Response(List<Map<String, Object>> values) {}
  record DbResponse(String prettyPrintedQuery, List<Map<String, Object>> values) {}
}

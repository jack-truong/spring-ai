package com.jtruong.ai.chat.db;

import java.util.List;
import java.util.Map;

public interface DbRecords {
  record Request(String query) {}
  record Response(List<Map<String, Object>> values) {}
  record DbQueryResponse(String query, List<Map<String, Object>> values) {}
  record DbSchemaImageResponse(String b64Image){}
}

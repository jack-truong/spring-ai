package com.jtruong.ai.chat.db;

import com.jtruong.ai.chat.db.DbRecords.Request;
import com.jtruong.ai.chat.db.DbRecords.Response;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.jdbc.core.simple.JdbcClient;

public class DBFunction implements Function<Request, Response> {
  private final JdbcClient jdbcClient;

  public DBFunction(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  public Response apply(Request request) {
    List<Map<String, Object>> results = jdbcClient
        .sql(request.query())
        .query().listOfRows();
    return new Response(results);
  }
}

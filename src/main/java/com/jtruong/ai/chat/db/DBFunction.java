package com.jtruong.ai.chat.db;

import com.jtruong.ai.chat.db.DbRecords.Request;
import com.jtruong.ai.chat.db.DbRecords.Response;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.springframework.jdbc.core.simple.JdbcClient;

public class DBFunction implements Function<Request, Response> {
  private final JdbcClient jdbcClient;
  protected enum DisallowedWord {
    Drop,
    Create,
    Alter,
    Update,
    Insert,
    Delete,
    Truncate
  }

  public DBFunction(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  public Response apply(Request request) throws DisallowedUpdateException {
    validateQuery(request.query());
    List<Map<String, Object>> results = jdbcClient
        .sql(request.query())
        .query().listOfRows();
    return new Response(results);
  }

  private void validateQuery(String query) {
    String lowerCaseQuery = query.toLowerCase();
    for (DisallowedWord disallowedWord : DisallowedWord.values()) {
      if (lowerCaseQuery.contains(disallowedWord.name().toLowerCase())) {
        throw new DisallowedUpdateException(
            "Query contains disallowed update keyword: " + disallowedWord.name().toLowerCase());
      }
    }
  }

  public static class DisallowedUpdateException extends RuntimeException {
    public DisallowedUpdateException(String message) {
      super(message);
    }
  }
}

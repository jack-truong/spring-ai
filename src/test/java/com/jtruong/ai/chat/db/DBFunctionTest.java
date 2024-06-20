package com.jtruong.ai.chat.db;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtruong.ai.chat.db.DBFunction.DisallowedUpdateException;
import com.jtruong.ai.chat.db.DBFunction.DisallowedWord;
import com.jtruong.ai.chat.db.DbRecords.Request;
import com.jtruong.ai.chat.db.DbRecords.Response;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.ResultQuerySpec;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;

@SpringBootTest
class DBFunctionTest {
  @MockBean
  private JdbcClient jdbcClient;

  private DBFunction dbFunction;

  @BeforeEach
  void setUp() {
    dbFunction = new DBFunction(jdbcClient);
  }

  @Test
  public void testApply() throws JsonProcessingException {
    // given
    List<Map<String, Object>> results = List.of(
        Map.of("row_1_col_1", "value_1"),
        Map.of("row_2_col_1", "value_2")
    );

    String query = "the sql query";
    StatementSpec statementSpec = mock(StatementSpec.class);
    when(jdbcClient.sql(query)).thenReturn(statementSpec);
    ResultQuerySpec resultQuerySpec = mock(ResultQuerySpec.class);
    when(statementSpec.query()).thenReturn(resultQuerySpec);
    when(resultQuerySpec.listOfRows()).thenReturn(results);

    // when
    Response apply = dbFunction.apply(new Request(query));

    // then
    assertThat(apply.values()).isEqualTo(results);
  }

  @ParameterizedTest
  @EnumSource(DisallowedWord.class)
  public void throwsExceptionIfUpdateKeywordFound(DisallowedWord disallowedWord) throws JsonProcessingException {
    // given
    String query = "the sql query with invalid modification word: " + disallowedWord;

    // when:
    Throwable exception = assertThrows(DisallowedUpdateException.class, () -> dbFunction.apply(new Request(query)));

    // then:
    assertThat(exception.getMessage()).contains(disallowedWord.name().toLowerCase());
  }
}

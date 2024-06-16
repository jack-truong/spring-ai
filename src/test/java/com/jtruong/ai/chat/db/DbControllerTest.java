package com.jtruong.ai.chat.db;

import static com.jtruong.ai.chat.db.DbController.GET_CUSTOMERS_QUERY;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtruong.ai.chat.BaseChatControllerTest;
import com.jtruong.ai.chat.db.DbRecords.Customer;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.core.simple.JdbcClient.MappedQuerySpec;
import org.springframework.jdbc.core.simple.JdbcClient.StatementSpec;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class DbControllerTest extends BaseChatControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  private JdbcClient jdbcClient;

  @Test
  public void getCustomers() throws Exception {
    // given
    List<Customer> expectedCustomers = List.of(
        new Customer("Jim", "Smith"),
        new Customer("Susan", "Chan")
    );
    StatementSpec statementSpec = mock(StatementSpec.class);
    when(jdbcClient.sql(GET_CUSTOMERS_QUERY)).thenReturn(statementSpec);
    MappedQuerySpec<Customer> mappedQuerySpec = mock(MappedQuerySpec.class);
    when(statementSpec.query(Customer.class)).thenReturn(mappedQuerySpec);
    when(mappedQuerySpec.list()).thenReturn(expectedCustomers);

    // when
    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get("/ai/db/customers").accept(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(content().string(equalTo(new ObjectMapper().writeValueAsString(expectedCustomers))));
  }

  @Test
  public void dbQuery() throws Exception {
    // given
    String expectedResponse = new ObjectMapper().writeValueAsString(Map.of("answer", "value"));
    setupMockChatResponse(expectedResponse);

    // when
    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get("/ai/db/query?query=test").accept(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(content().string(expectedResponse));
  }
}

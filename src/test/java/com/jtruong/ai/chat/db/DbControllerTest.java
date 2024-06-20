package com.jtruong.ai.chat.db;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtruong.ai.chat.BaseChatControllerTest;
import com.jtruong.ai.chat.db.DbRecords.DbQueryResponse;
import com.jtruong.ai.chat.db.DbRecords.DbSchemaImageResponse;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class DbControllerTest extends BaseChatControllerTest {
  @Autowired
  private MockMvc mvc;

  @MockBean
  protected DbSchemaImageLoader dbSchemaImageLoader;

  @Test
  public void dbSchemaImage() throws Exception {
    // given
    String response = "the b64 encoded image";
    when(dbSchemaImageLoader.load()).thenReturn(response);

    // when
    ResultActions result = mvc.perform(
        MockMvcRequestBuilders.get("/ai/db/schema-image").accept(MediaType.APPLICATION_JSON));

    // then
    result
        .andExpect(status().isOk())
        .andExpect(content().string(new ObjectMapper().writeValueAsString(new DbSchemaImageResponse(response))));
  }

  @Test
  public void dbQuery() throws Exception {
    // given
    DbQueryResponse dbResponse = new DbQueryResponse(
        "the query",
        List.of(
            Map.of("row_1_col_1", "value_1"),
            Map.of("row_2_col_1", "value_2")
        )
    );
    String expectedResponse = new ObjectMapper().writeValueAsString(dbResponse);
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

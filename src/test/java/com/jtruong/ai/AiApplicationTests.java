package com.jtruong.ai;

import static org.assertj.core.api.Assertions.assertThat;

import com.jtruong.ai.chat.ChatController;
import com.jtruong.ai.chat.db.DbController;
import com.jtruong.ai.chat.dog.DogChatController;
import com.jtruong.ai.chat.image.ImageController;
import com.jtruong.ai.chat.stock.StockController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class AiApplicationTests {

	@Autowired
	private ChatController chatController;

	@Autowired
	private ImageController imageController;

  @Autowired
  private DogChatController dogChatController;

  @Autowired
  private StockController stockController;

  @Autowired
  private DbController dbController;

	@Test
	void contextLoads() {
		assertThat(chatController).isNotNull();
		assertThat(imageController).isNotNull();
		assertThat(dogChatController).isNotNull();
		assertThat(stockController).isNotNull();
		assertThat(dbController).isNotNull();
	}
}

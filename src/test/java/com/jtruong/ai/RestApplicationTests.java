package com.jtruong.ai;

import com.jtruong.ai.chat.ChatController;
import com.jtruong.ai.image.ImageController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class RestApplicationTests {

	@Autowired
	private ChatController chatController;

	@Autowired
	private ImageController imageController;

	@Test
	void contextLoads() {
		assertThat(chatController).isNotNull();
		assertThat(imageController).isNotNull();
	}
}

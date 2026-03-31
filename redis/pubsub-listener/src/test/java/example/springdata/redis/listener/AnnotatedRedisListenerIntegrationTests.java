/*
 * Copyright 2026-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.springdata.redis.listener;

import static org.assertj.core.api.Assertions.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.MessageHeaders;

/**
 * Integration tests for {@literal @RedisListener} annotation-driven endpoints.
 *
 * @author Christoph Strobl
 */
@SpringBootTest(classes = RedisListenerApplication.class)
@Import(RedisTestConfiguration.class)
class AnnotatedRedisListenerIntegrationTests {

	@Autowired StringRedisTemplate redisTemplate;
	@Autowired Messages messages;

	/**
	 * Publish a raw String message to the {@literal string-channel} channel and verify that the message is received by
	 * the {@link RedisListenerApplication.Listeners#processStringMessage(String, MessageHeaders) string listener}.
	 */
	@Test
	void shouldReceiveStringMessage() throws Exception {

		redisTemplate.convertAndSend("string-channel", "Hello, world!");

		String received = messages.capturedStrings().poll(5, TimeUnit.SECONDS);
		assertThat(received).isEqualTo("Hello, world!");
	}

	/**
	 * Publish a JSON String message to the {@literal person-channel} channel and verify that the payload is converted and
	 * processed by the {@link RedisListenerApplication.Listeners#processPersonMessage(Person) person listener}.
	 */
	@Test
	void shouldReceiveJsonPersonMessage() throws Exception {

		String json = "{\"firstname\":\"Homer\",\"lastname\":\"Simpson\"}";
		redisTemplate.convertAndSend("person-channel", json);

		Person received = messages.capturedPojos().poll(5, TimeUnit.SECONDS);
		assertThat(received).isNotNull();
		assertThat(received.firstname()).isEqualTo("Homer");
		assertThat(received.lastname()).isEqualTo("Simpson");
	}
}

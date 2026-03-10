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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.annotation.EnableRedisListeners;
import org.springframework.data.redis.annotation.RedisListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Spring Boot application for the Redis annotated listener sample.
 *
 * @author Christoph Strobl
 */
@SpringBootApplication
@EnableRedisListeners
public class RedisListenerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisListenerApplication.class, args);
	}

	/**
	 * Compontent tying together declarative redis listeners and the capturing {@link Messages}
	 * {@link RedisListenerApplication#messages() bean) that preserves received payload for verification in tests.
	 */
	@Component
	static class Listeners {

		private final Messages messages;

		Listeners(@Autowired Messages messages) {
			this.messages = messages;
		}

		/**
		 * Declarative channel listener capturing raw {@link String}s received from Redis via the {@literal string-channel}.
		 *
		 * @param data the payload received via the {@literal string-channel}.
		 * @param headers {@link MessageHeaders} when receiving the message.
		 */
		@RedisListener(topic = "string-channel")
		public void processStringMessage(@Payload String data, MessageHeaders headers) {

			System.out.printf("received message [%s] from [%s] at [%s].%n", data, headers.get("channel"), headers.getTimestamp());
			messages.capture(data);
		}

		/**
		 * Declarative channel listener capturing {@link Person} objects converted from JSON.
		 *
		 * @param person already converted {@link Person} object received from Redis via the {@literal person-channel}.
		 */
		@RedisListener(topic = "person-channel")
		public void processPersonMessage(Person person) {
			messages.capture(person);
		}
	}


	@Bean
	Messages messages() {
		return new Messages();
	}

	/**
	 * Provide required
	 * {@link RedisMessageListenerContainer} for annotation-driven endpoints
	 * {@link Listeners#processStringMessage(String, MessageHeaders)}} &
	 * {@link Listeners#processPersonMessage(Person)}.
	 *
	 * @param connectionFactory must not be {@literal null}.
	 * @return new instance of {@link RedisListenerApplication}.
	 */
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory) {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		return container;
	}
}

/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.springdata.redis.listener;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Just a little helper to capture the already converted payload received from Redis Pub/Sub.
 *
 * @author Christoph Strobl
 */
public class Messages {

	private final MessageCapturer<String> stringMessages = new MessageCapturer<>();
	private final MessageCapturer<Person> pojoMessages = new MessageCapturer<>();

	public BlockingQueue<String> capturedStrings() {
		return stringMessages.captured();
	}

	void capture(Person person) {
		pojoMessages.capture(person);
	}

	void capture(String string) {
		stringMessages.capture(string);
	}

	public BlockingQueue<Person> capturedPojos() {
		return pojoMessages.captured();
	}

	/**
	 * Holder for received messages.
	 */
	public static class MessageCapturer<T> {

		private final BlockingQueue<T> received = new LinkedBlockingQueue<>();

		void capture(T message) {
			received.add(message);
		}

		public BlockingQueue<T> captured() {
			return received;
		}
	}
}

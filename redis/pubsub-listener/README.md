# Spring Data Redis - Annotated Listener Example

This project demonstrates **declarative Redis Pub/Sub listeners**.

## Features

- `@RedisListener` on component methods to subscribe to Redis channels
- `@EnableRedisListeners` to activate the listener infrastructure
- Receiving **string** messages on a channel
- Receiving **JSON** payloads deserialized to domain types (e.g. `Person`)

## Requirements

- **Docker** (for running integration tests with Testcontainers).

## Running the tests

Integration tests publish messages to Redis channels and assert that the annotated listeners receive and process them. They use Testcontainers to start a Redis container, so Docker must be running.

```bash
cd redis/listener
mvn test
```

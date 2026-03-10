/*
 * Copyright 2025 the original author or authors.
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
package example.springdata.mongodb.bulk;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import example.springdata.mongodb.util.MongoContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.bulk.Bulk;
import org.springframework.data.mongodb.core.bulk.BulkWriteOptions;
import org.springframework.data.mongodb.core.bulk.BulkWriteResult;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

/**
 * Integration test demonstrating {@link MongoOperations#bulkWrite(Bulk, BulkWriteOptions)} with a combination of
 * insert and update operations in a single request.
 *
 * @author Christoph Strobl
 */
@Testcontainers
@DataMongoTest
class BulkWriteIntegrationTest {

	@Container
	private static MongoDBContainer mongoDBContainer = MongoContainers.getDefaultContainer();

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Autowired
	MongoOperations operations;

	@BeforeEach
	void setUp() {
		operations.dropCollection(Jedi.class);
	}

	@Test
	void bulkWriteInsertAndUpdateInOrder() {

		Bulk bulk = Bulk.create(builder -> builder.inCollection(Jedi.class, spec -> spec
				.insert(new Jedi("Luke", "Skywalker"))
				.insert(new Jedi("Leia", "Princess"))
				.updateOne(where("firstname").is("Leia"), new Update().set("lastname", "Organa")))
			.inCollection(Sith.class, spec -> spec.upsert(where("name").is("Darth Sidious"), Update.update("realName", "Palpatine")))
		);

		BulkWriteResult result = operations.bulkWrite(bulk, BulkWriteOptions.ordered());

		assertThat(result.acknowledged()).isTrue();
		assertThat(result.insertCount()).isEqualTo(2); // luke & leia
		assertThat(result.modifiedCount()).isOne(); // leia
		assertThat(result.upsertCount()).isOne(); // darth sidious

		assertThat(operations.findAll(Jedi.class))
			.extracting(Jedi::fullName)
			.containsExactlyInAnyOrder("Luke Skywalker", "Leia Organa");

		assertThat(operations.findAll(Sith.class))
			.extracting(Sith::name)
			.containsExactlyInAnyOrder("Darth Sidious");
	}
}

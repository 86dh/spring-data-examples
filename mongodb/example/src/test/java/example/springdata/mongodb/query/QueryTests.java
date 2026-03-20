/*
 * Copyright 2026-prese the original author or authors.
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
package example.springdata.mongodb.query;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.data.mongodb.core.query.Criteria.*;
import static org.springframework.data.mongodb.core.query.Query.*;

import example.springdata.mongodb.customer.Address;
import example.springdata.mongodb.customer.Customer;
import example.springdata.mongodb.unwrapping.ApplicationConfiguration;
import example.springdata.mongodb.util.MongoContainers;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.data.core.TypedPropertyPath;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

/**
 * Demonstrates type-safe property paths with {@link Sort}.
 *
 * @author Mark Paluch
 */
@Testcontainers
@ContextConfiguration(classes = ApplicationConfiguration.class)
@DataMongoTest
class QueryTests {

	@Container private static MongoDBContainer mongoDBContainer = MongoContainers.getDefaultContainer();

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Autowired MongoOperations operations;

	@BeforeEach
	void setUp() {
		operations.dropCollection(Customer.class);

		operations.insert(createCustomer("Walter", "White", "Albuquerque", "87104"));
		operations.insert(createCustomer("Jesse", "Pinkman", "Albuquerque", "87104"));
		operations.insert(createCustomer("Skyler", "White", "Albuquerque", "87116"));
	}

	@Test
	void queryAndSortWithStringPropertyPath() {

		Query query = query(where("lastname").is("White"));
		query.with(Sort.by("firstname"));

		List<Customer> result = operations.query(Customer.class).matching(query).all();

		assertThat(result).extracting(Customer::getFirstname).containsExactly("Skyler", "Walter");
	}

	@Test
	void queryAndSortWithTypedPropertyPath() {

		Query query = query(where(Customer::getLastname).is("White"));
		query.with(Sort.by(Customer::getFirstname));

		List<Customer> result = operations.query(Customer.class).matching(query).all();

		assertThat(result).extracting(Customer::getFirstname).containsExactly("Skyler", "Walter");
	}

	@Test
	void queryAndSortNestedWithStringPropertyPath() {

		Query query = query(where("address.city").is("Albuquerque").and("address.zipCode").is("87104"));
		query.with(Sort.by("lastname", "firstname"));

		List<Customer> result = operations.query(Customer.class).matching(query).all();

		assertThat(result).extracting(Customer::getFirstname).containsExactly("Jesse", "Walter");
	}

	@Test
	void queryAndSortNestedWithTypedPropertyPath() {

		Query query = query(where(TypedPropertyPath.of(Customer::getAddress).then(Address::getCity)).is("Albuquerque") //
				.and(TypedPropertyPath.of(Customer::getAddress).then(Address::getZipCode)).is("87104"));
		query.with(Sort.by(Customer::getLastname, Customer::getFirstname));

		List<Customer> result = operations.query(Customer.class).matching(query).all();

		assertThat(result).extracting(Customer::getFirstname).containsExactly("Jesse", "Walter");
	}

	private static Customer createCustomer(String firstname, String lastname, String city, String zipCode) {

		Customer customer = new Customer(firstname, lastname);
		customer.setAddress(new Address(new Point(0, 0), city, zipCode));
		return customer;
	}

}

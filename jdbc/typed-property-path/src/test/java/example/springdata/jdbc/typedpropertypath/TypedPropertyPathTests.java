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
package example.springdata.jdbc.typedpropertypath;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureJdbc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;

/**
 * Demonstrates type-safe property paths with {@link Sort}.
 *
 * @author Christoph Strobl
 */
@SpringBootTest(classes = TypedPropertyPathJdbcConfiguration.class)
@AutoConfigureJdbc
class TypedPropertyPathTests {

	@Autowired
	CategoryRepository repository;

	@Autowired
	JdbcAggregateTemplate jdbcAggregateTemplate;

	@BeforeEach
	void clearData() {
		repository.deleteAll();
	}

	@Test
	void queryFilterWithTypedPropertyPath() {

		repository.save(new Category("Porsche", "Sports cars"));
		repository.save(new Category("BMW", "Luxury cars"));
		repository.save(new Category("Porsche", "SUV series"));

		List<Category> all = jdbcAggregateTemplate.findAll(
			query(where(Category::getName).is("Porsche")),
			Category.class);

		assertThat(all).hasSize(2);
		assertThat(all).extracting(Category::getName).containsOnly("Porsche");
		assertThat(all).extracting(Category::getDescription)
			.containsExactlyInAnyOrder("Sports cars", "SUV series");
	}

	@Test
	void sortBySinglePropertyWithTypedPropertyPath() {

		repository.save(new Category("Porsche", "Sports cars"));
		repository.save(new Category("Audi", "German luxury"));
		repository.save(new Category("Mercedes", "Premium"));

		List<Category> all = repository.findAll(Sort.by(Category::getName));

		assertThat(all).extracting(Category::getName).containsExactly("Audi", "Mercedes", "Porsche");
	}

	@Test
	void sortByMultiplePropertiesWithTypedPropertyPath() {

		repository.save(new Category("Porsche", "Cayenne"));
		repository.save(new Category("Porsche", "911"));
		repository.save(new Category("BMW", "911"));

		List<Category> all = repository.findAll(Sort.by(Category::getName, Category::getDescription));

		assertThat(all).extracting(Category::toString)
			.containsExactly("BMW 911", "Porsche 911", "Porsche Cayenne");
	}
}

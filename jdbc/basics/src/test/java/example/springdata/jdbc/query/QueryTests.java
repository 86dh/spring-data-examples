/*
 * Copyright 2025 the original author or authors.
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
package example.springdata.jdbc.query;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.data.relational.core.query.Criteria.*;
import static org.springframework.data.relational.core.query.Query.*;

import example.springdata.jdbc.basics.simpleentity.Category;
import example.springdata.jdbc.basics.simpleentity.CategoryConfiguration;
import example.springdata.jdbc.basics.simpleentity.CategoryRepository;

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
 * @author Mark Paluch
 */
@SpringBootTest(classes = CategoryConfiguration.class)
@AutoConfigureJdbc
class QueryTests {

	@Autowired JdbcAggregateTemplate template;

	@Autowired CategoryRepository repository;

	@BeforeEach
	void clearData() {
		repository.deleteAll();
	}

	@Test
	void queryFilterWithStringPropertyPath() {

		repository.save(new Category("Porsche", "Sports cars"));
		repository.save(new Category("BMW", "Luxury cars"));
		repository.save(new Category("Porsche", "SUV series"));

		List<Category> all = template.findAll(query(where("name").is("Porsche")), Category.class);

		assertThat(all).hasSize(2);
		assertThat(all).extracting(Category::getName).containsOnly("Porsche");
		assertThat(all).extracting(Category::getDescription).containsExactlyInAnyOrder("Sports cars", "SUV series");
	}

	@Test
	void queryFilterWithTypedPropertyPath() {

		repository.save(new Category("Porsche", "Sports cars"));
		repository.save(new Category("BMW", "Luxury cars"));
		repository.save(new Category("Porsche", "SUV series"));

		List<Category> all = template.findAll(query(where(Category::getName).is("Porsche")), Category.class);

		assertThat(all).hasSize(2);
		assertThat(all).extracting(Category::getName).containsOnly("Porsche");
		assertThat(all).extracting(Category::getDescription).containsExactlyInAnyOrder("Sports cars", "SUV series");
	}

	@Test
	void sortBySinglePropertyWithStringPropertyPath() {

		insertAudiMercedesAndPorsche();
		List<Category> all = repository.findAll(Sort.by("name"));

		assertThat(all).extracting(Category::getName).containsExactly("Audi", "Mercedes", "Porsche");
	}

	@Test
	void sortBySinglePropertyWithTypedPropertyPath() {

		insertAudiMercedesAndPorsche();
		List<Category> all = repository.findAll(Sort.by(Category::getName));

		assertThat(all).extracting(Category::getName).containsExactly("Audi", "Mercedes", "Porsche");
	}

	@Test
	void sortByMultiplePropertiesWithStringPropertyPath() {

		insertBmwAndPorsche();
		List<Category> all = repository.findAll(Sort.by("name", "description"));

		assertThat(all).extracting(Category::toString).containsExactly("BMW 911", "Porsche 911", "Porsche Cayenne");
	}

	@Test
	void sortByMultiplePropertiesWithTypedPropertyPath() {

		insertBmwAndPorsche();
		List<Category> all = repository.findAll(Sort.by(Category::getName, Category::getDescription));

		assertThat(all).extracting(Category::toString).containsExactly("BMW 911", "Porsche 911", "Porsche Cayenne");
	}

	private void insertAudiMercedesAndPorsche() {
		repository.save(new Category("Porsche", "Sports cars"));
		repository.save(new Category("Audi", "German luxury"));
		repository.save(new Category("Mercedes", "Premium"));
	}

	private void insertBmwAndPorsche() {
		repository.save(new Category("Porsche", "Cayenne"));
		repository.save(new Category("Porsche", "911"));
		repository.save(new Category("BMW", "911"));
	}
}

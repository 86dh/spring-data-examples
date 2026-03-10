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

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;

/**
 * Coarse classification entity for demonstrating type-safe property paths with {@link org.springframework.data.domain.Sort}.
 *
 * @author Christoph Strobl
 */
public class Category {

	private final @Id Long id;
	private String name;
	private String description;
	private LocalDateTime created;
	private Long inserted;

	public Category(String name, String description) {
		this.id = null;
		this.name = name;
		this.description = description;
		this.created = LocalDateTime.now();
		this.inserted = null;
	}

	@PersistenceCreator
	Category(Long id, String name, String description, LocalDateTime created, Long inserted) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.created = created;
		this.inserted = inserted;
	}

	public void timeStamp() {
		if (inserted == null || inserted == 0) {
			inserted = System.currentTimeMillis();
		}
	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public LocalDateTime getCreated() {
		return this.created;
	}

	public Long getInserted() {
		return this.inserted;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public void setInserted(Long inserted) {
		this.inserted = inserted;
	}

	@Override
	public String toString() {
		return "%s %s".formatted(name, description);
	}
}

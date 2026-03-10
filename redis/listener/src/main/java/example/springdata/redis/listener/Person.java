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

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Example domain type for JSON payloads in Redis Pub/Sub messages.
 *
 * @author Christoph Strobl
 */
public class Person {

	private final String firstname;
	private final String lastname;

	@JsonCreator
	public Person(@JsonProperty("firstname") String firstname, @JsonProperty("lastname") String lastname) {
		this.firstname = firstname;
		this.lastname = lastname;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}


	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Person person = (Person) o;
		return Objects.equals(firstname, person.firstname) && Objects.equals(lastname, person.lastname);
	}

	@Override
	public int hashCode() {
		return Objects.hash(firstname, lastname);
	}

	@Override
	public String toString() {
		return "Person{firstname='" + firstname + "', lastname='" + lastname + "'}";
	}
}

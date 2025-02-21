package dev.nokee.commons.gradle.provider;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.MapProperty;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static dev.nokee.commons.gradle.provider.ProviderUtils.asJdkMap;
import static dev.nokee.commons.hamcrest.gradle.provider.ProviderOfMatcher.providerOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class JdkMapPropertyAdapterIntegrationTests {
	ObjectFactory objectFactory() {
		return ProjectBuilder.builder().build().getObjects();
	}

	static Object a = new Object();
	static Object b = new Object();
	static Object c = new Object();
	static Object d = new Object();
	static Object e = new Object();

	MapProperty<String, Object> property = objectFactory().mapProperty(String.class, Object.class);
	Map<String, Object> subject = asJdkMap(property);

	@BeforeEach
	void setup() {
		property.put("first", a);
		property.put("second", b);
		property.put("third", c);
	}

	@Test
	void canPutNewValue() {
		subject.put("fourth", d);
		assertThat(property.get(), hasEntry("fourth", d));
	}

	@Test
	void canPutNewValues() {
		subject.putAll(Map.of("fourth", d, "fifth", e));
		assertThat(property.get(), allOf(hasEntry("fourth", d), hasEntry("fifth", e)));
	}

	@Test
	void canGetValue() {
		assertThat(subject.get("first"), sameInstance(a));
		assertThat(subject.get("second"), sameInstance(b));
		assertThat(subject.get("third"), sameInstance(c));

		assertThat(subject.get("non-existent"), nullValue());
	}

	@Test
	void canGetValues() {
		assertThat(subject.values(), contains(a, b, c));
	}

	@Test
	void canGetKeys() {
		assertThat(subject.keySet(), contains("first", "second", "third"));
	}

	@Test
	void canGetOrDefault() {
		assertThat(subject.getOrDefault("first", e), equalTo(a));
		assertThat(subject.getOrDefault("non-existent", e), equalTo(e));
	}

	@Test
	void canClear() {
		subject.clear();
		assertThat(property, providerOf(anEmptyMap()));
	}

	@Test
	void canGetSize() {
		assertThat(subject.size(), is(3));
	}

	@Test
	void canRemoveKey() {
		assertThat(subject.remove("second"), equalTo(b));
	}

	@Test
	void canContainsKey() {
		assertThat(subject.containsKey("third"), is(true));
	}

	@Test
	void canContainsValue() {
		assertThat(subject.containsValue(a), is(true));
	}
}

package dev.nokee.commons.gradle.attributes;

import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class AttributesMapPropertyCreationTests {
	@Test
	void canCreateMapPropertyNestedInExtension() {
		ProjectBuilder.builder().build().getObjects().newInstance(MyExtension.class);
	}

	@Test
	void canCreateMapPropertyUsingObjectFactory() {
		ProjectBuilder.builder().build().getObjects().newInstance(Attributes.MapProperty.class);
	}

	public interface MyExtension {
		@org.gradle.api.tasks.Nested
		Attributes.MapProperty getMyAttributesProperty();
	}
}

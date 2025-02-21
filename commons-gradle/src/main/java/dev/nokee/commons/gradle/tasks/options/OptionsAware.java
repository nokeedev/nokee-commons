package dev.nokee.commons.gradle.tasks.options;

import org.gradle.api.tasks.Nested;

/**
 * Represents an object with options.
 */
public interface OptionsAware {
	@Nested
	Options getOptions();
}

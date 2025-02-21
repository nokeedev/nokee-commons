package dev.nokee.commons.gradle.tasks.options;

import org.gradle.api.Action;

public interface IConfigureActionLookup<T> {
	Action<T> resolve(ISourceKey key);
}

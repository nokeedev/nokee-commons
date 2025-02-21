package dev.nokee.commons.gradle.provider;

import org.gradle.api.NonExtensible;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.HasConfigurableValue;
import org.gradle.api.provider.HasMultipleValues;
import org.gradle.api.provider.Provider;

import javax.inject.Inject;

@NonExtensible
public /*final*/ class MemoizeProviderFactory {
	private final ObjectFactory objects;

	@Inject
	public MemoizeProviderFactory(ObjectFactory objects) {
		this.objects = objects;
	}

	@SuppressWarnings({"unchecked", "UnstableApiUsage"})
	public <T> Provider<T> memoizeProvider(Provider<T> provider) {
		HasConfigurableValue result = null;
		if (provider instanceof HasMultipleValues) {
			result = objects.listProperty(Object.class).value((Provider<? extends Iterable<?>>) provider);
		} else {
			result = objects.property(Object.class).value(provider);
		}
		result.finalizeValueOnRead();
		result.disallowChanges();
		return (Provider<T>) result;
	}
}

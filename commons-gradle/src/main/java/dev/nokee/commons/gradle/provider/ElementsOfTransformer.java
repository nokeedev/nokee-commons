package dev.nokee.commons.gradle.provider;

import org.gradle.api.Transformer;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileSystemLocation;
import org.gradle.api.provider.Provider;

import java.util.Set;

final class ElementsOfTransformer<T> implements Transformer<Provider<Set<FileSystemLocation>>, T> {
	private final Transformer<? extends FileCollection, ? super T> mapper;

	public ElementsOfTransformer(Transformer<? extends FileCollection, ? super T> mapper) {
		this.mapper = mapper;
	}

	@Override
	public Provider<Set<FileSystemLocation>> transform(T t) {
		return mapper.transform(t).getElements();
	}

	@Override
	public String toString() {
		return "element of " + mapper;
	}
}

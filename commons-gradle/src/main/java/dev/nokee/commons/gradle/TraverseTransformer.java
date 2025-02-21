package dev.nokee.commons.gradle;

import org.gradle.api.Transformer;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

final class TraverseTransformer<OutputType, InputType> implements Transformer<List<OutputType>, Iterable<? extends InputType>> {
	private final Transformer<OutputType, InputType> mapper;

	public TraverseTransformer(Transformer<OutputType, InputType> mapper) {
		this.mapper = mapper;
	}

	@Override
	public List<OutputType> transform(Iterable<? extends InputType> values) {
		final List<OutputType> result = new ArrayList<>();
		for (InputType value : values) {
			@Nullable
			final OutputType mappedValue = mapper.transform(value);
			if (mappedValue != null) {
				result.add(mappedValue);
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "traverse(" + mapper + ")";
	}
}

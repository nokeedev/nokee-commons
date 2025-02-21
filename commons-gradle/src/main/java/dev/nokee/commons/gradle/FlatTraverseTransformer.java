package dev.nokee.commons.gradle;

import org.gradle.api.Transformer;

import java.util.ArrayList;
import java.util.List;

final class FlatTraverseTransformer<OutputType, InputType> implements Transformer<List<OutputType>, Iterable<? extends InputType>> {
	private final Transformer<? extends Iterable<? extends OutputType>, InputType> mapper;

	public FlatTraverseTransformer(Transformer<? extends Iterable<? extends OutputType>, InputType> mapper) {
		this.mapper = mapper;
	}

	@Override
	public List<OutputType> transform(Iterable<? extends InputType> values) {
		final List<OutputType> result = new ArrayList<>();
		for (InputType value : values) {
			Iterable<? extends OutputType> transformedValue = mapper.transform(value);
			if (transformedValue != null) {
				for (OutputType out : transformedValue) {
					result.add(out);
				}
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "flat traverse(" + mapper + ")";
	}
}

package dev.nokee.commons.names;

import java.util.function.Consumer;

import static dev.nokee.commons.names.StringUtils.capitalize;
import static dev.nokee.commons.names.StringUtils.uncapitalize;

interface NameBuilder {
	NameBuilder append(String s);
	NameBuilder append(Consumer<? super NameBuilder> action);
	NameBuilder append(NameString.MainName name);

	static NameBuilder accordion(NameBuilder builder) {
		return new NameBuilder() {
			@Override
			public NameBuilder append(String s) {
				builder.append(s);
				return this;
			}

			@Override
			public NameBuilder append(Consumer<? super NameBuilder> action) {
				builder.append(it -> action.accept(this));
				return this;
			}

			@Override
			public NameBuilder append(NameString.MainName name) {
				// ignore
				return this;
			}

			@Override
			public String toString() {
				return builder.toString();
			}
		};
	}

	static NameBuilder dirNames() {
		return new NameBuilder() {
			private final StringBuilder result = new StringBuilder();

			@Override
			public NameBuilder append(String s) {
				if (!s.trim().isEmpty()) {
					result.append("/").append(s);
				}
				return this;
			}

			@Override
			public NameBuilder append(Consumer<? super NameBuilder> action) {
				action.accept(accordion(this)); // ignore main sub strings
				return this;
			}

			@Override
			public NameBuilder append(NameString.MainName name) {
				name.delegate().appendTo(this);
				return this;
			}

			@Override
			public String toString() {
				return result.substring(1);
			}
		};
	}

	static NameBuilder lowerCamelCase() {
		return new NameBuilder() {
			private final StringBuilder result = new StringBuilder();

			@Override
			public NameBuilder append(String s) {
				result.append(capitalize(s));
				return this;
			}

			@Override
			public NameBuilder append(Consumer<? super NameBuilder> action) {
				action.accept(this);
				return this;
			}

			@Override
			public NameBuilder append(NameString.MainName name) {
				name.delegate().appendTo(this);
				return this;
			}

			@Override
			public String toString() {
				return uncapitalize(result.toString());
			}
		};
	}

	static NameBuilder toStringCase() {
		return accordion(lowerCamelCase());
	}
}

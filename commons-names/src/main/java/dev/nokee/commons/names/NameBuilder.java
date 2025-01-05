package dev.nokee.commons.names;

import static dev.nokee.commons.names.StringUtils.capitalize;
import static dev.nokee.commons.names.StringUtils.uncapitalize;

interface NameBuilder {
	NameBuilder append(String s);
	NameBuilder append(IAppendTo qualifier);
	NameBuilder append(MainName name);

	static NameBuilder accordion(NameBuilder builder) {
		return new NameBuilder() {
			@Override
			public NameBuilder append(String s) {
				builder.append(s);
				return this;
			}

			@Override
			public NameBuilder append(IAppendTo qualifier) {
				qualifier.appendTo(this);
				return this;
			}

			@Override
			public NameBuilder append(MainName name) {
				// ignore
				return this;
			}

			@Override
			public String toString() {
				return builder.toString();
			}
		};
	}

	static NameBuilder lowerCamelCase() {
		return new NameBuilder() {
			StringBuilder result = new StringBuilder();

			@Override
			public NameBuilder append(String s) {
				result.append(capitalize(s));
				return this;
			}

			@Override
			public NameBuilder append(IAppendTo qualifier) {
				qualifier.appendTo(this);
				return this;
			}

			@Override
			public NameBuilder append(MainName name) {
				result.append(capitalize(name.toString()));
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

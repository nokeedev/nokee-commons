package dev.nokee.commons.names;

import static dev.nokee.commons.names.StringUtils.capitalize;
import static dev.nokee.commons.names.StringUtils.uncapitalize;

interface NameBuilder {
	NameBuilder append(String s);
	default NameBuilder append(NameString qualifier) {
		qualifier.appendTo(this);
		return this;
	}
	NameBuilder append(MainName name);

	static NameBuilder accordion(NameBuilder builder) {
		return new NameBuilder() {
			@Override
			public NameBuilder append(String s) {
				builder.append(s);
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

	static NameBuilder dirNames() {
		return new NameBuilder() {
			private boolean ignoreMain = false; // by default ignores main segment
			private final StringBuilder result = new StringBuilder();

			@Override
			public NameBuilder append(String s) {
				if (!s.trim().isEmpty()) {
					result.append("/").append(s);
				}
				return this;
			}

			@Override
			public NameBuilder append(NameString qualifier) {
				// ignores main sub segment
				boolean currentIgnoreMain = ignoreMain;
				ignoreMain = true;
				try {
					qualifier.appendTo(this);
				} finally {
					ignoreMain = currentIgnoreMain;
				}
				return this;
			}

			@Override
			public NameBuilder append(MainName name) {
				if (!ignoreMain) {
					return append(name.toString());
				}
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

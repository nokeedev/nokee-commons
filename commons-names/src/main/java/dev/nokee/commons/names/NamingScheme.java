package dev.nokee.commons.names;

import static dev.nokee.commons.names.StringUtils.capitalize;
import static dev.nokee.commons.names.StringUtils.uncapitalize;

public interface NamingScheme {
	String format(Object name);

	static NamingScheme dirNames() {
		return new NamingScheme() {
			@Override
			public String format(Object name) {
				NameBuilder builder = new NameBuilder() {
					StringBuilder result = new StringBuilder();

					@Override
					public NameBuilder append(String s) {
						if (!s.trim().isEmpty()) {
							result.append("/").append(s);
						}
						return this;
					}

					@Override
					public NameBuilder append(MainName name) {
						return append(name.toString());
					}

					@Override
					public String toString() {
						return result.substring(1);
					}
				};
				if (name instanceof IAppendTo) {
					((IAppendTo) name).appendTo(builder);
				} else {
					builder.append(name.toString());
				}
				return builder.toString();
			}
		};
	}
}

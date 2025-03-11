package dev.nokee.commons.names;

public interface NamingScheme {
	String format(Name name);

	static NamingScheme taskName() {
		return new NamingScheme() {
			@Override
			public String format(Name name) {
				NameBuilder builder = NameBuilder.lowerCamelCase();
				Object verb = name.get("verb");
				if (verb != null) builder.append(verb.toString());
				Object qualifyingName = name.get("qualifyingName");
				if(qualifyingName != null) builder.append(qualifyingName.toString());
				Object object = name.get("object");
				if (object != null) builder.append(object.toString());

				return builder.toString();
			}
		};
	}

	static NamingScheme prefixQualifyingName() {
		return new NamingScheme() {
			@Override
			public String format(Name name) {
				NameBuilder builder = NameBuilder.lowerCamelCase();
				Object qualifyingName = name.get("qualifyingName");
				if (qualifyingName != null) builder.append(qualifyingName.toString());
				Object elementName = name.get("elementName");
				if (elementName != null) builder.append(elementName.toString());
				return builder.toString();
			}
		};
	}

	static NamingScheme suffixQualifyingName() {
		return new NamingScheme() {
			@Override
			public String format(Name name) {
				NameBuilder builder = NameBuilder.lowerCamelCase();
				Object elementName = name.get("elementName");
				if (elementName != null) builder.append(elementName.toString());
				Object qualifyingName = name.get("qualifyingName");
				if (qualifyingName != null) builder.append(qualifyingName.toString());
				return builder.toString();
			}
		};
	}

	static NamingScheme dirNames() {
		return new NamingScheme() {
			@Override
			public String format(Name name) {
				NameBuilder builder = NameBuilder.dirNames();
				name.toString(builder);
				return builder.toString();
			}
		};
	}
}

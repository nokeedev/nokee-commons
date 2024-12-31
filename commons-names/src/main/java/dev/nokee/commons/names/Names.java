package dev.nokee.commons.names;

/**
 * Names are a special type of qualifying names that fits in the middle of additional names, ex: {@literal <componentName>.<binaryName>.<taskName>}, both componentName and binaryName would be node names.
 * Configuration, task, or software component names are generally leaf names.
 */
public interface Names extends QualifyingName {
	default Names append(String name) {
		return append(Qualifiers.of(name));
	}

	default Names append(Qualifier qualifier) {
		return new DefaultNames(Qualifiers.of(this, qualifier));
	}

	default Names append(Names name) {
		return append((Qualifier) name);
	}

	//region Returns String for conveniences
	default String append(ElementName name) {
		return name.qualifiedBy(this).toString();
	}

	default String taskName(String verb) {
		return ElementName.taskName(verb).qualifiedBy(this).toString();
	}

	default String taskName(String verb, String object) {
		return ElementName.taskName(verb, object).qualifiedBy(this).toString();
	}

	default String configurationName(String name) {
		return ElementName.configurationName(name).qualifiedBy(this).toString();
	}

	default String componentName(String name) {
		return ElementName.componentName(name).qualifiedBy(this).toString();
	}
	//endregion
}

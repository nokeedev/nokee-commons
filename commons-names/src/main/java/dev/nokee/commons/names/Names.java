package dev.nokee.commons.names;

/**
 * Names are a special type of qualifying names that fits in the middle of additional names, ex: {@literal <componentName>.<binaryName>.<taskName>}, both componentName and binaryName would be node names.
 * Configuration, task, or software component names are generally leaf names.
 */
public interface Names extends Qualifier, FullyQualifiedName {
	static Names ofMain() {
		return new DefaultNames(Qualifiers.as(ElementName.ofMain("main")));
	}

	static Names ofMain(String name) {
		return new DefaultNames(Qualifiers.as(ElementName.ofMain(name)));
	}

	static Names of(String name) {
		return new DefaultNames(Qualifiers.as(ElementName.of(name)));
	}

	default Names append(String name) {
		return append(ElementName.of(name));
	}

	default Names append(OtherName name) {
		return new DefaultNames(Qualifiers.as(name.qualifiedBy(this)));
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

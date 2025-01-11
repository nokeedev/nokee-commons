package dev.nokee.commons.names;

import static dev.nokee.commons.names.NamingScheme.qualifier;
import static dev.nokee.commons.names.NamingScheme.string;

public final class SoftwareComponentName extends NameSupport<SoftwareComponentName> implements ElementName {
	private final String name;

	private SoftwareComponentName(String name) {
		this.name = name;
	}

	public static SoftwareComponentName of(String name) {
		return new SoftwareComponentName(name);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public FullyQualifiedName qualifiedBy(Qualifier qualifier) {
		return new DefaultFullyQualifiedName(qualifier, this, NamingScheme.of(string(name), qualifier()));
	}
}

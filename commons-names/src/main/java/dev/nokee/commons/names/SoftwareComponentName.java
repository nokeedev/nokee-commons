package dev.nokee.commons.names;

import static dev.nokee.commons.names.NamingScheme.qualifier;
import static dev.nokee.commons.names.NamingScheme.string;

final class SoftwareComponentName extends NameSupport<SoftwareComponentName> implements ElementName {
	private final NamingScheme scheme;

	private SoftwareComponentName(NamingScheme scheme) {
		this.scheme = scheme;
	}

	public static SoftwareComponentName of(String name) {
		return new SoftwareComponentName(NamingScheme.of(string(name), qualifier()));
	}

	@Override
	public String toString() {
		return scheme.format(this).using(NameBuilder::toStringCase);
	}

	@Override
	public FullyQualifiedName qualifiedBy(Qualifier qualifier) {
		return new DefaultFullyQualifiedName(qualifier, this, scheme);
	}
}

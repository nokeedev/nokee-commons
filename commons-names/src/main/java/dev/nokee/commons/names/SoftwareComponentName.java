package dev.nokee.commons.names;

import static dev.nokee.commons.names.LegacyNamingScheme.qualifier;
import static dev.nokee.commons.names.LegacyNamingScheme.string;

final class SoftwareComponentName extends NameSupport<SoftwareComponentName> implements ElementName {
	private final LegacyNamingScheme scheme;

	private SoftwareComponentName(LegacyNamingScheme scheme) {
		this.scheme = scheme;
	}

	public static SoftwareComponentName of(String name) {
		return new SoftwareComponentName(LegacyNamingScheme.of(string(name), qualifier()));
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

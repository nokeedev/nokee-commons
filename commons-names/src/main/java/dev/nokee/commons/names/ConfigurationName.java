package dev.nokee.commons.names;

import static dev.nokee.commons.names.NamingScheme.qualifier;
import static dev.nokee.commons.names.NamingScheme.string;

// TODO: We should make this an interface to allow "Configuration Name" implementation
public final class ConfigurationName extends NameSupport<ConfigurationName> implements ElementName {
	private final NamingScheme scheme;

	private ConfigurationName(NamingScheme scheme) {
		this.scheme = scheme;
	}

	static ConfigurationName of(String name) {
		return new ConfigurationName(NamingScheme.of(qualifier(), string(name)));
	}

	@Override
	public String toString() {
		return scheme.format(this).using(NameBuilder::toStringCase);
	}

	@Override
	public FullyQualifiedName qualifiedBy(Qualifier qualifier) {
		return new DefaultFullyQualifiedName(qualifier, this, scheme);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Builder() {}

		public ConfigurationName prefix(String name) {
			return new ConfigurationName(NamingScheme.of(string(name), qualifier()));
		}
	}
}

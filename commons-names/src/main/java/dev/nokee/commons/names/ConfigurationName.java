package dev.nokee.commons.names;

// TODO: We should make this an interface to allow "Configuration Name" implementation
// TODO: Support a different naming scheme (default is <qualifyingName><Name> but create a builder to allow <name><QualifyingName>)
//   This would avoid requirements to create custom implementation.
//   Also add a builder method in Names
public final class ConfigurationName extends NameSupport<ConfigurationName> implements ElementName {
	private final String name;

	private ConfigurationName(String name) {
		this.name = name;
	}

	static ConfigurationName of(String name) {
		return new ConfigurationName(name);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public FullyQualifiedName qualifiedBy(Qualifier qualifier) {
		return new DefaultFullyQualifiedName(qualifier, this, new Scheme() {
			@Override
			public String format(NameBuilder builder) {
				return builder.append(qualifier).append(name.toString()).toString();
			}
		});
	}
}

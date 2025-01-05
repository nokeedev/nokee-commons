package dev.nokee.commons.names;

// TODO: We should make this an interface to allow "Configuration Name" implementation
public final class ConfigurationName extends NameSupport implements ElementName {
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
		return new ForQualifiedName() {
			@Override
			public String toString() {
				return NameBuilder.lowerCamelCase().append(qualifier).append(name).toString();
			}
		};
	}
}

package dev.nokee.commons.names;

import java.util.function.Function;

// TODO: We should make this an interface to allow "Configuration Name" implementation
// TODO: Support a different naming scheme (default is <qualifyingName><Name> but create a builder to allow <name><QualifyingName>)
//   This would avoid requirements to create custom implementation.
//   Also add a builder method in Names
public final class ConfigurationName extends NameSupport<ConfigurationName> implements ElementName {
	private final String name;
	private final Function<? super Qualifier, ? extends Scheme> factory;

	private ConfigurationName(String name, Function<? super Qualifier, ? extends Scheme> factory) {
		this.name = name;
		this.factory = factory;
	}

	static ConfigurationName of(String name) {
		return new ConfigurationName(name, qualifier -> {
			return builder -> {
				qualifier.appendTo(builder);
				builder.append(name);
				return builder.toString();
			};
		});
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public FullyQualifiedName qualifiedBy(Qualifier qualifier) {
		return new DefaultFullyQualifiedName(qualifier, this, factory.apply(qualifier));
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Builder() {}

		public ConfigurationName prefix(String name) {
			return new ConfigurationName(name, qualifier -> {
				return builder -> {
					builder.append(name);
					qualifier.appendTo(builder);
					return builder.toString();
				};
			});
		}
	}
}

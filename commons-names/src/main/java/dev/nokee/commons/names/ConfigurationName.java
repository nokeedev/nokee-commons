package dev.nokee.commons.names;

// TODO: We should make this an interface to allow "Configuration Name" implementation
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
		return new FullyQualified(qualifier);
	}

	private final class FullyQualified extends NameSupport<FullyQualifiedName> implements FullyQualifiedName {
		private final Qualifier qualifier;

		public FullyQualified(Qualifier qualifier) {
			this.qualifier = qualifier;
		}

		Prop<FullyQualifiedName> init() {
			return new Prop.Builder<>(FullyQualifiedName.class)
				.with("qualifier", this::withQualifier)
				.with("elementName", this::withElementName)
				.elseWith(b -> b.elseWith(qualifier, this::withQualifier))
				.build();
		}

		public FullyQualified withQualifier(Qualifier qualifier) {
			return new FullyQualified(qualifier);
		}

		public FullyQualifiedName withElementName(ElementName elementName) {
			return elementName.qualifiedBy(qualifier);
		}

		@Override
		public String toString() {
			return NameBuilder.toStringCase().append(qualifier).append(name.toString()).toString();
		}
	}
}

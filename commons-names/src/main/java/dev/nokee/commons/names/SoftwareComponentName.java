package dev.nokee.commons.names;

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
		return new FullyQualified(qualifier);
	}

	private final class FullyQualified extends NameSupport<FullyQualifiedName> implements FullyQualifiedName {
		private final Qualifier qualifier;

		private FullyQualified(Qualifier qualifier) {
			this.qualifier = qualifier;
		}

		@Override
		Prop<FullyQualifiedName> init() {
			return new Prop.Builder<>(FullyQualifiedName.class)
				.with("qualifier", this::withQualifier)
				.with("elementName", this::withElementName)
				.elseWith(b -> b.elseWith(qualifier, this::withQualifier))
				.build();
		}

		public FullyQualifiedName withQualifier(Qualifier qualifier) {
			return new FullyQualified(qualifier);
		}

		public FullyQualifiedName withElementName(ElementName elementName) {
			return elementName.qualifiedBy(qualifier);
		}

		@Override
		public String toString() {
			return NameBuilder.toStringCase().append(name).append(qualifier).toString();
		}
	}
}

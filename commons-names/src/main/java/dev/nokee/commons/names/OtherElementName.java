package dev.nokee.commons.names;

final class OtherElementName extends NameSupport<OtherElementName> implements OtherName, IAppendTo {
	private final String name;

	public OtherElementName(String name) {
		this.name = name;
	}

	@Override
	public QualifyingName qualifiedBy(Qualifier qualifier) {
		return new FullyQualified(qualifier);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void appendTo(NameBuilder builder) {
		builder.append(name);
	}

	private final class FullyQualified extends NameSupport<FullyQualifiedName> implements QualifyingName {
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

		public FullyQualified withQualifier(Qualifier qualifier) {
			return new FullyQualified(qualifier);
		}

		public FullyQualifiedName withElementName(ElementName elementName) {
			return elementName.qualifiedBy(qualifier);
		}

		@Override
		public void appendTo(NameBuilder builder) {
			builder.append(qualifier);
			builder.append(OtherElementName.this);
		}

		@Override
		public String toString(NamingScheme scheme) {
			return scheme.format(this);
		}

		@Override
		public String toString() {
			return NameBuilder.toStringCase().append(qualifier).append(name).toString();
		}
	}
}

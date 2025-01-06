package dev.nokee.commons.names;

import java.util.Set;

final class MainElementName extends NameSupport implements OtherName, IAppendTo {
	private final String name;

	public MainElementName(String name) {
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
		builder.append(new MainName() {
			@Override
			public String toString() {
				return name;
			}
		});
	}

	private final class FullyQualified extends NameSupport implements QualifyingName, IParameterizedObject<FullyQualifiedName> {
		private final Qualifier qualifier;
		private final Prop<FullyQualifiedName> prop;

		private FullyQualified(Qualifier qualifier) {
			this.qualifier = qualifier;
			this.prop = new Prop.Builder<>(FullyQualifiedName.class)
				.with("qualifier", this::withQualifier)
				.with("elementName", this::withElementName)
				.elseWith(qualifier, this::withQualifier)
				.build();
		}

		public FullyQualified withQualifier(Qualifier qualifier) {
			return new FullyQualified(qualifier);
		}

		public FullyQualifiedName withElementName(ElementName elementName) {
			return elementName.qualifiedBy(qualifier);
		}

		@Override
		public Set<String> propSet() {
			return prop.names();
		}

		@Override
		public FullyQualifiedName with(String propName, Object value) {
			return prop.with(propName, value);
		}

		@Override
		public void appendTo(NameBuilder builder) {
			builder.append(qualifier);
			builder.append(MainElementName.this);
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

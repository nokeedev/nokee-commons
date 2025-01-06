package dev.nokee.commons.names;

import java.util.Set;

public final class SoftwareComponentName extends NameSupport implements ElementName {
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

	private final class FullyQualified extends NameSupport implements FullyQualifiedName, IParameterizedObject<FullyQualifiedName> {
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

		public FullyQualifiedName withQualifier(Qualifier qualifier) {
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
		public String toString() {
			return NameBuilder.toStringCase().append(name).append(qualifier).toString();
		}
	}
}

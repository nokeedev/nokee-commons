package dev.nokee.commons.names;

public class DefaultFullyQualifiedName extends NameSupport<FullyQualifiedName> implements FullyQualifiedName {
	private final Qualifier qualifier;
	private final ElementName elementName;
	private final Scheme scheme;

	public DefaultFullyQualifiedName(Qualifier qualifier, ElementName elementName, Scheme scheme) {
		this.qualifier = qualifier;
		this.elementName = elementName;
		this.scheme = scheme;
	}

	Prop<FullyQualifiedName> init() {
		return new Prop.Builder<>(FullyQualifiedName.class)
			.with("qualifier", this::withQualifier)
			.with("elementName", this::withElementName)
			.elseWith(b -> b.elseWith(qualifier, this::withQualifier))
			.elseWith(b -> b.elseWith(elementName, this::withElementName))
			.build();
	}

	public DefaultFullyQualifiedName withQualifier(Qualifier qualifier) {
		return new DefaultFullyQualifiedName(qualifier, elementName, scheme);
	}

	public FullyQualifiedName withElementName(ElementName elementName) {
		return elementName.qualifiedBy(qualifier);
	}

	@Override
	public String toString() {
		return scheme.format(NameBuilder.toStringCase());
	}
}

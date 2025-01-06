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

	@Override
	void init(Prop.Builder<FullyQualifiedName> builder) {
		builder.with("qualifier", this::withQualifier)
			.with("elementName", this::withElementName)
			.elseWith(qualifier, this::withQualifier)
			.elseWith(elementName, this::withElementName);
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

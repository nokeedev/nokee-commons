package dev.nokee.commons.names;

final class DefaultFullyQualifiedName extends NameSupport<FullyQualifiedName> implements FullyQualifiedName {
	private final Qualifier qualifier;
	private final ElementName elementName;
	private final NamingScheme scheme;

	public DefaultFullyQualifiedName(Qualifier qualifier, ElementName elementName, NamingScheme scheme) {
		this.qualifier = qualifier;
		this.elementName = elementName;
		this.scheme = scheme;
	}

	@Override
	void init(Prop.Builder<FullyQualifiedName> builder) {
		builder.with("qualifier", this::withQualifier, this::getQualifier)
			.with("elementName", this::withElementName, this::getElementName);
	}

	public DefaultFullyQualifiedName withQualifier(Qualifier qualifier) {
		return new DefaultFullyQualifiedName(qualifier, elementName, scheme);
	}

	public Qualifier getQualifier() {
		return qualifier;
	}

	public FullyQualifiedName withElementName(ElementName elementName) {
		return elementName.qualifiedBy(qualifier);
	}

	public ElementName getElementName() {
		return elementName;
	}

	@Override
	public String toString() {
		return scheme.format(this).using(NameBuilder::toStringCase);
	}

	@Override
	public String toString(NameBuilder builder) {
		return scheme.format(this).using(() -> builder);
	}

	@Override
	public RelativeName relativeTo(Qualifier qualifier) {
		return new DefaultRelativeName(this, elementName.qualifiedBy(DefaultRelativeName.relativeTo(this.qualifier, qualifier)));
	}
}

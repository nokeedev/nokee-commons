package dev.nokee.commons.names;

final class DefaultQualifyingName extends NameSupport<FullyQualifiedName> implements QualifyingName {
	private final Qualifier qualifier;
	private final OtherName elementName;
	private final LegacyNamingScheme scheme;

	public DefaultQualifyingName(Qualifier qualifier, OtherName elementName, LegacyNamingScheme scheme) {
		this.qualifier = qualifier;
		this.elementName = elementName;
		this.scheme = scheme;
	}

	@Override
	void init(Prop.Builder<FullyQualifiedName> builder) {
		builder.with("qualifier", this::withQualifier, this::getQualifier)
			.with("elementName", this::withElementName, this::getElementName);
	}

	public DefaultQualifyingName withQualifier(Qualifier qualifier) {
		return new DefaultQualifyingName(qualifier, elementName, scheme);
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
	public void appendTo(NameBuilder builder) {
		qualifier.appendTo(builder);
		elementName.appendTo(builder);
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

	@Override
	public void accept(Visitor visitor) {
		qualifier.accept(visitor);
		visitor.visit(elementName);
	}
}

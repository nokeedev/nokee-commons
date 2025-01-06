package dev.nokee.commons.names;

final class DefaultQualifyingName extends NameSupport<FullyQualifiedName> implements QualifyingName {
	private final Qualifier qualifier;
	private final OtherName elementName;
	private final Scheme scheme;

	public DefaultQualifyingName(Qualifier qualifier, OtherName elementName, Scheme scheme) {
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

	public DefaultQualifyingName withQualifier(Qualifier qualifier) {
		return new DefaultQualifyingName(qualifier, elementName, scheme);
	}

	public FullyQualifiedName withElementName(ElementName elementName) {
		return elementName.qualifiedBy(qualifier);
	}

	@Override
	public void appendTo(NameBuilder builder) {
		qualifier.appendTo(builder);
		builder.append(elementName);
	}

	@Override
	public String toString(NamingScheme scheme) {
		return scheme.format(this);
	}

	@Override
	public String toString() {
		return scheme.format(NameBuilder.toStringCase());
	}
}

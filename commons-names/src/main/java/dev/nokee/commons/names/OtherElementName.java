package dev.nokee.commons.names;

import static dev.nokee.commons.names.NamingScheme.qualifier;

final class OtherElementName extends NameSupport<OtherElementName> implements OtherName, IAppendTo {
	private final NameString name;

	public OtherElementName(NameString name) {
		this.name = name;
	}

	@Override
	void init(Prop.Builder<OtherElementName> builder) {
		builder.elseWith(name, OtherElementName::new)
			.prop(name, name::get);
	}

	@Override
	public QualifyingName qualifiedBy(Qualifier qualifier) {
		return new DefaultQualifyingName(qualifier, this, NamingScheme.of(qualifier(), name));
	}

	@Override
	public String toString() {
		return name.toString();
	}

	@Override
	public void appendTo(NameBuilder builder) {
		name.appendTo(builder);
	}

	@Override
	public String toString(NameBuilder builder) {
		return builder.append(name.toString()).toString();
	}

	@Override
	public RelativeName relativeTo(Qualifier qualifier) {
		throw new UnsupportedOperationException("no qualifier, hence cannot relativize");
	}

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}

package dev.nokee.commons.names;

final class DefaultNames extends NameSupport<DefaultNames> implements Names {
	private final QualifyingName qualifier;

	public DefaultNames(QualifyingName qualifier) {
		this.qualifier = qualifier;
	}

	@Override
	void init(Prop.Builder<DefaultNames> builder) {
		builder.elseWith(qualifier, DefaultNames::new);
	}

	@Override
	public void appendTo(NameBuilder builder) {
		qualifier.appendTo(builder);
	}

	@Override
	public String toString(NameBuilder builder) {
		qualifier.appendTo(builder);
		return builder.toString();
	}

	@Override
	public RelativeName relativeTo(Qualifier qualifier) {
		return this.qualifier.relativeTo(qualifier);
	}

	@Override
	public String toString() {
		return qualifier.toString();
	}

	@Override
	public void accept(Visitor visitor) {
		qualifier.accept(visitor);
	}
}

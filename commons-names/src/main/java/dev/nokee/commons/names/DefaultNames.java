package dev.nokee.commons.names;

final class DefaultNames extends NameSupport<DefaultNames> implements Names {
	private final QualifyingName qualifier;

	public DefaultNames(QualifyingName qualifier) {
		this.qualifier = qualifier;
	}

	@Override
	Prop<DefaultNames> init() {
		return new Prop.Builder<>(DefaultNames.class)
			.elseWith(b -> b.elseWith(qualifier, DefaultNames::new))
			.build();
	}

	@Override
	public void appendTo(NameBuilder sb) {
		sb.append(qualifier);
	}

	@Override
	public String toString(NamingScheme scheme) {
		return qualifier.toString(scheme);
	}

	@Override
	public String toString(NameBuilder builder) {
		return builder.append(qualifier).toString();
	}

	@Override
	public String toString() {
		return qualifier.toString();
	}
}

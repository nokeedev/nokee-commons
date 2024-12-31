package dev.nokee.commons.names;

final class DefaultNames extends NameSupport implements Names {
	private final Qualifier qualifier;

	public DefaultNames(Qualifier qualifier) {
		this.qualifier = qualifier;
	}

	@Override
	public void appendTo(NameBuilder sb) {
		sb.append(qualifier);
	}

	@Override
	public void accept(Visitor visitor) {
		qualifier.accept(visitor);
	}

	@Override
	public FullyQualifiedName qualifiedBy(Qualifier qualifier) {
		return new FullyQualifiedName() {
			@Override
			public void appendTo(NameBuilder builder) {
				builder.append(DefaultNames.this.qualifier);
				builder.append(qualifier);
			}

			@Override
			public String toString() {
				return NamingScheme.lowerCamelCase().format(this);
			}
		};
	}

	@Override
	public String toString(NamingScheme scheme) {
		return qualifier.toString(scheme);
	}

	@Override
	public String toString() {
		return toString(NamingScheme.lowerCamelCase());
	}
}

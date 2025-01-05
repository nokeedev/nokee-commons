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
	public String toString(NamingScheme scheme) {
		return qualifier.toString(scheme);
	}

	@Override
	public String toString() {
		return qualifier.toString();
	}
}

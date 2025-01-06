package dev.nokee.commons.names;

import java.util.Set;

abstract class NameSupport<SELF> implements IParameterizedObject<SELF> {
	private Prop<SELF> prop;

	protected NameSupport() {}

	void init(Prop.Builder<SELF> builder) {

	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;

		Name other = Name.of(obj);
		return toString().equals(other.toString());
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public abstract String toString();

	private Prop<SELF> prop() {
		if (prop == null) {
			Prop.Builder<SELF> builder = new Prop.Builder<>(null);
			init(builder);
			prop = builder.build();
		}
		return prop;
	}

	@Override
	public Set<String> propSet() {
		return prop().names();
	}

	@Override
	public SELF with(String propName, Object value) {
		return prop().with(propName, value);
	}
}

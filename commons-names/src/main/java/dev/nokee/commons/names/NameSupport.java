package dev.nokee.commons.names;

import java.util.Set;

abstract class NameSupport<SELF> implements IParameterizedObject<SELF> {
	private final Prop<SELF> prop;

	protected NameSupport() {
		this.prop = init();
	}

	Prop<SELF> init() {
		return Prop.empty();
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

	@Override
	public Set<String> propSet() {
		return prop.names();
	}

	@Override
	public SELF with(String propName, Object value) {
		return prop.with(propName, value);
	}
}

package dev.nokee.commons.names;

import java.util.Collections;
import java.util.Set;

final class DefaultNames extends NameSupport implements Names, IParameterizedObject<DefaultNames> {
	private final QualifyingName qualifier;

	public DefaultNames(QualifyingName qualifier) {
		this.qualifier = qualifier;
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

	@Override
	public Set<String> propSet() {
		if (qualifier instanceof IParameterizedObject) {
			return ((IParameterizedObject<?>) qualifier).propSet();
		}
		return Collections.emptySet();
	}

	@Override
	public DefaultNames with(String propName, Object value) {
		if (qualifier instanceof IParameterizedObject) {
			return new DefaultNames(((IParameterizedObject<QualifyingName>) qualifier).with(propName, value));
		}
		return this;
	}
}

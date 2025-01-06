package dev.nokee.commons.names;

import java.util.Set;

interface IParameterizedObject<SELF> {
	Set<String> propSet();
	SELF with(String propName, Object value);
}

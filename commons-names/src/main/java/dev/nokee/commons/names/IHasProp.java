package dev.nokee.commons.names;

import java.util.Set;

interface IHasProp {
	Set<String> propSet();
	Object get(String propName);
}

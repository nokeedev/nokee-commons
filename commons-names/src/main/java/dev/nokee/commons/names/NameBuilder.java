package dev.nokee.commons.names;

import static dev.nokee.commons.names.StringUtils.capitalize;
import static dev.nokee.commons.names.StringUtils.uncapitalize;

interface NameBuilder {
	NameBuilder append(String s);
	NameBuilder append(IAppendTo qualifier);
	NameBuilder append(MainName name);
}

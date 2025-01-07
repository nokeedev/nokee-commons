package dev.nokee.commons.names;

interface NameString extends IAppendTo, IParameterizedObject<NameString> {
	interface MainName {
		NameString delegate();
	}
}

package dev.nokee.commons.names;

public interface OtherName extends ElementName, QualifyingName {
	@Override
	QualifyingName qualifiedBy(Qualifier qualifier);
}

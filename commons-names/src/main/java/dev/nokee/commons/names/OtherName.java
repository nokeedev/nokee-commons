package dev.nokee.commons.names;

public interface OtherName extends ElementName {
	@Override
	QualifyingName qualifiedBy(Qualifier qualifier);
}

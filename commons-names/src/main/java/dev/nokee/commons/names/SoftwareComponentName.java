package dev.nokee.commons.names;

public final class SoftwareComponentName extends NameSupport implements ElementName {
	private final String name;

	private SoftwareComponentName(String name) {
		this.name = name;
	}

	public static SoftwareComponentName of(String name) {
		return new SoftwareComponentName(name);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public FullyQualifiedName qualifiedBy(Qualifier qualifier) {
		return new ForQualifiedName() {
			@Override
			public String toString() {
				return NameBuilder.toStringCase().append(name).append(qualifier).toString();
			}
		};
	}
}

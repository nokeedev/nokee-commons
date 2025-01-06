package dev.nokee.commons.names;

final class OtherElementName extends NameSupport<OtherElementName> implements OtherName, IAppendTo {
	private final String name;

	public OtherElementName(String name) {
		this.name = name;
	}

	@Override
	public QualifyingName qualifiedBy(Qualifier qualifier) {
		return new DefaultQualifyingName(qualifier, this, builder -> {
			qualifier.appendTo(builder);
			builder.append(name);
			return builder.toString();
		});
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void appendTo(NameBuilder builder) {
		builder.append(name);
	}
}

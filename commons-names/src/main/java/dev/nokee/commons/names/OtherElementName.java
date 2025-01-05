package dev.nokee.commons.names;

final class OtherElementName extends NameSupport implements OtherName, IAppendTo {
	private final String name;

	public OtherElementName(String name) {
		this.name = name;
	}

	@Override
	public QualifyingName qualifiedBy(Qualifier qualifier) {
		return new QualifyingName() {
			@Override
			public void appendTo(NameBuilder builder) {
				builder.append(qualifier);
				builder.append(OtherElementName.this);
			}

			@Override
			public String toString() {
				return NameBuilder.lowerCamelCase().append(qualifier).append(name).toString();
			}
		};
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

package dev.nokee.commons.names;

final class MainElementName extends NameSupport<MainElementName> implements OtherName, IAppendTo {
	private final String name;

	public MainElementName(String name) {
		this.name = name;
	}

	@Override
	public QualifyingName qualifiedBy(Qualifier qualifier) {
		return new DefaultQualifyingName(qualifier, this, new Scheme() {
			@Override
			public String format(NameBuilder builder) {
				return builder.append(qualifier).append(name).toString();
			}
		});
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public void appendTo(NameBuilder builder) {
		builder.append(new MainName() {
			@Override
			public String toString() {
				return name;
			}
		});
	}
}

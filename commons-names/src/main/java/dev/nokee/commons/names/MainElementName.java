package dev.nokee.commons.names;

final class MainElementName extends NameSupport implements OtherName, IAppendTo {
	private final String name;

	public MainElementName(String name) {
		this.name = name;
	}

	@Override
	public QualifyingName qualifiedBy(Qualifier qualifier) {
		return new QualifyingName() {
			@Override
			public void appendTo(NameBuilder builder) {
				builder.append(qualifier);
				builder.append(MainElementName.this);
			}

			@Override
			public String toString() {
				return NameBuilder.toStringCase().append(qualifier).append(name).toString();
			}
		};
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

package dev.nokee.commons.names;

final class OtherElementName extends NameSupport<OtherElementName> implements OtherName, IAppendTo {
	private final NameString name;

	private OtherElementName(NameString name) {
		this.name = name;
	}

	public static OtherElementName main(String name) {
		return new OtherElementName(new MainString(name));
	}

	public static OtherElementName other(String name) {
		return new OtherElementName(new OtherString(name));
	}

	private interface NameString extends IAppendTo {

	}

	private static final class MainString implements MainName, NameString {
		private final String name;

		private MainString(String name) {
			this.name = name;
		}

		@Override
		public void appendTo(NameBuilder builder) {
			builder.append(this);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private static final class OtherString implements Name, NameString {
		private final String name;

		public OtherString(String name) {
			this.name = name;
		}

		@Override
		public void appendTo(NameBuilder builder) {
			builder.append(name);
		}

		@Override
		public String toString() {
			return name;
		}
	}

	@Override
	public QualifyingName qualifiedBy(Qualifier qualifier) {
		return new DefaultQualifyingName(qualifier, this, builder -> {
			qualifier.appendTo(builder);
			builder.append(name.toString());
			return builder.toString();
		});
	}

	@Override
	public String toString() {
		return name.toString();
	}

	@Override
	public void appendTo(NameBuilder builder) {
		name.appendTo(builder);
	}

	@Override
	public String toString(NameBuilder builder) {
		return builder.append(name.toString()).toString();
	}
}

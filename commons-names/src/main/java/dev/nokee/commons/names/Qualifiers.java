package dev.nokee.commons.names;

import java.util.Arrays;

final class Qualifiers {
	public static Qualifier as(OtherName name) {
		return new Qualifier() {
			@Override
			public void appendTo(NameBuilder builder) {
				// TODO: Should just append the name and let the builder figure out
				((IAppendTo) name).appendTo(builder);
			}

			@Override
			public String toString() {
				return name.toString();
			}
		};
	}

	public static Qualifier as(FullyQualifiedName name) {
		return new Qualifier() {
			@Override
			public void appendTo(NameBuilder builder) {
				// TODO: Should just append the name and let the builder figure out
				((IAppendTo) name).appendTo(builder);
			}

			@Override
			public String toString() {
				return name.toString();
			}
		};
	}

	public static Qualifier of(Qualifier... qualifiers) {
		return new CompositeQualifier(Arrays.asList(qualifiers));
	}

	public static Qualifier of(String value) {
		return new DefaultQualifier(value);
	}

	public static Qualifier ofMain(Qualifier qualifier) {
		return new MainQualifier(qualifier);
	}

	private static final class CompositeQualifier implements Qualifier {
		private final Iterable<Qualifier> qualifiers;

		public CompositeQualifier(Iterable<Qualifier> qualifiers) {
			this.qualifiers = qualifiers;
		}

		@Override
		public void appendTo(NameBuilder sb) {
			qualifiers.forEach(sb::append);
		}

		@Override
		public void accept(Visitor visitor) {
			qualifiers.forEach(visitor::visit);
		}

		@Override
		public String toString(NamingScheme scheme) {
			return scheme.format(this);
		}

		@Override
		public String toString() {
			return toString(NamingScheme.lowerCamelCase());
		}
	}

	private static final class DefaultQualifier implements Qualifier {
		private final String value;

		public DefaultQualifier(String value) {
			this.value = value;
		}

		@Override
		public void appendTo(NameBuilder sb) {
			sb.append(value);
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visit(this);
		}

		@Override
		public String toString(NamingScheme scheme) {
			return scheme.format(this);
		}

		@Override
		public String toString() {
			return toString(NamingScheme.lowerCamelCase());
		}
	}

	private static final class MainQualifier implements Qualifier, MainName {
		private final Qualifier qualifier;

		public MainQualifier(Qualifier qualifier) {
			this.qualifier = qualifier;
		}

		@Override
		public void appendTo(NameBuilder sb) {
			sb.append((MainName) this);
		}

		@Override
		public void accept(Visitor visitor) {
			visitor.visit(qualifier);
		}

		@Override
		public String toString(NamingScheme scheme) {
			return scheme.format(qualifier);
		}

		@Override
		public String toString() {
			return toString(NamingScheme.lowerCamelCase());
		}
	}
}

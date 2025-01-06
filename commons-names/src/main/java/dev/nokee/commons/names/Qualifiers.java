package dev.nokee.commons.names;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

final class Qualifiers {
	public static Qualifier of(Qualifier... qualifiers) {
		return new CompositeQualifier(Arrays.asList(qualifiers));
	}

	public static Qualifier of(String value) {
		return new DefaultQualifier(value);
	}

	public static Qualifier ofMain(Qualifier qualifier) {
		return new MainQualifier(qualifier);
	}

	private static final class CompositeQualifier implements Qualifier, IParameterizedObject<Qualifier> {
		private final Iterable<Qualifier> qualifiers;

		public CompositeQualifier(Iterable<Qualifier> qualifiers) {
			this.qualifiers = qualifiers;
		}

		@Override
		public Set<String> propSet() {
			return Collections.emptySet();
		}

		@Override
		public Qualifier with(String propName, Object value) {
			return this;
		}

		@Override
		public void appendTo(NameBuilder sb) {
			qualifiers.forEach(sb::append);
		}

		@Override
		public String toString(NamingScheme scheme) {
			return scheme.format(this);
		}

		@Override
		public String toString() {
			NameBuilder builder = NameBuilder.toStringCase();
			qualifiers.forEach(builder::append);
			return builder.toString();
		}
	}

	private static final class DefaultQualifier implements Qualifier, IParameterizedObject<Qualifier> {
		private final String value;

		public DefaultQualifier(String value) {
			this.value = value;
		}

		@Override
		public Set<String> propSet() {
			return Collections.emptySet();
		}

		@Override
		public Qualifier with(String propName, Object value) {
			return this;
		}

		@Override
		public void appendTo(NameBuilder sb) {
			sb.append(value);
		}

		@Override
		public String toString(NamingScheme scheme) {
			return scheme.format(this);
		}

		@Override
		public String toString() {
			return NameBuilder.toStringCase().append(value).toString();
		}
	}

	private static final class MainQualifier implements Qualifier, MainName, IParameterizedObject<Qualifier> {
		private final Qualifier qualifier;

		public MainQualifier(Qualifier qualifier) {
			this.qualifier = qualifier;
		}

		@Override
		public Set<String> propSet() {
			return Collections.emptySet();
		}

		@Override
		public Qualifier with(String propName, Object value) {
			return this;
		}

		@Override
		public void appendTo(NameBuilder sb) {
			sb.append((MainName) this);
		}

		@Override
		public String toString(NamingScheme scheme) {
			return scheme.format(qualifier);
		}

		@Override
		public String toString() {
			return NameBuilder.toStringCase().append(qualifier).toString();
		}
	}
}

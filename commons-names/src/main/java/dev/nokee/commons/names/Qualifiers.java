package dev.nokee.commons.names;

import java.util.*;
import java.util.stream.Collectors;

final class Qualifiers {
	public static Qualifier of(Qualifier... qualifiers) {
		return new CompositeQualifier(Arrays.asList(qualifiers));
	}
	public static Qualifier of(Iterable<Qualifier> qualifiers) {
		return new CompositeQualifier(qualifiers);
	}

	public static Qualifier of(String value) {
		return new DefaultQualifier(value);
	}

	public static Qualifier ofMain(Qualifier qualifier) {
		return new MainQualifier(qualifier);
	}

	private static final class CompositeQualifier implements Qualifier, IParameterizedObject<Qualifier> {
		private final List<Qualifier> qualifiers = new ArrayList<>();
		private final Prop<Qualifier> prop;

		public CompositeQualifier(Iterable<Qualifier> qualifiers) {
			qualifiers.forEach(this.qualifiers::add);

			Prop.Builder<Qualifier> builder = new Prop.Builder<>(Qualifier.class);
			for (final Qualifier q : qualifiers) {
				builder.elseWith(q, it -> {
					return new CompositeQualifier(this.qualifiers.stream().map(t -> {
						if (t.equals(q)) {
							return it;
						}
						return t;
					}).collect(Collectors.toList()));
				});
			}
			this.prop = builder.build();
		}

		@Override
		public Set<String> propSet() {
			return prop.names();
		}

		@Override
		public Qualifier with(String propName, Object value) {
			return prop.with(propName, value);
		}

		@Override
		public void appendTo(NameBuilder builder) {
			// TODO: Not exactly, see BinaryName
			qualifiers.forEach(builder::append);
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
		public void appendTo(NameBuilder builder) {
			builder.append(value);
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
		public void appendTo(NameBuilder builder) {
			builder.append((MainName) this);
		}

		@Override
		public String toString() {
			return NameBuilder.toStringCase().append(qualifier).toString();
		}
	}
}

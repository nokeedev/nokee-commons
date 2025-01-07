package dev.nokee.commons.names;

import java.util.*;
import java.util.stream.Collectors;

final class Qualifiers {
	public static CompositeQualifier of(NameString... qualifiers) {
		return new CompositeQualifier(Arrays.asList(qualifiers));
	}
	public static CompositeQualifier of(Iterable<NameString> qualifiers) {
		return new CompositeQualifier(qualifiers);
	}

	public static DefaultQualifier of(String value) {
		return new DefaultQualifier(value);
	}

	public static NameString empty() {
		return new EmptyQualifier();
	}

	public static MainQualifier ofMain(NameString qualifier) {
		return new MainQualifier(qualifier);
	}

	public static NameString of(String propName, NameString s) {
		return new PropQualifier(propName, s);
	}

	static final class EmptyQualifier implements IParameterizedObject<EmptyQualifier>, NameString {
		@Override
		public void appendTo(NameBuilder builder) {
			// nothing to append
		}

		@Override
		public Set<String> propSet() {
			return Collections.emptySet();
		}

		@Override
		public EmptyQualifier with(String propName, Object value) {
			return this; // no property
		}
	}

	static final class PropQualifier implements IParameterizedObject<PropQualifier>, NameString {
		private final String propName;
		private final NameString delegate;

		private PropQualifier(String propName, NameString delegate) {
			this.propName = propName;
			this.delegate = delegate;
		}

		@Override
		public void appendTo(NameBuilder builder) {
			delegate.appendTo(builder);
		}

		@Override
		public Set<String> propSet() {
			return Collections.singleton(propName);
		}

		@Override
		public PropQualifier with(String propName, Object value) {
			if (propName.equals(this.propName)) {
				if (value instanceof NameString) {
					return new PropQualifier(this.propName, (NameString) value);
				} else if (value instanceof String) {
					return new PropQualifier(this.propName, Qualifiers.of((String) value));
				} else {
					throw new IllegalArgumentException("Unsupported qualifier type: " + value);
				}
			}
			return this;
		}

		@Override
		public String toString() {
			return delegate.toString();
		}
	}

	static final class CompositeQualifier implements Qualifier, IParameterizedObject<CompositeQualifier>, NameString {
		private final List<NameString> qualifiers = new ArrayList<>();
		private final Prop<CompositeQualifier> prop;

		public CompositeQualifier(Iterable<NameString> qualifiers) {
			qualifiers.forEach(this.qualifiers::add);

			Prop.Builder<CompositeQualifier> builder = new Prop.Builder<>(CompositeQualifier.class);
			for (final NameString q : qualifiers) {
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
		public CompositeQualifier with(String propName, Object value) {
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

	static final class DefaultQualifier implements Qualifier, IParameterizedObject<Qualifier>, NameString {
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

	static final class MainQualifier implements Qualifier, MainName, IParameterizedObject<Qualifier>, NameString {
		private final NameString qualifier;

		public MainQualifier(NameString qualifier) {
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

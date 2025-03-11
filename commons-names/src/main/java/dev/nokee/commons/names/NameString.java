package dev.nokee.commons.names;

import java.util.*;
import java.util.stream.Collectors;

abstract class NameString implements IAppendTo, IParameterizedObject<NameString>, LegacyNamingScheme.Segment, IHasProp {
	interface MainName {
		NameString delegate();
	}

	public static NameString of(NameString... qualifiers) {
		return new CompositeQualifier(Arrays.asList(qualifiers));
	}
	public static NameString of(Iterable<NameString> qualifiers) {
		return new CompositeQualifier(qualifiers);
	}

	public static NameString of(String value) {
		return new DefaultQualifier(value);
	}

	public static NameString empty() {
		return new EmptyQualifier();
	}

	public static NameString ofMain(NameString qualifier) {
		return new MainQualifier(qualifier);
	}

	public static NameString of(String propName, NameString s) {
		return new PropQualifier(propName, s);
	}

	@Override
	public Optional<Object> format(Name prop) {
		return Optional.of(this);
	}

	private static final class EmptyQualifier extends NameString {
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

		@Override
		public Object get(String propName) {
			return null;
		}
	}

	private static final class PropQualifier extends NameString {
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
					return new PropQualifier(this.propName, NameString.of((String) value));
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

		@Override
		public Object get(String propName) {
			if (propName.equals(this.propName)) {
				return delegate;
			}
			return null;
		}
	}

	private static final class CompositeQualifier extends NameString {
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
			builder.append(it -> {
				for (NameString qualifier : qualifiers) {
					qualifier.appendTo(it);
				}
			});
		}

		@Override
		public String toString() {
			NameBuilder builder = NameBuilder.toStringCase();
			qualifiers.forEach(it -> it.appendTo(builder));
			return builder.toString();
		}

		@Override
		public Object get(String propName) {
			for (NameString q : qualifiers) {
				Object result = q.get(propName);
				if (result != null) {
					return result;
				}
			}
			return null;
		}
	}

	private static final class DefaultQualifier extends NameString {
		private final String value;

		public DefaultQualifier(String value) {
			this.value = value;
		}

		@Override
		public Set<String> propSet() {
			return Collections.emptySet();
		}

		@Override
		public NameString with(String propName, Object value) {
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

		@Override
		public Optional<Object> format(Name prop) {
			return Optional.of(this);
		}

		@Override
		public Object get(String propName) {
			return null;
		}
	}

	private static final class MainQualifier extends NameString implements NameString.MainName {
		private final NameString qualifier;

		public MainQualifier(NameString qualifier) {
			this.qualifier = qualifier;
		}

		@Override
		public Set<String> propSet() {
			return Collections.emptySet();
		}

		@Override
		public NameString with(String propName, Object value) {
			return this;
		}

		@Override
		public void appendTo(NameBuilder builder) {
			builder.append(this);
		}

		@Override
		public String toString() {
			final NameBuilder builder = NameBuilder.toStringCase();
			qualifier.appendTo(builder);
			return builder.toString();
		}

		@Override
		public NameString delegate() {
			return qualifier;
		}

		@Override
		public Object get(String propName) {
			return null;
		}
	}
}

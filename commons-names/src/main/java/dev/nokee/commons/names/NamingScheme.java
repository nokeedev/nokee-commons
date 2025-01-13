package dev.nokee.commons.names;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

final class NamingScheme {
	private final List<Segment> segments;

	private NamingScheme(List<Segment> segments) {
		this.segments = segments;
	}

	public Formatter format(Name name) {
		return new Formatter() {
			@Override
			public String using(Factory<NameBuilder> factory) {
				NameBuilder builder = factory.create();
				for (Segment segment : segments) {
					segment.format(name).ifPresent(obj -> {
						if (obj instanceof String) {
							builder.append((String) obj);
						} else if (obj instanceof IAppendTo) {
							((IAppendTo) obj).appendTo(builder);
						} else {
							throw new UnsupportedOperationException();
						}
					});
				}
				return builder.toString();
			}
		};
	}

	public interface Formatter {
		String using(Factory<NameBuilder> factory);
	}

	public interface Factory<T> {
		T create();
	}

	public static NamingScheme of(Segment... segments) {
		return new NamingScheme(Arrays.asList(segments));
	}

	public interface Segment {
		Optional<Object> format(Name prop);
	}

	public static Segment property(String property) {
		return new Segment() {
			@Override
			public Optional<Object> format(Name prop) {
				return Optional.ofNullable(prop.get(property));
			}
		};
	}

	public static Segment string(String s) {
		return new Segment() {
			@Override
			public Optional<Object> format(Name prop) {
				return Optional.of(s);
			}
		};
	}

	public static Segment qualifier() {
		return new Segment() {
			@Override
			public Optional<Object> format(Name prop) {
				return Optional.ofNullable(prop.get("qualifier"));
			}
		};
	}
}

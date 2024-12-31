package dev.nokee.commons.names;

import java.util.Optional;

import static dev.nokee.commons.names.StringUtils.capitalize;
import static java.util.Objects.requireNonNull;

// <verb><qualifyingName>[<object>]
public final class TaskName extends NameSupport implements ElementName {
	private final String verb;
	private final String object;

	private TaskName(String verb, String object) {
		this.verb = verb;
		this.object = object;
	}

	public String getVerb() {
		return verb;
	}

	public Optional<String> getObject() {
		return Optional.ofNullable(object);
	}

	@Override
	public FullyQualifiedName qualifiedBy(Qualifier qualifier) {
		return new NameSupport.ForQualifiedName() {
			@Override
			public void appendTo(NameBuilder builder) {
				// TODO: this should be the format <verb><qualifier><object>
				builder.append(getVerb());
				builder.append(qualifier);
				getObject().ifPresent(builder::append);
			}

			@Override
			public String toString() {
				return NamingScheme.lowerCamelCase().format(this);
			}
		};
	}

	public static TaskName of(String taskName) {
		requireNonNull(taskName);
//		checkArgument(!taskName.isEmpty(), "'taskName' must not be empty");
//		checkArgument(Character.isLowerCase(taskName.charAt(0)));
		return new TaskName(taskName, null);
	}

	public static TaskName of(String verb, String object) {
		requireNonNull(verb);
		requireNonNull(object);
//		checkArgument(!verb.isEmpty());
//		checkArgument(!object.isEmpty());
//		checkArgument(Character.isLowerCase(verb.charAt(0)));
//		checkArgument(Character.isLowerCase(object.charAt(0)));
		return new TaskName(verb, object);
	}

	public static String taskName(String verb, String object) {
		return of(verb, object).toString();
	}

	@Override
	public String toString() {
		if (object == null) {
			return verb;
		}
		return verb + capitalize(object);
	}
}

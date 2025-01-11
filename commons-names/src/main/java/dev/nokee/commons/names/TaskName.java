package dev.nokee.commons.names;

import javax.annotation.Nullable;
import java.util.Optional;

import static dev.nokee.commons.names.NamingScheme.property;
import static dev.nokee.commons.names.NamingScheme.qualifier;
import static java.util.Objects.requireNonNull;

// <verb><qualifyingName>[<Object>]
// <qualifyingName><Object>
public final class TaskName extends NameSupport<TaskName> implements ElementName {
	private final NamingScheme scheme = NamingScheme.of(property("verb"), qualifier(), property("object"));
	@Nullable
	private final String verb;

	@Nullable
	private final String object;

	private TaskName(@Nullable String verb, @Nullable String object) {
		assert verb != null || object != null;
		this.verb = verb;
		this.object = object;
	}

	@Override
	void init(Prop.Builder<TaskName> builder) {
		builder.with("verb", this::withVerb)
			.with("object", this::withObject)
			.prop("verb", () -> getVerb().orElse(null))
			.prop("object", () -> getObject().orElse(null));
	}

	public Optional<String> getVerb() {
		return Optional.ofNullable(verb);
	}

	public TaskName withVerb(String verb) {
		return new TaskName(verb, object);
	}

	public Optional<String> getObject() {
		return Optional.ofNullable(object);
	}

	public TaskName withObject(@Nullable String object) {
		return new TaskName(verb, object);
	}

	@Override
	public FullyQualifiedName qualifiedBy(Qualifier qualifier) {
		// TODO: this should be the format <verb><qualifier><object>
		return new DefaultFullyQualifiedName(qualifier, this, scheme);
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
		return scheme.format(this).using(NameBuilder::toStringCase);
	}

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private Builder() {}

		public TaskName forObject(String object) {
			requireNonNull(object);
			return new TaskName(null, object);
		}
	}
}

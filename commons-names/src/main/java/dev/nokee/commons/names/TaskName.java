package dev.nokee.commons.names;

import javax.annotation.Nullable;
import java.util.Optional;

import static dev.nokee.commons.names.LegacyNamingScheme.property;
import static dev.nokee.commons.names.LegacyNamingScheme.qualifier;
import static dev.nokee.commons.names.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Represents a task name in Gradle.
 * Task name has three distinct scheme:
 * <ul>
 *   <li>verb-only: {@literal linkTest} - see {@link ElementName#taskName(String)}</li>
 *   <li>verb and object: {@literal compileTestCpp}, {@literal processTestResources} - see {@link ElementName#taskName(String, String)}</li>
 *   <li>object-only: {@literal testClasses}, {@literal testJar} - see {@link ElementName#taskName()} and {@link TaskName.Builder#forObject(String)}</li>
 * </ul>
 */
// TODO: Convert into interface
public final class TaskName extends NameSupport<TaskName> implements ElementName {
	private final LegacyNamingScheme scheme = LegacyNamingScheme.of(property("verb"), qualifier(), property("object"));
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
		builder.with("verb", this::withVerb, () -> getVerb().orElse(null))
			.with("object", this::withObject, () -> getObject().orElse(null));
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

	/**
	 * Returns an {@link TaskName} instance where qualifiers are appended to the specified task name.
	 * For example:
	 * <ul>
	 *   <li><i>taskName</i><u>SomeQualifier</u></li>
	 * </ul>
	 *
	 * @param taskName  the task name, must not be null
	 * @return a qualifiable task name
	 */
	public static TaskName of(String taskName) {
		requireNonNull(taskName);
		checkArgument(!taskName.trim().isEmpty(), "'taskName' must not be blank");
		checkArgument(Character.isLowerCase(taskName.charAt(0)), "'taskName' must start with a lowercase letter");
		return new TaskName(taskName, null);
	}

	/**
	 * Returns an {@link TaskName} instance where qualifiers are sandwiched between the specified verb and object (sometime referred as target).
	 * For example:
	 * <ul>
	 *   <li><i>verb</i><u>SomeQualifier</u><i>Object</i></li>
	 * </ul>
	 *
	 * @param verb  the verb for the task name, must not be null
	 * @param object  the object/target for the task name, must not be null
	 * @return a qualifiable task name
	 */
	public static TaskName of(String verb, String object) {
		requireNonNull(verb);
		requireNonNull(object);
		checkArgument(!verb.trim().isEmpty(), "'verb' must not be blank");
		checkArgument(!object.trim().isEmpty(), "'object' must not be blank");
		checkArgument(Character.isLowerCase(verb.charAt(0)), "'verb' must start with a lowercase letter");
		checkArgument(Character.isLowerCase(object.charAt(0)), "'object' must start with a lowercase letter");
		return new TaskName(verb, object);
	}

	@Deprecated // only for backward compatibility
	public static String taskName(String verb, String object) {
		return of(verb, object).toString();
	}

	/**
	 * Returns the {@link TaskName} instance representing the clean task name based on the lifecycle Gradle rule for the specified task.
	 *
	 * @param taskName  the task name to clean
	 * @return a qualifiable clean task name
	 */
	public static TaskName clean(TaskName taskName) {
		if (taskName.verb == null) {
			return taskName.withVerb("clean");
		} else {
			return taskName.withVerb("clean" + StringUtils.capitalize(taskName.verb));
		}
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

package dev.nokee.commons.names;

import javax.annotation.Nullable;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

// <verb><qualifyingName>[<Object>]
// <qualifyingName><Object>
public final class TaskName extends NameSupport<TaskName> implements ElementName {
	@Nullable
	private final String verb;

	@Nullable
	private final String object;

	private TaskName(@Nullable String verb, @Nullable String object) {
		this.verb = verb;
		this.object = object;
	}

	@Override
	Prop<TaskName> init() {
		return new Prop.Builder<>(TaskName.class)
			.with("verb", this::withVerb)
			.with("object", this::withObject)
			.build();
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
		return new FullyQualified(qualifier, this);
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
		NameBuilder builder = NameBuilder.toStringCase();
		getVerb().ifPresent(builder::append);
		getObject().ifPresent(builder::append);
		return builder.toString();
	}

	private static final class FullyQualified extends NameSupport<FullyQualifiedName> implements FullyQualifiedName {
		private final Qualifier qualifier;
		private final TaskName elementName;

		public FullyQualified(Qualifier qualifier, TaskName elementName) {
			this.qualifier = qualifier;
			this.elementName = elementName;
		}

		@Override
		Prop<FullyQualifiedName> init() {
			return new Prop.Builder<>(FullyQualifiedName.class)
				.with("qualifier", this::withQualifier)
				.with("elementName", this::withElementName)
				.elseWith(b -> b.elseWith(qualifier, this::withQualifier))
				.elseWith(b -> b.elseWith(elementName, this::withElementName))
				.build();
		}

		public FullyQualified withQualifier(Qualifier qualifier) {
			return new FullyQualified(qualifier, elementName);
		}

		public FullyQualifiedName withElementName(ElementName elementName) {
			return elementName.qualifiedBy(qualifier);
		}

		@Override
		public String toString() {
			// TODO: this should be the format <verb><qualifier><object>
			final NameBuilder result = NameBuilder.toStringCase();
			elementName.getVerb().ifPresent(result::append);
			result.append(qualifier);
			elementName.getObject().ifPresent(result::append);
			return result.toString();
		}
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

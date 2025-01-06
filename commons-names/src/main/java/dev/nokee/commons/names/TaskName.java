package dev.nokee.commons.names;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

import static dev.nokee.commons.names.StringUtils.capitalize;
import static java.util.Objects.requireNonNull;

// <verb><qualifyingName>[<object>]
public final class TaskName extends NameSupport implements ElementName, IParameterizedObject<TaskName> {
	private final String verb;
	private final String object;
	private final Prop<TaskName> prop = new Prop.Builder<>(TaskName.class).with("verb", this::withVerb).with("object", this::withObject).build();

	private TaskName(String verb, String object) {
		this.verb = verb;
		this.object = object;
	}

	public String getVerb() {
		return verb;
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
		if (object == null) {
			return verb;
		}
		return verb + capitalize(object);
	}

	@Override
	public Set<String> propSet() {
		return prop.names();
	}

	@Override
	public TaskName with(String propName, Object value) {
		return prop.with(propName, value);
	}

	private static final class FullyQualified extends NameSupport implements FullyQualifiedName {
		private final Qualifier qualifier;
		private final TaskName elementName;
		private final Prop<FullyQualifiedName> prop;

		public FullyQualified(Qualifier qualifier, TaskName elementName) {
			this.qualifier = qualifier;
			this.elementName = elementName;
			this.prop = new Prop.Builder<>(FullyQualifiedName.class)
				.with("qualifier", this::withQualifier)
				.with("elementName", this::withElementName)
				.elseWith(qualifier, this::withQualifier)
				.elseWith(elementName, this::withElementName)
				.build();
		}

		public FullyQualifiedName with(String propName, Object value) {
			return prop.with(propName, value);
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
			final NameBuilder result = NameBuilder.toStringCase().append(elementName.getVerb()).append(qualifier);
			elementName.getObject().ifPresent(result::append);
			return result.toString();
		}
	}
}

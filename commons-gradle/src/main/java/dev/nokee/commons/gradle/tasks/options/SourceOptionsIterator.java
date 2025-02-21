package dev.nokee.commons.gradle.tasks.options;

import org.gradle.api.Action;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.Iterator;

// Responsible for unrolling the "source files" into "source options" instance
public class SourceOptionsIterator<T> implements Iterator<SourceOptions<T>> {
	private final Iterator<File> delegate;
	private final Lookup<File, T> options;
	private final ReusableSourceOptions element;

	public SourceOptionsIterator(Iterable<File> sourceFiles, Lookup<File, T> options, ObjectFactory objects) {
		this.delegate = sourceFiles.iterator();
		this.options = options;
		this.element = objects.newInstance(ReusableSourceOptions.class);
	}

	@Override
	public boolean hasNext() {
		return delegate.hasNext();
	}

	@Override
	public SourceOptions<T> next() {
		File nextSourceFile = delegate.next();
		element.getSourceFile().set(nextSourceFile);
		element.options = options.get(nextSourceFile);
		return element.withNarrowedType(); // MUST NOT save the instance
	}

	/*private*/ static abstract /*final*/ class ReusableSourceOptions implements SourceOptions<Object> {
		Object options;

		@Inject
		public ReusableSourceOptions() {}

		@Override
		public abstract RegularFileProperty getSourceFile();

		@Override
		public Object getOptions() {
			return options;
		}

		@Override
		public void options(Action<? super Object> configureAction) {
			configureAction.execute(options);
		}

		@SuppressWarnings("unchecked")
		<S> SourceOptions<S> withNarrowedType() {
			return (SourceOptions<S>) this;
		}
	}
}

package dev.nokee.commons.gradle.tasks.options;

import dev.nokee.commons.gradle.Factory;
import org.gradle.api.Action;
import org.gradle.api.NonExtensible;
import org.gradle.api.Task;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;
import org.gradle.internal.UncheckedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Represents configurable source options.
 * The type is {@link Nested}-able, but due to limitation of the Gradle class decorator, you will have to use
 *
 * @param <T>  the source option type
 */
@NonExtensible
abstract /*final*/ class SourceOptionsSpec<T> implements ConfigurableSourceOptions<T>, AutoCloseable {
	private final ObjectFactory objects;
	private final Factory<T> optionsFactory;

	@Inject
	public SourceOptionsSpec(ObjectFactory objects, Factory<T> optionsFactory) {
		this.objects = objects;
		this.optionsFactory = optionsFactory;

		getAllBuckets().finalizeValueOnRead();
		getTaskDependencies().builtBy(new ImplicitTaskDependency());
		getAllSelectedFiles().from((Callable<?>) () -> getAllBuckets().get().stream().map(SourceOptionBucket::getFiles).collect(Collectors.toList()));
		getCleanedUp().set(false);
	}

	//region
	private interface SourceOptionBucket<T> extends SourceConfiguration {
		void visitDependencies(DependenciesContext<T> ctx);
		void visit(FileContext<T> ctx);
		void execute(FileContext<T> options);
		FileCollection getFiles();

		interface FileContext<T> {
			File getSourceFile();
			T getOptions();
			void mark();
		}

		interface DependenciesContext<T> {
			T getOptions();
		}
	}

	@SuppressWarnings("ClassEscapesDefinedScope")
	protected abstract ListProperty<SourceOptionBucket<T>> getAllBuckets();

	private void forFilesInternal(FileCollection files, Action<? super T> configureAction) {
		getAllBuckets().add(new SourceOptionBucket<T>() {
			@Override
			public void visitDependencies(DependenciesContext<T> ctx) {
				configureAction.execute(ctx.getOptions());
			}

			@Override
			public void visit(FileContext<T> ctx) {
				if (files.contains(ctx.getSourceFile())) {
					ctx.mark();
				}
			}

			@Override
			public void execute(FileContext<T> ctx) {
				configureAction.execute(ctx.getOptions());
			}

			@Override
			public FileCollection getFiles() {
				return files;
			}
		});
	}
	//endregion

	public abstract ConfigurableFileCollection getAllSelectedFiles();

	//region
	protected abstract ConfigurableFileCollection getTaskDependencies();

	private class ImplicitTaskDependency implements TaskDependency {
		@Override
		public @NotNull Set<? extends Task> getDependencies(@Nullable Task task) {
			// No source options buckets, nothing to compute
			if (getAllBuckets().get().isEmpty()) {
				return Collections.emptySet();
			}

			class Context implements SourceOptionBucket.DependenciesContext<T> {
				T options = null;

				@Override
				public T getOptions() {
					if (options == null) {
						options = optionsFactory.create();
					}
					return options;
				}
			}
			Context ctx = new Context();
			getAllBuckets().get().forEach(it -> it.visitDependencies(ctx));

			if (ctx.options != null) {
				ConfigurableFileCollection files = objects.fileCollection();
				collectDependencies(new TaskDependenciesCollector() {
					@Override
					public TaskDependenciesCollector from(Object taskDependencies) {
						files.from(taskDependencies);
						return this;
					}
				}, ctx.options);
				return files.getBuildDependencies().getDependencies(task);
			} else {
				return Collections.emptySet(); // no options used
			}
		}

		/**
		 * Collects dependencies from the configured options.
		 * The collecting process is the best effort to detect implicit task dependencies wired into the options.
		 *
		 * @param collector  the task dependencies collector
		 * @param options  the configured options
		 */
		private /*static*/ void collectDependencies(TaskDependenciesCollector collector, T options) {
			for (Method method : options.getClass().getInterfaces()[0].getDeclaredMethods()) {
				if (Modifier.isPublic(method.getModifiers()) && method.getParameterCount() == 0) {
					if (method.getAnnotation(Input.class) != null || method.getAnnotation(InputFiles.class) != null || method.getAnnotation(InputFile.class) != null) {
						if (Provider.class.isAssignableFrom(method.getReturnType())) {
							try {
								collector.from(((Provider<?>) method.invoke(options)).map(Collections::singletonList).orElse(Collections.emptyList()));
							} catch (IllegalAccessException | InvocationTargetException e) {
								throw new RuntimeException(e);
							}
						} else if (FileCollection.class.isAssignableFrom(method.getReturnType())) {
							try {
								collector.from(method.invoke(options));
							} catch (IllegalAccessException | InvocationTargetException e) {
								throw new RuntimeException(e);
							}
						} // not a task-aware property type
					} // not an input-annotated method
				} // not a getter like method
			}
		}
	}

	protected interface TaskDependenciesCollector {
		TaskDependenciesCollector from(Object taskDependencies);
	}
	//endregion

	//region
	private class OptionsResolver {
		private final Set<File> knownFiles = new HashSet<>();
		private final Map<File, ISourceKey> sourceFileToKeys = new HashMap<>();
		private final Map<ISourceKey, T> keyToOptions = new HashMap<>();
		private final MyContext ctx = new MyContext();

		public ISourceKey resolveKey(File sourceFile) {
			if (knownFiles.add(sourceFile)) {
				ctx.reset(sourceFile);

				for (int i = 0; i < getAllBuckets().get().size(); i++) {
					ctx.current = i;
					getAllBuckets().get().get(i).visit(ctx);
				}
				if (ctx.indices != null) {
					sourceFileToKeys.put(sourceFile, new Key(ctx.indices.stream().mapToInt(Integer::intValue).toArray()));
				}
			}
			return sourceFileToKeys.get(sourceFile);
		}

		private class MyContext implements SourceOptionBucket.FileContext<T> {
			@Override
			public File getSourceFile() {
				return sourceFile;
			}

			File sourceFile = null;
			T options = null;
			List<Integer> indices = null;
			int current = -1;

			private void reset(File sourceFile) {
				this.sourceFile = sourceFile;
				options = null;
				indices = null;
			}

			@Override
			public T getOptions() {
				if (options == null) {
					options = optionsFactory.create();
					indices = new ArrayList<>();
				}
				indices.add(current);
				return options;
			}

			@Override
			public void mark() {
				if (indices == null) {
					indices = new ArrayList<>();
				}
				indices.add(current);
			}
		}

		public Opt<T> resolve(File sourceFile) {
			ISourceKey key = resolveKey(sourceFile);
			if (key != null) {
				T options = keyToOptions.computeIfAbsent(key, __ -> {
					ctx.reset(sourceFile);
					key.restrict(getAllBuckets().get()).forEach(it -> it.execute(ctx));
					return ctx.options;
				});
				return new Opt<>(key, options);
			}
			return null;
		}
	}

	private static final class Opt<T> {
		private final ISourceKey key;
		private final T options;

		private Opt(ISourceKey key, T options) {
			this.key = key;
			this.options = options;
		}
	}

	private transient OptionsResolver resolver;

	private OptionsResolver resolver() {
		if (resolver == null) {
			resolver = new OptionsResolver();
		}
		return resolver;
	}
	//endregion

	public void close() {
		resolver = null;
		getCleanedUp().set(true);
	}

	protected abstract Property<Boolean> getCleanedUp();

	@Inject
	protected abstract ObjectFactory getObjects_do_not_use_in_child_classes();

	//region forFile* methods
	@Override
	public void options(Action<? super T> configureAction) {
		forFilesInternal(getAllSelectedFiles(), configureAction);
	}

	private FileTree asFileTree(Object sourcePath) {
		return getObjects_do_not_use_in_child_classes().fileCollection().from(sourcePath).getAsFileTree();
	}

	@Override
	public ConfigurableSourceFileOptions<T> forFile(File sourceFile) {
		return new DefaultOptionsProvider(asFileTree(sourceFile));
	}

	public void forFile(Object sourceFile, Action<? super T> configureAction) {
		forFilesInternal(asFileTree(sourceFile), configureAction);
	}

	@Override
	public void forFile(File sourceFile, Action<? super T> configureAction) {
		forFilesInternal(asFileTree(sourceFile), configureAction);
	}

	public ConfigurableSourceOptions<T> forFiles(FileTree sources) {
		return new DefaultOptionsProvider(sources);
	}

	private class DefaultOptionsProvider extends AbstractMap<String, T> implements ConfigurableSourceOptions<T>, BucketAware<T>, ConfigurableSourceOptionsInternal, ConfigurableSourceFileOptions<T> {
		private final FileTree sources;

		private DefaultOptionsProvider(FileTree sources) {
			this.sources = sources;
		}

		@Override
		public ConfigurableSourceFileOptions<T> forFile(File sourceFile) {
			return SourceOptionsSpec.this.forFile(sourceFile);
		}

		@Override
		public void forFile(File sourceFile, Action<? super T> configureAction) {
			SourceOptionsSpec.this.forFile(sourceFile, configureAction);
		}

		@Override
		public ConfigurableSourceOptions<T> forFiles(FileTree sourceFiles) {
			return SourceOptionsSpec.this.forFiles(sourceFiles);
		}

		@Override
		public ConfigurableSourceOptions<T> with(SourceOptions<T> sourceOptions) {
			SourceOptionsSpec.this.with(sourceOptions);
			return this;
		}

		@Override
		public ConfigurableSourceOptions<T> with(Provider<? extends SourceOptions<T>> sourceOptionsProvider) {
			SourceOptionsSpec.this.with(sourceOptionsProvider);
			return this;
		}

		@Override
		public void options(Action<? super T> action) {
			getAllSelectedFiles().from(sources);
			forFilesInternal(sources, action);
		}

		@Override
		public @NotNull Iterator<SourceFileOptions<T>> iterator() {
			assert !getCleanedUp().get();
			if (getAllBuckets().get().isEmpty()) {
				return Collections.emptyIterator();
			}

			List<SourceFileOptions<T>> result = new ArrayList<>();
			for (File file : sources) {
				Opt<T> options = resolver().resolve(file);
				if (options != null) {
					result.add(new DefaultSourceFileOptions<>(file, options.options));
				}
			}
			return result.iterator();
		}

		@Override
		public Iterable<Group<T>> groupedByOptions() {
			if (getAllBuckets().get().isEmpty()) {
				return Collections.emptyList();
			}

			Map<ISourceKey, Group<T>> result = new LinkedHashMap<>();
			for (File file : sources) {
				Opt<T> options = resolver().resolve(file);
				if (options != null) {
					((MutableGroupedOptions<T>) result.computeIfAbsent(options.key, key -> new MutableGroupedOptions<>(key, options.options))).sourceFiles.add(file);
				}
			}
			return result.values();
		}

		//region Map<String, T> for @Nested snapshotting
		@Override
		public @NotNull Set<Entry<String, T>> entrySet() {
			assert !getCleanedUp().get();
			if (getAllBuckets().get().isEmpty()) {
				return Collections.emptySet();
			}

			Map<String, T> result = new HashMap<>();
			result.put("<default>", (T) new Object() {
				/**
				 * {@return the implicit task dependencies of the source options}
				 */
				@InputFiles
				@PathSensitive(PathSensitivity.NONE)
				protected FileCollection getTaskDependencies() {
					return SourceOptionsSpec.this.getTaskDependencies();
				}
			});
			sources.visit(details -> {
				Opt<T> options = resolver().resolve(details.getFile());
				if (options != null) {
					result.put(quote(relativePath(details.getFile())), options.options);
				}
			});
			return result.entrySet();
		}

		private String relativePath(File sourceFile) {
			return getLayout().getProjectDirectory().getAsFile().toPath().relativize(sourceFile.toPath()).toString();
		}

		@Override
		public int size() {
			return Integer.MAX_VALUE; // See Spliterator#estimateSize()
		}

		@Internal
		@Override
		public Provider<Iterable<? extends SourceOptionBucket<T>>> getAllSelectedBuckets() {
			return getAllBuckets().map(it -> {
				Set<Integer> indices = new TreeSet<>();
				for (File source : sources) {
					ISourceKey key = resolver().resolveKey(source);
					if (key != null) {
						key.forEach(indices::add);
					}
				}

				// TODO: Restrict
				List<SourceOptionBucket<T>> result = new ArrayList<>();
				for (Integer index : indices) {
					result.add(it.get(index));
				}

				return result;
			});
		}

		@Override
		public ConfigurableSourceOptions<?> getRootSpec() {
			return SourceOptionsSpec.this;
		}

		@Override
		public File getSourceFile() {
			return sources.getSingleFile();
		}

		@Override
		public T getOptions() {
			return iterator().next().getOptions();
		}
		//endregion
	}
	//endregion

	@Inject
	protected abstract ProjectLayout getLayout();

	@Override
	public @NotNull Iterator<SourceFileOptions<T>> iterator() {
		return forFiles(getAllSelectedFiles().getAsFileTree()).iterator();
	}

	@Override
	public Iterable<Group<T>> groupedByOptions() {
		return forFiles(getAllSelectedFiles().getAsFileTree()).groupedByOptions();
	}

	//region Bucket transfer from one task to another
	private interface BucketAware<T> {
		Provider<Iterable<? extends SourceOptionBucket<T>>> getAllSelectedBuckets();
	}

	/**
	 * Adds the given source options to this source options.
	 * <pre class='autoTested'>
	 * def src = tasks.compileCpp
	 * tasks.register('otherCompileCpp', CppCompile) {
	 *   sourceOptions.with src.sourceOptions.forFiles(source)
	 * }
	 * </pre>
	 *
	 * @param sourceOptions the source options to add
	 * @return this source options
	 */
	@SuppressWarnings("unchecked")
	public SourceOptionsSpec<T> with(SourceOptions<T> sourceOptions) {
		getAllBuckets().addAll(((BucketAware<T>) sourceOptions).getAllSelectedBuckets());
		return this;
	}

	/**
	 * Adds the given source options to this source options.
	 *
	 * @param sourceOptionsProvider a provider to source options to add
	 * @return this source options
	 */
	@SuppressWarnings("unchecked")
	public SourceOptionsSpec<T> with(Provider<? extends SourceOptions<T>> sourceOptionsProvider) {
		getAllBuckets().addAll(sourceOptionsProvider.flatMap(it -> ((BucketAware<T>) it).getAllSelectedBuckets()).orElse(Collections.emptyList()));
		return this;
	}
	//endregion

	private static class MutableGroupedOptions<T> implements Group<T> {
		private final Set<File> sourceFiles = new LinkedHashSet<>();
		private final ISourceKey key;
		private final T options;

		public MutableGroupedOptions(ISourceKey key, T options) {
			this.key = key;
			this.options = options;
		}

		@Override
		public T getOptions() {
			return options;
		}

		@Override
		public Collection<File> getSourceFiles() {
			return Collections.unmodifiableCollection(sourceFiles);
		}

		@Override
		public String getUniqueId() {
			return key.asUniqueId();
		}

		@Override
		public @NotNull Iterator<SourceFileOptions<T>> iterator() {
			return getSourceFiles().stream()
				.map(sourceFile -> (SourceFileOptions<T>) new DefaultSourceFileOptions<>(sourceFile, options))
				.iterator();
		}

		@Override
		public String toString() {
			return "grouped options '" + getUniqueId() + "'";
		}
	}

	private static class DefaultSourceFileOptions<T> implements SourceFileOptions<T> {
		private final File sourceFile;
		private final T options;

		public DefaultSourceFileOptions(File sourceFile, T options) {
			this.sourceFile = sourceFile;
			this.options = options;
		}

		@Internal
		@Override
		public File getSourceFile() {
			return sourceFile;
		}

		@Nested
		@Override
		public T getOptions() {
			return options;
		}

		@Override
		public String toString() {
			return "options for '" + sourceFile + "'";
		}
	}

	//region A key representing the selected buckets
	private interface ISourceKey extends Iterable<Integer> {
		String asUniqueId();
		<T> Iterable<SourceOptionBucket<T>> restrict(List<? extends SourceOptionBucket<T>> buckets);
	}

	private static class Key implements Iterable<Integer>, ISourceKey {
		private final int[] indices;

		private Key(int[] indices) {
			this.indices = indices;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			Key key = (Key) o;
			return Arrays.equals(indices, key.indices);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(indices);
		}

		@Override
		public String toString() {
			return "Key{" + Arrays.toString(indices) + "}";
		}

		@Override
		public @NotNull Iterator<Integer> iterator() {
			return Arrays.stream(indices).iterator();
		}

		@Override
		public String asUniqueId() {
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("MD5");
				for (int i : indices) {
					messageDigest.update(ByteBuffer.allocate(4).putInt(i).array());
				}
				return new BigInteger(1, messageDigest.digest()).toString(36);
			} catch (NoSuchAlgorithmException e) {
				throw UncheckedException.throwAsUncheckedException(e);
			}
		}

		@Override
		public <T> Iterable<SourceOptionBucket<T>> restrict(List<? extends SourceOptionBucket<T>> buckets) {
			return new Iterable<SourceOptionBucket<T>>() {
				@Override
				public @NotNull Iterator<SourceOptionBucket<T>> iterator() {
					return new Iterator<SourceOptionBucket<T>>() {
						private int i = 0;
						@Override
						public boolean hasNext() {
							return i < indices.length;
						}

						@Override
						public SourceOptionBucket<T> next() {
							return buckets.get(indices[i++]);
						}
					};
				}
			};
		}
	}
	//endregion

	private static String quote(String s) {
		return "[" + s + "]";
	}

	// Represents an opaque source configuration (a set of source configured by some action)
	private interface SourceConfiguration {}

}

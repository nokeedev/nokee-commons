package dev.nokee.commons.gradle.tasks.options;

import dev.nokee.commons.gradle.Factory;
import org.gradle.api.Action;
import org.gradle.api.file.FileTree;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderConvertible;
import org.gradle.api.reflect.TypeOf;

import javax.inject.Inject;
import java.io.File;

import static dev.nokee.commons.gradle.TransformerUtils.traverse;

public abstract class AllSourceOptionsEx2<T> implements ProviderConvertible<Iterable<? extends SourceConfiguration>> {
	private final ListProperty<Entry<T>> entriesProp;
	private final Property<Entries<T>> entries;
	private final Property<OptionCache<T>> options;
	private final ObjectFactory objects;
	private final Property<CacheableEntries<T>> cEntries;

	@Inject
	public AllSourceOptionsEx2(ObjectFactory objects, Factory<T> optionsFactory) {
		this.entriesProp = objects.listProperty(new TypeOf<Entry<T>>() {}.getConcreteClass());
		this.entriesProp.finalizeValueOnRead();

		this.objects = objects;

		this.entries = objects.property(new TypeOf<Entries<T>>() {}.getConcreteClass());
		this.entries.set(entriesProp.map(Entries::new));
		this.entries.finalizeValueOnRead();
		this.entries.disallowChanges();

		this.options = objects.property(new TypeOf<OptionCache<T>>() {}.getConcreteClass());
		this.options.set(entries.map(it -> new OptionCache<>(new FactoryLookup<>(it, optionsFactory))));
		this.options.finalizeValueOnRead();
		this.options.disallowChanges();

		this.cEntries = objects.property(new TypeOf<CacheableEntries<T>>() {}.getConcreteClass());
		this.cEntries.set(entries.map(it -> new CacheableEntries<>(it, options.get())));
		this.cEntries.finalizeValueOnRead();
		this.cEntries.disallowChanges();
	}

	public void configure(Object sourcePaths, Action<? super T> configureAction) {
		entriesProp.add(new Entry<>(objects.fileCollection().from(sourcePaths), configureAction));
	}

	// TODO: We may need to disallow lambda action. We should validate.
	public AllSourceOptionsEx2<T> from(ProviderConvertible<Iterable<? extends SourceConfiguration>> otherSourceConfiguration) {
		entriesProp.addAll(otherSourceConfiguration.asProvider().map(traverse(Entry.class::cast)));
		return this;
	}

	public Provider<ISourceKey> keyOf(File sourceFile) {
		return entries.map(it -> it.forFile(sourceFile));
	}

	public Provider<T> forKey(ISourceKey key) {
		return options.map(it -> it.get(key));
	}

	public Provider<T> forFile(File sourceFile) {
		return cEntries.map(it -> it.forFile(sourceFile).get());
	}

	public Provider<OptionsIter<T>> forAllSources(FileTree sourceFiles) {
		return cEntries.map(it -> new DefaultOptionsIter<>(sourceFiles, it, objects));
	}

	public Provider<OptionsIter<T>> forAllSources(Iterable<File> sourceFiles) {
		return cEntries.map(it -> new DefaultOptionsIter<>(sourceFiles, it, objects));
	}

	public Provider<Iterable<? extends SourceConfiguration>> asProvider() {
		return entriesProp.map(traverse(SourceConfiguration.class::cast));
	}
}

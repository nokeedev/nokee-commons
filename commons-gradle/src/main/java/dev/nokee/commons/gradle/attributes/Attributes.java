package dev.nokee.commons.gradle.attributes;

import org.gradle.api.Action;
import org.gradle.api.Named;
import org.gradle.api.NonExtensible;
import org.gradle.api.artifacts.result.ResolvedVariantResult;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.attributes.HasAttributes;
import org.gradle.api.attributes.HasConfigurableAttributes;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderConvertible;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.specs.Spec;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class aims at offering a more consist and errorless DSL for the AttributeContainer.
 * Mutation-only:
 * - Inconsistent APIs (attribute vs attributeProvider)
 * - Missing API for ProviderConvertible
 * - Introduce AttributesProvider (for consumable and resolvable)
 * - Avoid piping the ObjectFactory for Named element
 * - Introduce AttributeMap[Property] to make attribute collection easier to manage
 *
 * Read-only:
 * - Provider of all attributes
 * - Deal with inconsistency between remote metadata and Gradle metadata (Named are treated as String, Enum treated as String, etc.)
 * - Convert all attributes to map
 * - Easier attribute auditing
 *
 * Clear distinction between mutation/read as it's enforced.
 * Essentially, there is a lot of decision-making
 *
 * The attributes view is a read-only adapter over standard {@link AttributeContainer} and interface for something that has attributes such as {@link MapProperty}.
 */
public abstract /*final*/ class Attributes {
	private final ObjectFactory objects;

	protected Attributes(ObjectFactory objects) {
		this.objects = objects;
	}

	/**
	 * For convenience
	 * Note: Back support with {@link AttributeContainer#getAttribute(Attribute)} ()}.
	 * Note: This API will account for attribute type mismatch.
	 */
	@Nullable
	public final <T> T getAttribute(Attribute<T> attribute) {
		return get(attribute).getOrNull();
	}

	/**
	 * Return a value provider that take cares of attribute type mismatch.
	 */
	public abstract <T> ValueProvider<T> get(Attribute<T> attribute);

	/**
	 * Returns the known attributes.
	 * Note: Back support with {@link AttributeContainer#keySet()}.
	 */
	public abstract Set<Attribute<?>> keySet();

	/**
	 * Filter these attributes with the specified specification.
	 * The returned value is live and will change depending on the underlying attributes.
	 *
	 * @param spec  the filter specification, must not be null
	 * @return a live view of the filtered attributes, never null
	 */
	public final Attributes filter(Spec<? super Attribute<?>> spec) {
		return objects.newInstance(FilteredAttributes.class, spec, this);
	}

	/**
	 * Returns the attributes as a standard Map of attribute to its value.
	 */
	public abstract Provider<Map<Attribute<?>, ?>> getAsMap();

	@SuppressWarnings({"unchecked", "rawtypes"})
	private void addTo(MinimalAttributeContainer attributes) {
		// TODO: It should be live and added attributes be automatically communicated to the AttributeContainer
		// TODO: On MapProperty, make sure there is no task dependencies, we wont be able to honor it.
		keySet().forEach(key -> attributes.attribute((Attribute) key, get(key).asProvider()));
	}

	public static abstract /*final*/ class ValueProvider<T> {
		private final Attribute<T> key;
		private final Provider<T> value;

		@Inject
		public ValueProvider(Attribute<T> key, Provider<T> value) {
			this.key = key;
			this.value = value;
		}

		public boolean equalTo(Object o) {
			@Nullable
			final Object value = asProvider().getOrNull();
			if (value == null) {
				return false;
			}

			// TODO: We should account for all convertible scenarios:
			//   Named input:
			//    Named <-> String: convenient
			//    Named <-> Named: normal mode
			//    String <-> Named: unserialized data
			//   Enum input:
			//    Enum <-> String: convenient
			//    Enum <-> Enum: normal mode
			//    String <-> Enum: unserialized data
			if (Named.class.isAssignableFrom(key.getType()) && o instanceof String) {
				return ((Named) value).getName().equals(o);
			} else if (Enum.class.isAssignableFrom(key.getType()) && o instanceof String) {
				return ((Enum<?>) value).name().equals(o);
			} else if (String.class.isAssignableFrom(key.getType())) {
				if (o instanceof Named) {
					return value.equals(((Named) o).getName());
				} else if (o instanceof Enum) {
					return value.equals(((Enum<?>) o).name());
				}
			}
			return value.equals(o);
		}

		public T get() {
			return asProvider().get();
		}

		public T getOrNull() {
			return asProvider().getOrNull();
		}

		public boolean equals(Object o) {
			throw new UnsupportedOperationException("Attribute values are not expected to be captured.");
		}

		public int hashCode() {
			throw new UnsupportedOperationException("Attribute values are not expected to be captured.");
		}

		public Provider<T> asProvider() {
			return value;
		}

		@Override
		public String toString() {
			return Optional.ofNullable(asProvider().getOrNull()).map(Object::toString).orElse("<null>");
		}
	}

	/*private*/ static abstract /*final*/ class FilteredAttributes extends Attributes {
		private final ObjectFactory objects;
		private final ProviderFactory providers;
		private final Spec<? super Attribute<?>> spec;
		private final Attributes delegate;

		@Inject
		public FilteredAttributes(ObjectFactory objects, ProviderFactory providers, Spec<? super Attribute<?>> spec, Attributes delegate) {
			super(objects);
			this.objects = objects;
			this.providers = providers;
			this.spec = Objects.requireNonNull(spec, "'spec' must not be null");
			this.delegate = delegate;
		}

		@Override
		public <T> ValueProvider<T> get(Attribute<T> attribute) {
			final Provider<T> value = providers.provider(() -> {
				if (spec.isSatisfiedBy(attribute)) {
					return delegate.getAttribute(attribute);
				}
				return null;
			});

			@SuppressWarnings("unchecked")
			ValueProvider<T> result = objects.newInstance(ValueProvider.class, attribute, value);
			return result;
		}

		@Override
		public Set<Attribute<?>> keySet() {
			return delegate.keySet().stream().filter(spec::isSatisfiedBy).collect(Collectors.toCollection(LinkedHashSet::new));
		}

		@Override
		public Provider<Map<Attribute<?>, ?>> getAsMap() {
			return delegate.getAsMap().map(values -> {
				final Map<Attribute<?>, Object> result = new LinkedHashMap<>();
				values.forEach((key, value) -> {
					if (spec.isSatisfiedBy(key)) {
						result.put(key, value);
					}
				});
				return result;
			});
		}
	}

	/*private*/ static abstract /*final*/ class AttributeContainerAdapter extends Attributes {
		private final ObjectFactory objects;
		private final ProviderFactory providers;
		private final AttributeContainer delegate;

		@Inject
		public AttributeContainerAdapter(ObjectFactory objects, ProviderFactory providers, AttributeContainer delegate) {
			super(objects);
			this.objects = objects;
			this.providers = providers;
			this.delegate = delegate;
		}

		public <T> ValueProvider<T> get(Attribute<T> attribute) {
			// TODO: Should we normalize or allow normalization of the attributes
			//   When querying them, everything is mostly fine, but when we do getAsMap(), keySet(), etc.
			//   Those method will return unnormalized attributes which can cause some problems.

			// TODO: We should at the very least, provide some exception that will inform what was the issue.
			//   Maybe we should just allow full back and forward convertion between Named <-> Enum <-> String
			//   Meaning String, Named or Enum are just different ways to represent the same thing.
			//   It could means that we should normalize Attribute to String.class event if it was a Named or Enum
			//   If you specify an attribute String but a Named or Enum, we try to cast the attribute into the "more restrictive"
			//   If we specify an Named attribute when a String already exists, we replace our known attribute for the Named variant
			//   This means that we should allow specifying Enum and Named attribute using plain string in AttributeBuilder
			final Provider<T> value = providers.provider(() -> {
				T result = delegate.getAttribute(attribute);
				if (result == null) {
					Attribute<?> a = delegate.keySet().stream() //
						.filter(it -> it.getName().equals(attribute.getName())).findFirst()
						.orElse(null);

					if (a != null && a.getType().equals(String.class)) {
						// maybe a dehydrated attribute value
						if (Named.class.isAssignableFrom(attribute.getType())) {
							result = (T) objects.named((Class<Named>) attribute.getType(), (String) delegate.getAttribute(a));
						} else if (Enum.class.isAssignableFrom(attribute.getType())) {
							result = (T) Enum.valueOf((Class<Enum>) attribute.getType(), (String) delegate.getAttribute(a));
						}
						// TODO: Figure out if boolean and integer attribute needs to be rehydrated
					}
				}

				return result;
			});

			@SuppressWarnings("unchecked")
			final ValueProvider<T> result = objects.newInstance(ValueProvider.class, attribute, value);
			return result;
		}

		@Override
		public Set<Attribute<?>> keySet() {
			return delegate.keySet();
		}

		@Override
		public Provider<Map<Attribute<?>, ?>> getAsMap() {
			return providers.provider(() -> {
				final Map<Attribute<?>, Object> result = new LinkedHashMap<>();
				for (Attribute<?> key : delegate.keySet()) {
					result.put(key, delegate.getAttribute(key));
				}
				return result;
			});
		}
	}


	@NonExtensible
	public static abstract class MapProperty extends Attributes {
		private final Set<Attribute<?>> knownAttributes = new LinkedHashSet<>();
		private final ObjectFactory objects;

		@SuppressWarnings("rawtypes")
		private final org.gradle.api.provider.MapProperty<Attribute, Object> values;

		@Inject
		public MapProperty(ObjectFactory objects) {
			super(objects);
			this.objects = objects;
			this.values = objects.mapProperty(Attribute.class, Object.class);
		}

		public void configure(Action<? super Details> action) {
			action.execute(objects.newInstance(Details.class, new MinimalAttributeContainer() {
				@Override
				public <T> void attribute(Attribute<T> key, T value) {
					knownAttributes.add(key);
					values.put(key, value);
				}

				@Override
				public <T> void attributeProvider(Attribute<T> key, Provider<? extends T> value) {
					knownAttributes.add(key);
					values.put(key, value);
				}
			}));
		}

		@Override
		public <T> ValueProvider<T> get(Attribute<T> attribute) {
			@SuppressWarnings("unchecked")
			ValueProvider<T> result = objects.newInstance(ValueProvider.class, attribute, values.getting(attribute));
			return result;
		}

		@Override
		public Set<Attribute<?>> keySet() {
			return knownAttributes;
		}

		@Override
		public Provider<Map<Attribute<?>, ?>> getAsMap() {
			return values.map(it -> {
				final Map<Attribute<?>, Object> result = new LinkedHashMap<>();
				it.forEach(result::put);
				return result;
			});
		}
	}

	private interface MinimalAttributeContainer {
		<T> void attribute(Attribute<T> key, T value);
		<T> void attributeProvider(Attribute<T> key, Provider<? extends T> value);
	}

	@NonExtensible
	public static class Details {
		private static final Attribute<String> ARTIFACT_TYPE_ATTRIBUTE = Attribute.of("artifactType", String.class);
		private final ObjectFactory objects;
		private final ProviderFactory providers;
		private final MinimalAttributeContainer attributes;

		@Inject
		public Details(ObjectFactory objects, ProviderFactory providers, MinimalAttributeContainer attributes) {
			this.objects = objects;
			this.providers = providers;
			this.attributes = attributes;
		}

		public <T> AttributeBuilder<T> attribute(Attribute<T> key) {
			return new AttributeBuilder<>(key);
		}

		// Backward convenience
		//   If you are using Gradle 7.3+, use ArtifactTypeDefinition.ARTIFACT_TYPE_ATTRIBUTE
		public Attribute<String> artifactType() {
			return ARTIFACT_TYPE_ATTRIBUTE;
		}

		// For convenience
		public <T> Details attribute(Attribute<T> key, T value) {
			attributes.attribute(key, value);
			return this;
		}

		// Fix the stupid API change
		public <T> Details attribute(Attribute<T> key, Provider<? extends T> value) {
			attributes.attributeProvider(key, providers.provider(() -> {
				if (!objects.fileCollection().from(value).getBuildDependencies().getDependencies(null).isEmpty()) {
					throw new IllegalArgumentException("Dependencies not supported");
				}
				return value;
			}).flatMap(it -> it));
			return this;
		}

		// Support ProviderConvertible
		public <T> Details attribute(Attribute<T> key, ProviderConvertible<? extends T> value) {
			attributes.attributeProvider(key, providers.provider(() -> {
				final Provider<? extends T> provider = value.asProvider();
				if (!objects.fileCollection().from(provider).getBuildDependencies().getDependencies(null).isEmpty()) {
					throw new IllegalArgumentException("Dependencies not supported");
				}
				return value.asProvider();
			}).flatMap(it -> it));
			return this;
		}

		// Support attributes from another source
		public Details attributes(Attributes attributes) {
			attributes.addTo(this.attributes);
			return this;
		}

		public class AttributeBuilder<T> {
			private final Attribute<T> attribute;

			public AttributeBuilder(Attribute<T> attribute) {
				this.attribute = attribute;
			}

			// For convenience
			public void named(String name) {
				if (!Named.class.isAssignableFrom(attribute.getType())) {
					throw new UnsupportedOperationException("only attributes of Named object can use this convenient method");
				}

				@SuppressWarnings("unchecked")
				final Attribute<Named> key = (Attribute<Named>) attribute;
				attribute(key, objects.named(key.getType(), name));
			}

			public void of(CharSequence value) {
				if (Enum.class.isAssignableFrom(attribute.getType())) {
					@SuppressWarnings({"unchecked", "rawtypes"}) final Attribute<Enum> key = (Attribute<Enum>) attribute;
					attribute(key, Enum.valueOf(key.getType(), value.toString()));
				} else if (Named.class.isAssignableFrom(attribute.getType())) {
					@SuppressWarnings("unchecked")
					final Attribute<Named> key = (Attribute<Named>) attribute;
					attribute(key, objects.named(key.getType(), value.toString()));
				} else if (String.class.isAssignableFrom(attribute.getType())) {
					@SuppressWarnings("unchecked")
					final Attribute<String> key = (Attribute<String>) attribute;
					attribute(key, value.toString());
				} else {
					throw new UnsupportedOperationException("only string-like attributes (e.g. Named, Enum, String) can use this convenient method");
				}
			}

			public void of(T value) {
				attribute(attribute, value);
			}

			public void of(Provider<? extends T> value) {
				attribute(attribute, value);
			}

			public void of(ProviderConvertible<? extends T> value) {
				attribute(attribute, value);
			}
		}
	}

	/**
	 * Extension that you can register in your projects.
	 *
	 * <code>
	 * ext.attributesOf = extensions.create("attributes", Attributes.Extension).&amp;of
	 *
	 * dependencies {
	 *     implementation(...) {
	 *         attributesOf(it) {
	 *             attribute ... named '...'
	 *             attribute ... of ...
	 *         }
	 *     }
	 * }
	 *
	 * def attrs = attributes.mapProperty()
	 * afterEvaluate {
	 *     attributes.of(configurations.foo).attributes(attrs)
	 * }
	 * </code>
	 */
	@NonExtensible
	public static abstract /*final*/ class Extension {
		private final ObjectFactory objects;

		@Inject
		public Extension(ObjectFactory objects) {
			this.objects = objects;
		}

		/**
		 * Factory method to create a property that collects attributes.
		 */
		public MapProperty mapProperty() {
			return objects.newInstance(MapProperty.class);
		}

		// For VariantMetadata - adapt compatible
		// For ComponentMetadataDetails - adapt compatible
		// For Configuration
		public Attributes of(HasAttributes obj) {
			return objects.newInstance(AttributeContainerAdapter.class, obj.getAttributes());
		}

		// adapt compatible
		public Attributes of(ResolvedVariantResult result) {
			return objects.newInstance(AttributeContainerAdapter.class, result.getAttributes());
		}

		public void of(HasConfigurableAttributes<?> self, Action<? super Details> action) {
			self.attributes(attributes -> {
				action.execute(objects.newInstance(Details.class, new MinimalAttributeContainer() {
					@Override
					public <T> void attribute(Attribute<T> key, T value) {
						attributes.attribute(key, value);
					}

					@Override
					public <T> void attributeProvider(Attribute<T> key, Provider<? extends T> value) {
						attributes.attributeProvider(key, value);
					}
				}));
			});
		}
	}
}

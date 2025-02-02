//package dev.nokee.commons.provider;
//
//import org.gradle.api.model.ObjectFactory;
//import org.gradle.api.provider.Provider;
//import org.gradle.api.provider.ProviderFactory;
//import org.gradle.api.reflect.TypeOf;
//import org.gradle.testfixtures.ProjectBuilder;
//import org.junit.jupiter.api.Test;
//
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static dev.nokee.commons.hamcrest.gradle.FileSystemMatchers.aFile;
//import static dev.nokee.commons.hamcrest.gradle.FileSystemMatchers.withAbsolutePath;
//import static dev.nokee.commons.hamcrest.gradle.provider.ProviderOfMatcher.providerOf;
//import static dev.nokee.commons.provider.CollectionElementTransformer.transformEach;
//import static dev.nokee.commons.provider.FlattenTransformer.alice;
//import static dev.nokee.commons.provider.FlattenTransformer.bob;
//import static dev.nokee.commons.provider.FlattenTransformer.flatten;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.allOf;
//import static org.hamcrest.Matchers.contains;
//import static org.hamcrest.Matchers.emptyCollectionOf;
//import static org.hamcrest.Matchers.endsWith;
//import static org.hamcrest.Matchers.instanceOf;
//
//public class FlattenTransformerTests {
//	static ObjectFactory objectFactory() {
//		return ProjectBuilder.builder().build().getObjects();
//	}
//
//	static ProviderFactory providerFactory() {
//		return ProjectBuilder.builder().build().getProviders();
//	}
//
//	@Test
//	void g() {
//		assertThat(new FlattenTransformer<>(Object.class, bob(String.class), FlattenTransformer.DEFAULT_FLATTENER).transform(Arrays.asList("foo", "bar")), contains("foo", "bar"));
//	}
//
//	@Test
//	void gggg() {
//		assertThat(new FlattenTransformer<>(Object.class, bob(String.class), FlattenTransformer.DEFAULT_FLATTENER).transform(Arrays.asList(Collections.singletonList("foo"), Arrays.asList("bar", "far"))), contains("foo", "bar", "far"));
//	}
//
//	@Test
//	void canFlattenListOfJavaPath() {
//		assertThat(new FlattenTransformer<>(Path.class, bob(Path.class), FlattenTransformer.DEFAULT_FLATTENER).transform(Arrays.asList(Collections.singletonList(Paths.get("foo.txt")), Arrays.asList(Paths.get("bar/far.txt")))), contains(aFile(withAbsolutePath(endsWith("/foo.txt"))), aFile(withAbsolutePath(endsWith("/bar/far.txt")))));
//	}
//
//	@Test
//	void canFlattenNullObject() {
//		assertThat(new FlattenTransformer<>(Object.class, bob(String.class), FlattenTransformer.DEFAULT_FLATTENER).transform(null),
//			allOf(instanceOf(List.class), emptyCollectionOf(String.class)));
//	}
//
//	@Test
//	void canFlattenEmptyList() {
//		assertThat(new FlattenTransformer<>(Object.class, bob(String.class), FlattenTransformer.DEFAULT_FLATTENER).transform(Collections.<String>emptyList()),
//			allOf(instanceOf(List.class), emptyCollectionOf(String.class)));
//	}
//
//	@Test
//	void canFlattenEmptySet() {
//		assertThat(new FlattenTransformer<>(Object.class, bob(String.class), FlattenTransformer.DEFAULT_FLATTENER).transform(Collections.<String>emptySet()),
//			allOf(instanceOf(List.class), emptyCollectionOf(String.class)));
//	}
//
//
//
//	@Test
//	void normalFlatteningOfNestedProviders() {
//		assertThat(new FlattenTransformer<>(Object.class, bob(new TypeOf<Provider<String>>() {}), FlattenTransformer.DEFAULT_FLATTENER).transform(Arrays.asList(providerFactory().provider(() -> "foo"), providerFactory().provider(() -> "bar"))),
//			allOf(instanceOf(List.class), contains(providerOf("foo"), providerOf("bar"))));
//	}
//
//
//
//
//	@Test
//	void gg() {
//		assertThat(new FlattenTransformer<>(Object.class, alice(objectFactory(), String.class), FlattenTransformer.DEFAULT_FLATTENER).transform(Arrays.asList(providerFactory().provider(() -> "foo"), providerFactory().provider(() -> "bar"))), providerOf(contains("foo", "bar")));
//	}
//
//
//
//
//
//
//
//	@Test
//	void gsfd() {
//		flatten(transformEach((String it) -> Collections.singletonList(it))).toList().transform(Arrays.asList("foo", "bar"));
//	}
//}

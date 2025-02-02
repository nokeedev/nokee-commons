//package dev.nokee.commons.provider;
//
//import org.gradle.api.model.ObjectFactory;
//import org.gradle.api.provider.Provider;
//import org.gradle.api.provider.ProviderFactory;
//import org.gradle.testfixtures.ProjectBuilder;
//import org.junit.jupiter.api.Test;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Set;
//
//import static dev.nokee.commons.hamcrest.gradle.provider.ProviderOfMatcher.providerOf;
//import static dev.nokee.commons.provider.ProviderOfCollectionTransformer.toProviderOfCollection;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.allOf;
//import static org.hamcrest.Matchers.contains;
//import static org.hamcrest.Matchers.instanceOf;
//
//class ProviderOfCollectionTransformerTests {
//	static ObjectFactory objectFactory() {
//		return ProjectBuilder.builder().build().getObjects();
//	}
//
//	static ProviderFactory providerFactory() {
//		return ProjectBuilder.builder().build().getProviders();
//	}
//
//	Provider<List<Provider<String>>> provider = providerFactory().provider(() -> Arrays.asList(providerFactory().provider(() -> "foo"), providerFactory().provider(() -> "bar"), objectFactory().property(String.class).value("far")));
//
//	@Test
//	void canTransformCollectionOfProviderToList() {
//		assertThat(provider.flatMap(toProviderOfCollection(objectFactory()::listProperty)),
//			providerOf(allOf(instanceOf(List.class), contains("foo", "bar", "far"))));
//	}
//
//	@Test
//	void canTransformCollectionOfProviderToSet() {
//		assertThat(provider.flatMap(toProviderOfCollection(objectFactory()::setProperty)),
//			providerOf(allOf(instanceOf(Set.class), contains("foo", "bar", "far"))));
//	}
//}

package dev.nokee.commons.names;

import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.publish.Publication;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static dev.nokee.commons.hamcrest.gradle.SpecMatchers.satisfiedBy;
import static dev.nokee.commons.names.PublishingTaskNames.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

class PublishingTaskNamesTests {
	PublishingTaskNames subject = new PublishingTaskNames();
	Publication publication = newPublication("cpp");

	private static Publication newPublication(String name) {
		Publication result = Mockito.mock(Publication.class);
		Mockito.when(result.getName()).thenReturn(name);
		return result;
	}

	private static ArtifactRepository newRepository(String name) {
		ArtifactRepository result = Mockito.mock(ArtifactRepository.class);
		Mockito.when(result.getName()).thenReturn(name);
		return result;
	}

	@Test
	void testGenerateMetadataFileTaskName() {
		assertThat(generateMetadataFileTaskName(publication), equalTo("generateMetadataFileForCppPublication"));
	}

	@Test
	void testGeneratePomFileTaskName() {
		assertThat(generatePomFileTaskName(publication), equalTo("generatePomFileForCppPublication"));
	}

	@Test
	void testGenerateDescriptorFileTaskName() {
		assertThat(generateDescriptorFileTaskName(publication), equalTo("generateDescriptorFileForCppPublication"));
	}

	@Test
	void testPublishPublicationToRepositoryTaskName() {
		assertThat(publishPublicationToRepositoryTaskName(publication, newRepository("MyRepo")), equalTo("publishCppPublicationToMyRepoRepository"));
	}

	@Test
	void testPublishPublicationToAnyRepositoriesSpec() {
		assertThat(publishPublicationToAnyRepositories(publication), satisfiedBy("publishCppPublicationToMavenLocal"));
		assertThat(publishPublicationToAnyRepositories(publication), satisfiedBy("publishCppPublicationToMavenRepository"));
		assertThat(publishPublicationToAnyRepositories(publication), satisfiedBy("publishCppPublicationToMyRepoRepository"));

		assertThat(publishPublicationToAnyRepositories(publication), not(satisfiedBy("publishCppDebugPublicationToMyRepoRepository")));
	}
}

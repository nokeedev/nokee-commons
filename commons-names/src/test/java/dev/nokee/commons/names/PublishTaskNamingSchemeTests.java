package dev.nokee.commons.names;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static dev.nokee.commons.names.PublishingTaskNamingScheme.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class PublishTaskNamingSchemeTests {
	@Test
	void testGeneratePomFile() {
		assertThat(GeneratePomFile.format(newName(Map.of("publicationName", "cpp"))), equalTo("generatePomFileForCppPublication"));
	}

	@Test
	void testGenerateMetadataFile() {
		assertThat(GenerateMetadataFile.format(newName(Map.of("publicationName", "cpp"))), equalTo("generateMetadataFileForCppPublication"));
	}

	@Test
	void testGenerateDescriptorFile() {
		assertThat(GenerateDescriptorFile.format(newName(Map.of("publicationName", "cpp"))), equalTo("generateDescriptorFileForCppPublication"));
	}

	@Test
	void testPublishPublicationToMavenLocal() {
		assertThat(PublishPublicationToMavenLocal.format(newName(Map.of("publicationName", "cpp"))), equalTo("publishCppPublicationToMavenLocal"));
	}

	@Test
	void testPublishPublicationToRepository() {
		assertThat(PublishPublicationToRepository.format(newName(Map.of("publicationName", "cpp", "repositoryName", "myOrg"))), equalTo("publishCppPublicationToMyOrgRepository"));
	}

	@Test
	void testPublishAllPublicationsToRepository() {
		assertThat(PublishAllPublicationsToRepository.format(newName(Map.of("repositoryName", "myOrg"))), equalTo("publishAllPublicationsToMyOrgRepository"));
	}

	private static Name newName(Map<String, String> properties) {
		return new Name() {
			@Override
			public Object get(String propertyName) {
				return properties.get(propertyName);
			}
		};
	}
}

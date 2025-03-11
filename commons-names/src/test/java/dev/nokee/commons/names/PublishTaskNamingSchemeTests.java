package dev.nokee.commons.names;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static dev.nokee.commons.hamcrest.gradle.ThrowableMatchers.throwsException;
import static dev.nokee.commons.names.PublishingTaskNamingScheme.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;

class PublishTaskNamingSchemeTests {
	private interface PublicationTester {
		NamingSchemeTester subject();

		@Test
		default void throwsExceptionOnNullPublicationName() {
			assertThat(subject().without("publicationName")::format, throwsException(isA(RuntimeException.class)));
		}
	}

	private interface RepositoryTester {
		NamingSchemeTester subject();

		@Test
		default void throwsExceptionOnNullRepositoryName() {
			assertThat(subject().without("repositoryName")::format, throwsException(isA(RuntimeException.class)));
		}
	}

	@Nested
	class GeneratePomFileTests implements PublicationTester {
		@Test
		void testGeneratePomFile() {
			assertThat(subject().format(), equalTo("generatePomFileForCppPublication"));
		}

		@Override
		public NamingSchemeTester subject() {
			return new NamingSchemeTester(GeneratePomFile, Map.of("publicationName", "cpp"));
		}
	}

	@Nested
	class GenerateMetadataFileTests implements PublicationTester {
		@Test
		void testGenerateMetadataFile() {
			assertThat(subject().format(), equalTo("generateMetadataFileForCppPublication"));
		}

		@Override
		public NamingSchemeTester subject() {
			return new NamingSchemeTester(GenerateMetadataFile, Map.of("publicationName", "cpp"));
		}
	}

	@Nested
	class GenerateDescriptorFileTests implements PublicationTester {
		@Test
		void testGenerateDescriptorFile() {
			assertThat(subject().format(), equalTo("generateDescriptorFileForCppPublication"));
		}

		@Override
		public NamingSchemeTester subject() {
			return new NamingSchemeTester(GenerateDescriptorFile, Map.of("publicationName", "cpp"));
		}
	}

	@Nested
	class PublishPublicationToMavenLocalTests implements PublicationTester {
		@Test
		void testPublishPublicationToMavenLocal() {
			assertThat(subject().format(), equalTo("publishCppPublicationToMavenLocal"));
		}

		@Override
		public NamingSchemeTester subject() {
			return new NamingSchemeTester(PublishPublicationToMavenLocal, Map.of("publicationName", "cpp"));
		}
	}

	@Nested
	class PublishPublicationToRepositoryTests implements PublicationTester, RepositoryTester {
		@Test
		void testPublishPublicationToRepository() {
			assertThat(subject().format(), equalTo("publishCppPublicationToMyOrgRepository"));
		}

		@Override
		public NamingSchemeTester subject() {
			return new NamingSchemeTester(PublishPublicationToRepository, Map.of("publicationName", "cpp", "repositoryName", "myOrg"));
		}
	}

	@Nested
	class PublishAllPublicationsToRepository implements RepositoryTester {
		@Test
		void testPublishAllPublicationsToRepository() {
			assertThat(subject().format(), equalTo("publishAllPublicationsToMyOrgRepository"));
		}

		@Override
		public NamingSchemeTester subject() {
			return new NamingSchemeTester(PublishAllPublicationsToRepository, Map.of("repositoryName", "myOrg"));
		}
	}
}

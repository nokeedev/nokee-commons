package dev.nokee.commons.names;

import org.gradle.api.artifacts.repositories.ArtifactRepository;
import org.gradle.api.publish.Publication;
import org.gradle.api.specs.Spec;

import static dev.nokee.commons.names.StringUtils.capitalize;

/**
 * Task names for {@literal maven-publish} and {@literal ivy-publish} publications.
 */
public final class PublishingTaskNames {
	private static final PublishingTaskNames INSTANCE = new PublishingTaskNames();

	interface PublishingTaskName extends Name {}

	public static final class GenerateMetadataFileTaskName extends NameSupport<GenerateMetadataFileTaskName> implements PublishingTaskName, QualifiedName {
		private final String publicationName;

		private GenerateMetadataFileTaskName(String publicationName) {
			this.publicationName = publicationName;
		}

		@Override
		Prop<GenerateMetadataFileTaskName> init() {
			return new Prop.Builder<>(GenerateMetadataFileTaskName.class)
				.with("publicationName", this::withPublicationName)
				.build();
		}

		public GenerateMetadataFileTaskName withPublicationName(String publicationName) {
			return new GenerateMetadataFileTaskName(publicationName);
		}

		public String getPublicationName() {
			return publicationName;
		}

		@Override
		public String toString() {
			// generateMetadataFileFor<PublicationName>Publication
			return "generateMetadataFileFor" + capitalize(publicationName) + "Publication";
		}
	}

	public interface ForPublicationBuilder<T extends PublishingTaskName> {
		T forPublication(Publication publication);
	}

	public ForPublicationBuilder<GenerateMetadataFileTaskName> generateMetadataFileTaskName() {
		return publication -> new GenerateMetadataFileTaskName(publication.getName());
	}

	public static String generateMetadataFileTaskName(Publication publication) {
		return INSTANCE.generateMetadataFileTaskName().forPublication(publication).toString();
	}

	public static final class GeneratePomFileTaskName extends NameSupport<GeneratePomFileTaskName> implements PublishingTaskName, QualifiedName {
		private final String publicationName;

		private GeneratePomFileTaskName(String publicationName) {
			this.publicationName = publicationName;
		}

		@Override
		Prop<GeneratePomFileTaskName> init() {
			return new Prop.Builder<>(GeneratePomFileTaskName.class)
				.with("publicationName", this::withPublicationName)
				.build();
		}

		public GeneratePomFileTaskName withPublicationName(String publicationName) {
			return new GeneratePomFileTaskName(publicationName);
		}

		public String getPublicationName() {
			return publicationName;
		}

		@Override
		public String toString() {
			// generatePomFileFor<PublicationName>Publication
			return "generatePomFileFor" + capitalize(publicationName) + "Publication";
		}
	}

	public ForPublicationBuilder<GeneratePomFileTaskName> generatePomFileTaskName() {
		return publication -> new GeneratePomFileTaskName(publication.getName());
	}

	public static String generatePomFileTaskName(Publication publication) {
		return INSTANCE.generatePomFileTaskName().forPublication(publication).toString();
	}

	public static final class GenerateDescriptorFileTaskName extends NameSupport<GenerateDescriptorFileTaskName> implements PublishingTaskName, QualifiedName {
		private final String publicationName;

		private GenerateDescriptorFileTaskName(String publicationName) {
			this.publicationName = publicationName;
		}

		@Override
		Prop<GenerateDescriptorFileTaskName> init() {
			return new Prop.Builder<>(GenerateDescriptorFileTaskName.class)
				.with("publicationName", this::withPublicationName)
				.build();
		}

		public GenerateDescriptorFileTaskName withPublicationName(String publicationName) {
			return new GenerateDescriptorFileTaskName(publicationName);
		}

		public String getPublicationName() {
			return publicationName;
		}

		@Override
		public String toString() {
			// generateDescriptorFileFor<PublicationName>Publication
			return "generateDescriptorFileFor" + capitalize(publicationName) + "Publication";
		}
	}

	public ForPublicationBuilder<GenerateDescriptorFileTaskName> generateDescriptorFileTaskName() {
		return publication -> new GenerateDescriptorFileTaskName(publication.getName());
	}

	public static String generateDescriptorFileTaskName(Publication publication) {
		return INSTANCE.generateDescriptorFileTaskName().forPublication(publication).toString();
	}

	public interface ToMavenLocalTaskName extends Name {}

	public interface ToRepositoryTaskName extends Name {}

	public interface ToRepositoryBuilder {
		ToMavenLocalTaskName toMavenLocal();

		Spec<String> toAnyRepositories();

		ToRepositoryTaskName to(ArtifactRepository repository);
	}

	public static final class PublishTaskName extends NameSupport<PublishTaskName> implements ToRepositoryBuilder, Name {
		@Override
		public ToMavenLocalTaskName toMavenLocal() {
			return new PublishToMavenLocalTaskName();
		}

		@Override
		public Spec<String> toAnyRepositories() {
			return it -> it.startsWith("publishAllPublicationsTo");
		}

		@Override
		public ToRepositoryTaskName to(ArtifactRepository repository) {
			return new PublishAllPublicationsToRepositoryTaskName(repository.getName());
		}

		@Override
		public String toString() {
			return "publish";
		}
	}

	// TODO: TaskName
	public static final class PublishToMavenLocalTaskName extends NameSupport<PublishToMavenLocalTaskName> implements ToMavenLocalTaskName {
		@Override
		public String toString() {
			return "publishToMavenLocal";
		}
	}

	public PublishTaskName publishTaskName() {
		return new PublishTaskName();
	}

	private static final class PublishAllPublicationsToRepositoryTaskName extends NameSupport<PublishAllPublicationsToRepositoryTaskName> implements ToRepositoryTaskName, QualifiedName {
		private final String repositoryName;

		private PublishAllPublicationsToRepositoryTaskName(String repositoryName) {
			this.repositoryName = repositoryName;
		}

		@Override
		Prop<PublishAllPublicationsToRepositoryTaskName> init() {
			return new Prop.Builder<>(PublishAllPublicationsToRepositoryTaskName.class)
				.with("repositoryName", this::withRepositoryName)
				.build();
		}

		public String getRepositoryName() {
			return repositoryName;
		}

		public PublishAllPublicationsToRepositoryTaskName withRepositoryName(String repositoryName) {
			return new PublishAllPublicationsToRepositoryTaskName(repositoryName);
		}

		@Override
		public String toString() {
			// publishAllPublicationsTo<RepositoryName>Repository
			return "publishAllPublicationsTo" + capitalize(repositoryName) + "Repository";
		}
	}

	private static final class PublishPublicationToMavenLocalTaskName extends NameSupport<PublishPublicationToMavenLocalTaskName> implements ToMavenLocalTaskName, QualifiedName {
		private final String publicationName;

		private PublishPublicationToMavenLocalTaskName(String publicationName) {
			this.publicationName = publicationName;
		}

		@Override
		Prop<PublishPublicationToMavenLocalTaskName> init() {
			return new Prop.Builder<>(PublishPublicationToMavenLocalTaskName.class)
				.with("publicationName", this::withPublicationName)
				.build();
		}

		public String getPublicationName() {
			return publicationName;
		}

		public PublishPublicationToMavenLocalTaskName withPublicationName(String publicationName) {
			return new PublishPublicationToMavenLocalTaskName(publicationName);
		}

		@Override
		public String toString() {
			// publish<PublicationName>PublicationToMavenLocal
			return "publish" + capitalize(publicationName) + "PublicationToMavenLocal";
		}
	}

	private static final class PublishPublicationToRepositoryTaskName extends NameSupport<PublishPublicationToRepositoryTaskName> implements ToRepositoryTaskName, QualifiedName {
		private final String publicationName;
		private final String repositoryName;

		private PublishPublicationToRepositoryTaskName(String publicationName, String repositoryName) {
			this.publicationName = publicationName;
			this.repositoryName = repositoryName;
		}

		@Override
		Prop<PublishPublicationToRepositoryTaskName> init() {
			return new Prop.Builder<>(PublishPublicationToRepositoryTaskName.class)
				.with("publicationName", this::withPublicationName)
				.with("repositoryName", this::withRepositoryName)
				.build();
		}

		public String getPublicationName() {
			return publicationName;
		}

		public PublishPublicationToRepositoryTaskName withPublicationName(String publicationName) {
			return new PublishPublicationToRepositoryTaskName(publicationName, repositoryName);
		}

		public String getRepositoryName() {
			return repositoryName;
		}

		public PublishPublicationToRepositoryTaskName withRepositoryName(String repositoryName) {
			return new PublishPublicationToRepositoryTaskName(publicationName, repositoryName);
		}

		@Override
		public String toString() {
			// publish<PublicationName>PublicationTo<RepositoryName>Repository
			return "publish" + capitalize(publicationName) + "PublicationTo" + capitalize(repositoryName) + "Repository";
		}
	}

	private static final class PublishTaskNameBuilder implements ToRepositoryBuilder {
		private final String publicationName;

		private PublishTaskNameBuilder(String publicationName) {
			this.publicationName = publicationName;
		}

		@Override
		public ToMavenLocalTaskName toMavenLocal() {
			return new PublishPublicationToMavenLocalTaskName(publicationName);
		}

		@Override
		public Spec<String> toAnyRepositories() {
			return it -> it.startsWith("publish" + capitalize(publicationName) + "PublicationTo");
		}

		@Override
		public ToRepositoryTaskName to(ArtifactRepository repository) {
			return new PublishPublicationToRepositoryTaskName(publicationName, repository.getName());
		}
	}

	public ToRepositoryBuilder publishTaskName(Publication publication) {
		return new PublishTaskNameBuilder(publication.getName());
	}

	public static String publishPublicationToRepositoryTaskName(Publication publication, ArtifactRepository repository) {
		return INSTANCE.publishTaskName(publication).to(repository).toString();
	}

	public static Spec<String> publishPublicationToAnyRepositories(Publication publication) {
		return INSTANCE.publishTaskName(publication).toAnyRepositories();
	}
}

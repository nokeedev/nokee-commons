package dev.nokee.commons.names;

enum PublishingTaskNamingScheme implements NamingScheme {
	GenerateMetadataFile {
		@Override
		public String format(Name name) {
			NameBuilder builder = NameBuilder.lowerCamelCase();
			builder.append("generateMetadataFileFor");
			builder.append(name.get("publicationName").toString());
			builder.append("publication");
			return builder.toString();
		}
	},
	GeneratePomFile {
		@Override
		public String format(Name name) {
			NameBuilder builder = NameBuilder.lowerCamelCase();
			builder.append("generatePomFileFor");
			builder.append(name.get("publicationName").toString());
			builder.append("publication");
			return builder.toString();
		}
	},
	GenerateDescriptorFile {
		@Override
		public String format(Name name) {
			NameBuilder builder = NameBuilder.lowerCamelCase();
			builder.append("generateDescriptorFileFor");
			builder.append(name.get("publicationName").toString());
			builder.append("publication");
			return builder.toString();
		}
	},
	PublishPublicationToMavenLocal {
		@Override
		public String format(Name name) {
			NameBuilder builder = NameBuilder.lowerCamelCase();
			builder.append("publish");
			builder.append(name.get("publicationName").toString());
			builder.append("publicationToMavenLocal");
			return builder.toString();
		}
	},
	PublishPublicationToRepository {
		@Override
		public String format(Name name) {
			NameBuilder builder = NameBuilder.lowerCamelCase();
			builder.append("publish");
			builder.append(name.get("publicationName").toString());
			builder.append("publicationTo");
			builder.append(name.get("repositoryName").toString());
			builder.append("repository");
			return builder.toString();
		}
	},
	PublishAllPublicationsToRepository {
		@Override
		public String format(Name name) {
			NameBuilder builder = NameBuilder.lowerCamelCase();
			builder.append("publishAllPublicationsTo");
			builder.append(name.get("repositoryName").toString());
			builder.append("repository");
			return builder.toString();
		}
	}
}

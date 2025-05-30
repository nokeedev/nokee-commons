package dev.nokee.commons.backports;

import org.gradle.api.NonExtensible;
import org.gradle.api.Project;
import org.gradle.api.artifacts.*;
import org.gradle.api.artifacts.dsl.DependencyHandler;
import org.gradle.api.attributes.Attribute;
import org.gradle.api.capabilities.Capability;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.util.GradleVersion;

import javax.inject.Inject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@NonExtensible
public abstract class DependencyCopier {
	private final DependencyHandler dependencies;
	private final ProviderFactory providers;
	private final Project project;

	@Inject
	public DependencyCopier(DependencyHandler dependencies, ProviderFactory providers, Project project) {
		this.dependencies = dependencies;
		this.providers = providers;
		this.project = project;
	}

	@SuppressWarnings("unchecked")
	public <DependencyType extends Dependency> DependencyType copyOf(DependencyType self) {
		DependencyType result = null;
		if (self instanceof ExternalModuleDependency) {
			result = (DependencyType) dependencies.create(Optional.ofNullable(self.getGroup()).orElse("") + ":" + self.getName() + ":" + Optional.ofNullable(self.getVersion()).orElse(""));
			copy((ExternalModuleDependency) self, (ExternalModuleDependency) result);
		} else if (self instanceof ProjectDependency) {
			result = (DependencyType) dependencies.create(project.project(dependencyProjectPathOf(((ProjectDependency) self))));
			copy((ProjectDependency) self, (ProjectDependency) result);
		} else {
			result = (DependencyType) self.copy();
		}

		return result;
	}

	private static String dependencyProjectPathOf(ProjectDependency dependency) {
		if (GradleVersion.current().compareTo(GradleVersion.version("8.11")) >= 0) {
			return dependency.getPath();
		} else {
			@SuppressWarnings("deprecation")
			final Project dependencyProject = dependency.getDependencyProject();
			return dependencyProject.getPath();
		}
	}

	private void copy(ExternalModuleDependency src, ExternalModuleDependency dst) {
		copy((ModuleDependency) src, dst);
		dst.setChanging(src.isChanging());
	}

	private Map<String, String> toExcludeNotation(ExcludeRule rule) {
		Map<String, String> result = new LinkedHashMap<>();
		result.put("group", rule.getGroup());
		result.put("module", rule.getModule());
		return result;
	}

	@SuppressWarnings("rawtypes")
	private void copy(ModuleDependency src, ModuleDependency dst) {
		Optional.ofNullable(src.getReason()).ifPresent(dst::because);
		for (DependencyArtifact artifact : src.getArtifacts()) {
			dst.addArtifact(artifact);
		}
		for (ExcludeRule excludeRule : src.getExcludeRules()) {
			dst.exclude(toExcludeNotation(excludeRule));
		}
		dst.setTransitive(src.isTransitive());
		dst.attributes(attributes -> {
			for (Attribute attribute : src.getAttributes().keySet()) {
				attributes.attribute(attribute, providers.provider(() -> attributes.getAttribute(attribute)));
			}
		});
		dst.capabilities(capabilities -> {
			for (Capability requestedCapability : src.getRequestedCapabilities()) {
				capabilities.requireCapability(requestedCapability);
			}
		});
	}
}

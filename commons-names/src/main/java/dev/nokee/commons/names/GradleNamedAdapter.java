package dev.nokee.commons.names;

import org.gradle.api.Named;

final class GradleNamedAdapter extends NameSupport<GradleNamedAdapter> implements FullyQualifiedName {
	private final Named obj;

	public GradleNamedAdapter(Named obj) {
		this.obj = obj;
	}

	@Override
	public String toString() {
		return obj.getName();
	}

	@Override
	public String toString(NameBuilder builder) {
		return builder.append(obj.getName()).toString();
	}
}

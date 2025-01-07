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

	@Override
	public RelativeName relativeTo(Qualifier qualifier) {
		if (obj.getName().startsWith(qualifier.toString())) {
			String relName = obj.getName().substring(qualifier.toString().length());
			return new DefaultRelativeName(this, new QualifiedStringName(relName));
		}

		String capitalizedQualifier = StringUtils.capitalize(qualifier.toString());
		if (obj.getName().contains(capitalizedQualifier)) {
			String relName = obj.getName().replace(capitalizedQualifier, "");
			return new DefaultRelativeName(this, new QualifiedStringName(relName));
		}

		throw new UnsupportedOperationException("not relative");
	}

	private static final class QualifiedStringName extends NameSupport<QualifiedName> implements QualifiedName {
		private final String relativeName;

		private QualifiedStringName(String relativeName) {
			this.relativeName = relativeName;
		}

		@Override
		public String toString(NameBuilder builder) {
			return builder.append(relativeName).toString();
		}

		@Override
		public String toString() {
			return relativeName;
		}
	}
}

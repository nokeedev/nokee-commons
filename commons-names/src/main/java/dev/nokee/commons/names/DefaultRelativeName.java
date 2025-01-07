package dev.nokee.commons.names;

import java.util.ArrayList;
import java.util.List;

final class DefaultRelativeName extends NameSupport<DefaultRelativeName> implements RelativeName {
	private final FullyQualifiedName fullName;
	private final QualifiedName relativeName;

	public DefaultRelativeName(FullyQualifiedName fullName, QualifiedName relativeName) {
		this.fullName = fullName;
		this.relativeName = relativeName;
	}

	@Override
	public FullyQualifiedName toFullName() {
		return fullName;
	}

	// TODO: Should override equal to ensure fully qualified name doesn't equal a relative name
	//   We should do the same for fully qualified name not equaling a non-qualified name

	@Override
	public String toString(NameBuilder builder) {
		return relativeName.toString(builder);
	}

	@Override
	public String toString() {
		return toString(NameBuilder.toStringCase());
	}

	static Qualifier relativeTo(Qualifier self, Qualifier qualifier) {
		List<Qualifier> rel = new ArrayList<>();
		qualifier.accept(new Qualifier.Visitor() {
			@Override
			public void visit(Qualifier qualifier) {
				rel.add(qualifier);
			}
		});

		List<Qualifier> full = new ArrayList<>();
		self.accept(new Qualifier.Visitor() {
			@Override
			public void visit(Qualifier qualifier) {
				full.add(qualifier);
			}
		});

		while (!rel.isEmpty() && rel.get(0).equals(full.get(0))) {
			rel.remove(0);
			full.remove(0);
		}

		if (!rel.isEmpty()) {
			throw new IllegalArgumentException("This name is not relative to specified qualifier.");
		}

		return builder -> full.forEach(it -> it.appendTo(builder));
	}
}

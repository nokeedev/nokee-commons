package dev.nokee.commons.names;

abstract class ForwardingNames<SELF extends QualifiedName> extends NameSupport<SELF> implements Names {
	@Override
	public String toString() {
		return delegate().toString();
	}

	@Override
	public String toString(NameBuilder builder) {
		return delegate().toString(builder);
	}

	@Override
	public RelativeName relativeTo(Qualifier qualifier) {
		return delegate().relativeTo(qualifier);
	}

	@Override
	public void appendTo(NameBuilder builder) {
		delegate().appendTo(builder);
	}

	@Override
	public void accept(Visitor visitor) {
		delegate().accept(visitor);
	}

	protected abstract Names delegate();
}

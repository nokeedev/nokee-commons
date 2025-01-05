package dev.nokee.commons.names;

final class ObjectNameAdapter extends NameSupport implements Name {
	private final Object obj;

	public ObjectNameAdapter(Object obj) {
		this.obj = obj;
	}

	@Override
	public String toString() {
		return obj.toString();
	}
}

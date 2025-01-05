package dev.nokee.commons.names;

class ObjectNameTests implements NameTester {
	Name subject = Name.of(new TestNameObject());

	@Override
	public Name subject() {
		return subject;
	}

	@Override
	public String name() {
		return "myName";
	}

	static class TestNameObject {
		@Override
		public String toString() {
			return "myName";
		}
	}
}

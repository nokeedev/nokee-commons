package dev.nokee.commons.names;

public class TestQualifier implements Qualifier {
	private final NameString value;

	public TestQualifier(NameString value) {
		this.value = value;
	}

	@Override
	public void appendTo(NameBuilder builder) {
		value.appendTo(builder);
	}
}

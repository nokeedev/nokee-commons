package dev.nokee.commons.names;

final class Preconditions {
	public static void checkArgument(boolean expression) {
		if (!expression) {
			throw new IllegalArgumentException();
		}
	}

	public static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
		if (!expression) {
			throw new IllegalArgumentException(String.format(errorMessageTemplate, errorMessageArgs));
		}
	}
}

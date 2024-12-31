package dev.nokee.commons.names;

final class StringUtils {
	public static String uncapitalize(String s) {
		return Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}

	public static String capitalize(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
}

package dev.nokee.commons.names;

final class StringUtils {
	public static String uncapitalize(String s) {
		if (!s.isEmpty()) {
			return Character.toLowerCase(s.charAt(0)) + s.substring(1);
		}
		return s;
	}

	public static String capitalize(String s) {
		if (!s.isEmpty()) {
			return Character.toUpperCase(s.charAt(0)) + s.substring(1);
		}
		return s;
	}
}

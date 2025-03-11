package dev.nokee.commons.names;

import java.util.HashMap;
import java.util.Map;

class NamingSchemeTester {
	private final NamingScheme scheme;
	private final TestName name;

	public NamingSchemeTester(NamingScheme scheme, Map<String, String> properties) {
		this(scheme, new TestName(properties));
	}

	private NamingSchemeTester(NamingScheme scheme, TestName name) {
		this.scheme = scheme;
		this.name = name;
	}

	public NamingSchemeTester without(String propertyName) {
		return new NamingSchemeTester(scheme, name.without(propertyName));
	}

	public String format() {
		return scheme.format(name);
	}

	private static class TestName implements Name {
		private final Map<String, String> properties;

		private TestName(Map<String, String> properties) {
			this.properties = properties;
		}

		public TestName without(String propertyName) {
			Map<String, String> newProperties = new HashMap<>(properties);
			newProperties.remove(propertyName);
			return new TestName(newProperties);
		}

		@Override
		public Object get(String propertyName) {
			return properties.get(propertyName);
		}
	}
}

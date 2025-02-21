package dev.nokee.commons.gradle.tasks.options;

public interface Lookup<K, V> {
	V get(K key);
}

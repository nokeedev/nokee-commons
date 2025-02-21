package dev.nokee.commons.gradle.provider;

import org.gradle.api.provider.MapProperty;

import java.util.*;

final class JdkMapToMapPropertyAdapter<K, V> extends AbstractMap<K, V> {
	private final MapProperty<K, V> delegate;

	public JdkMapToMapPropertyAdapter(MapProperty<K, V> delegate) {
		this.delegate = delegate;
	}

	@Override
	public V remove(Object key) {
		final Map<K, V> newValues = new LinkedHashMap<>(delegate.get());
		final V result = newValues.remove(key);
		delegate.set(newValues);
		return result;
	}

	@Override
	public void clear() {
		delegate.empty();
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return delegate.get().entrySet();
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return delegate.getting((K) key).getOrElse(defaultValue);
	}

	@Override
	public V get(Object key) {
		return delegate.getting((K) key).getOrNull();
	}

	@Override
	public V put(K key, V value) {
		delegate.put(key, value);
		return null; // can't honor the contract
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		delegate.putAll(m);
	}

	@Override
	public Collection<V> values() {
		return delegate.get().values();
	}

	@Override
	public Set<K> keySet() {
		return delegate.keySet().get();
	}
}

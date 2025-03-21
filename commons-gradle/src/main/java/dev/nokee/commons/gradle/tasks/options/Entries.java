package dev.nokee.commons.gradle.tasks.options;

import dev.nokee.commons.gradle.ActionSet;
import dev.nokee.commons.gradle.ActionUtils;
import org.gradle.api.Action;

import java.io.File;
import java.util.*;

// Responsible to calculate the configuration "key" for a specific file
//   Also responsible to "resolve" the key into a configure action
public class Entries<T> implements IConfigureActionLookup<T>, ISourceKey.Lookup {
	private final List<Entry<T>> entries;

	public Entries(List<Entry<T>> entries) {
		this.entries = entries;
	}

	public Action<T> resolve(ISourceKey key) {
		if (key == ISourceKey.DEFAULT_KEY) {
			return ActionUtils.doNothing();
		}

		List<Action<T>> actions = new ArrayList<>();
		for (int index : ((Key) key).indices) {
			actions.add(entries.get(index));
		}
		return (ActionSet<T>) actions::iterator;
	}

	public ISourceKey forFile(File sourceFile) {
		List<Integer> indices = new ArrayList<>();
		class Details implements Entry.SourceDetails {
			int idx = -1;

			@Override
			public File getSourceFile() {
				return sourceFile;
			}

			@Override
			public void thisEntryParticipateInTheConfiguration() {
				indices.add(idx);
			}
		}
		Details details = new Details();
		for (int i = 0; i < entries.size(); i++) {
			details.idx = i;
			entries.get(i).execute(details);
		}


		if (indices.isEmpty()) {
			return ISourceKey.DEFAULT_KEY;
		} else {
			return new Key(indices.stream().mapToInt(Integer::intValue).toArray());
		}
	}

	public static class Key implements Iterable<Integer>, ISourceKey {
		private final int[] indices;

		private Key(int[] indices) {
			this.indices = indices;
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) return false;
			Key key = (Key) o;
			return Arrays.equals(indices, key.indices);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(indices);
		}

		@Override
		public String toString() {
			return "Key{" + Arrays.toString(indices) + "}";
		}

		@Override
		public Iterator<Integer> iterator() {
			return Arrays.stream(indices).iterator();
		}

		@Override
		public int compareTo(ISourceKey o) {
			if (o == DEFAULT_KEY) {
				return 1;
			}
			assert o instanceof Key;
			Key other = (Key) o;

			// Compare corresponding elements up to the shorter length
			int minLength = Math.min(indices.length, other.indices.length);
			for (int i = 0; i < minLength; i++) {
				if (this.indices[i] < other.indices[i]) {
					return -1;
				} else if (this.indices[i] > other.indices[i]) {
					return 1;
				}
			}
			// If all elements so far are equal, compare lengths
			return this.indices.length - other.indices.length;
		}
	}
}

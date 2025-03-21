package dev.nokee.commons.gradle.tasks.options;

import java.io.File;

public interface ISourceKey extends Comparable<ISourceKey> {
	ISourceKey DEFAULT_KEY = new ISourceKey() {
		@Override
		public int compareTo(ISourceKey o) {
			if (o == this) {
				return 0;
			} else {
				return -1;
			}
		}
	};

	interface Lookup {
		ISourceKey forFile(File sourceFile);
	}
}

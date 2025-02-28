package dev.nokee.commons.gradle;

import org.gradle.api.Named;
import org.gradle.api.Namer;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;

// When developing against latest Gradle but targeting older Gradle,
//   you may encounter inability to cast to Named.
//   The issue boils down to not all Gradle type historically extended from Named (i.e. Task and Configuration).
//   This namer adapter takes care of this problem.
final class EssentiallyNamedNamer<T> implements Namer<T> {
	@SuppressWarnings("unchecked")
	public <S> Namer<S> withNarrowedType() {
		return (Namer<S>) this;
	}

	@Override
	public String determineName(T obj) {
		if (obj instanceof Task) {
			return ((Task) obj).getName(); // on Gradle 8.8+, Task extends Named
		} else if (obj instanceof Configuration) {
			return ((Configuration) obj).getName(); // on Gradle 8.4+, Configuration extends Named
		}
		return ((Named) obj).getName();
	}
}

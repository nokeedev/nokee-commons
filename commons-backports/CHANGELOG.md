# Changelog

## [1.0] - 2025-??-??

We're super excited to introduce the Nokee Commons: Backports project.
This first version is the result of several years of working with plugins supporting multiple Gradle versions.
This release introduce the following backports:

- A registry for the new `ConfigurationContainer` factory methods, named `ConfigurationRegistry` (released with Gradle 8.4)
- A backport of `DependencyFactory` of the same name (released with Gradle 7.6)
- The `ProviderConvertible` interface (released with Gradle 7.3)
- The `IgnoreEmptyDirectories` annotation (released with Gradle 6.8)
- The `UntrackedTask` annotation (released with Gradle 7.3)
- A backport of `DependencyCollector`, previously named `DependencyAdder` until Gradle 8.6, named `DependencyBucket` here (released with Gradle 7.6)

Stay tune for more information about this project.

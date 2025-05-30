# Changelog

## [1.4] - in development

## [1.3] - 2025-05-30

This minor version adds `DependencyCopier` to work around a [fundamental Gradle issue](https://github.com/gradle/gradle/issues/33673).
We also include a backport of Gradle's `DependencyModifier` which rely on composition instead of inheritance.
Finally, we worked on fixing a memory leak inside the `DependencyBucket` decorator.

## [1.2] - 2025-03-26

This minor version adds Nokee's Commons Names contract support to `ConfigurationRegistry` element naming (essentially using `Object#toString` to create a `String` name).
We also include an `SoftwareComponentRegistry` to work around the Gradle 8.1 core change to the `SoftwareComponentContainer` hierarchy change.

## [1.1] - 2025-01-25

This minor version align the implementation of the `DependencyBucket` with its latest counterpart as well as fix some bugs.
All provider taking API now allows no-value providers.
We also work around the immutability of `MinimalExternalModuleDependency` (released with Gradle 6.8).
Finally, we introduced a factory/builder API for creating the buckets, decorating `ExtensionAware` objects, and attaching the bucket to a `Configuration`.

## [1.0] - 2025-01-21

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

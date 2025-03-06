# Changelog

## [1.4] - in development

## [1.3] - 2025-03-06

This minor version introduce `Plugins#whenAnyPluginsApplied` to support a single configuration that must happen after any single plugins are applied from the set.

## [1.2] - 2025-02-28

This minor version perform some bug fixes for the task options support.
It also adds `Plugins#whenAllPluginsApplied` to support waiting for multiple plugin availability.
It adds backward support to the `SpecUtils#named` for `Task` and `Configuration`.
It changes the `SourceFileVisitor` to reuse the same instance, users should create a copy of the `SourceFile` to use outside the visitor, if needed.

## [1.1] - 2025-02-24

This minor version perform some bug fixes for the task options support.

## [1.0] - 2025-02-20

We're super excited to introduce the Nokee Commons: Gradle project.
This first version is the result of several years of working with plugins supporting multiple Gradle versions.
Although this release contains a lot of value to developers, we don't recommend having a direct dependency to the published artifacts.
There are several quality checks that needs to pass before we can change this recommendation (if we're ever going to change it).

### TransformerUtils

- `peek`: Similar to Stream#peek(Consumer), executes an action to any transformed elements
- `toSet`: Force any iterable as order retaining set
- `noOpTransformer`: An identity transformer (non-altering transformer)
- `traverse`/`flatTraverse`: Convenience for transforming each element of an iterable types
- `filter`: Stand as a backport of `Provider#filter(Spec)` and convenience for iterable filtering
- `compose`: Chain transformers together as a single transform

### SpecUtils

- `named`: Stand as a backport of `NamedDomainObjectCollection#named`, use `FilterAwareSpec#whenSatisfed` to use as a collection action
- `compose`: Chain a transformation before evaluating a `Spec`
- `negate`: Reverse the satisfaction of a `Spec`

### ActionUtils

- `doNothing`: Convenience for _null action_
- `transformBefore`: Chain a transformation before executing an `Action`
- `matching`: Stand as a forward port of `DomainObjectCollection#matching` to work avoidance APIs
- `ignored`: Convenience to ignore `Action` parameter

### Factory

Copy of the internal functional `Factory` interface with the ability to _tap_ the instance under creation (i.e. `Factory#tap(Action)`).

### ActionSet

A functional interface representing a set of actions as a single `Action`.

### Plugins

Unified convenience API for dealing with `Project` and `Settings` plugins.
This convenience came in light of [gradle/gradle#32438](https://github.com/gradle/gradle/issues/32438).

### ProviderUtils

- `flatten`: Convenience for unpacking provider of provider, commonly used to deferred provider creation
- `asJdkOptional`: Convenience for dealing with the presence of a provider by converting the current value as JDK `Optional`
- `asOptionalCollectionElement`: Convenience for dealing with optional collection elements
- `alwaysThrows`: Convenience for creating _throwing providers_, for when we require custom no-value exception
- `noValue`: Convenience for creating _no value providers_, gives better code readability
- fluent `finalizeValue`/`disallowChanges`/`finalizeValueOnRead`: Convenience for chaining methods
- `locationOnly`: Convenience for `dirProperty.flatMap { it.destinationDirectory.locationOnly }`
- `elementsOf`: Convenience for `provider.flatMap { it.sources.elements }`
- `toProviderOfCollection`: Sequence a list of providers to a provider of a collection
- `asJdkMap`: Adapt `MapProperty` as JDK `Map` for legacy support

### CallableProviderConvertible

Helper functional interface to automatically convert `ProviderConvertible` type through `Callable` interface supported throughout Gradle codebase.

### ZipProvider

Backport of `Provider#zip` while allowing zipping more than two values at a time.

### Attributes

Gradle attribute APIs are notoriously surprising.
We provide a unified convenience API for dealing with commons attributes use cases.

### RegularFileVisitor

Convenience for `FileTree#visit` only files (ignoring directories).

### SourceFileVisitor

Convenience for `FileTree#visit` only source files defined as a `File` and its relative path.

### TemporaryDirectory

Convenience over `Task#getTemporaryDir` as a `Directory` provider.
It allows things like the following: `temporaryDirectoryOf(task).file('temp-file')`

### SourceTask

A formal contract for source task.

Stay tune for more information about this project.

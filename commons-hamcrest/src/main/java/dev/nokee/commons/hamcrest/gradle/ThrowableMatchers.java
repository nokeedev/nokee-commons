/*
 * Copyright 2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.nokee.commons.hamcrest.gradle;

import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.nullValue;

public final class ThrowableMatchers {
	private static class ExceptionMatcher extends TypeSafeDiagnosingMatcher<Invocation> {
		private final Matcher<? super Throwable> delegate;
		private Invocation lastInvocation;
		private Throwable lastResult;

		private ExceptionMatcher(Matcher<? super Throwable> delegate) {
			this.delegate = delegate;
		}

		@Override
		protected boolean matchesSafely(Invocation invocation, Description description) {
			try {
				if (lastInvocation == invocation) {
					if (lastResult != null) {
						throw lastResult;
					}
				} else {
					lastInvocation = invocation;
					invocation.invoke();
					lastResult = null;
				}
				description.appendText("exception expected, none was thrown");
				return false;
			} catch (Throwable e) {
				lastResult = e;
				if (!delegate.matches(e)) {
					delegate.describeMismatch(e, description);
					return false;
				}
				return true;
			}
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("expecting exception that ").appendDescriptionOf(delegate);
		}
	}

	private static class NoExceptionMatcher extends TypeSafeDiagnosingMatcher<Invocation> {
		private Invocation lastInvocation;
		private Throwable lastResult;

		private NoExceptionMatcher() {
		}

		@Override
		protected boolean matchesSafely(Invocation invocation, Description description) {
			try {
				if (lastInvocation == invocation) {
					if (lastResult != null) {
						throw lastResult;
					}
				} else {
					lastInvocation = invocation;
					invocation.invoke();
					lastResult = null;
				}
				return true;
			} catch (Throwable e) {
				lastResult = e;
				description.appendText("no exception expected, but thrown ").appendText(e.getMessage());
				return false;
			}
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("expecting successful invocation");
		}
	}

	public static Matcher<Invocation> throwsException(Matcher<? super Throwable> matcher) {
		return new ExceptionMatcher(matcher);
	}

	public static Matcher<Invocation> doesNotThrowException() {
		return new NoExceptionMatcher();
	}

	public static Matcher<Throwable> noMessage() {
		return new FeatureMatcher<Throwable, String>(nullValue(), "", "") {
			@Override
			protected String featureValueOf(Throwable actual) {
				return actual.getMessage();
			}
		};
	}

	public static Matcher<Throwable> message(String message) {
		return new FeatureMatcher<Throwable, List<String>>(hasItem(message), "", "") {
			@Override
			protected List<String> featureValueOf(Throwable actual) {
				final List<String> failureMessages = new ArrayList<>();
				do {
					failureMessages.add(actual.getMessage());
				} while ((actual = actual.getCause()) != null);

				return failureMessages;
			}
		};
	}

	public static Matcher<Throwable> message(Matcher<String> message) {
		return new FeatureMatcher<Throwable, List<String>>(hasItem(message), "", "") {
			@Override
			protected List<String> featureValueOf(Throwable actual) {
				final List<String> failureMessages = new ArrayList<>();
				do {
					failureMessages.add(actual.getMessage());
				} while ((actual = actual.getCause()) != null);

				return failureMessages;
			}
		};
	}

	public static Matcher<Throwable> causedBy(Matcher<? super Throwable> matcher) {
		return new FeatureMatcher<Throwable, Throwable>(matcher, "", "") {
			@Override
			protected Throwable featureValueOf(Throwable throwable) {
				return throwable.getCause();
			}
		};
	}

	public interface Invocation {
		void invoke() throws Throwable;
	}
}

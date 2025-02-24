package dev.nokee.commons.hamcrest;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * Decorates another Matcher, retaining the behaviour but allowing tests to be slightly more expressive.
 * <p>
 * For example:  assertThat(project, extension(named("test")))
 *          vs.  assertThat(project, with(extension(named("test"))))
 *
 * @param <T>  the type of object to match
 */
public class With<T> extends BaseMatcher<T> {
	private final Matcher<T> matcher;

	private With(Matcher<T> matcher) {
		this.matcher = matcher;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean matches(Object arg) {
		return matcher.matches(arg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void describeTo(Description description) {
		description.appendText("has ").appendDescriptionOf(matcher);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void describeMismatch(Object item, Description mismatchDescription) {
		matcher.describeMismatch(item, mismatchDescription);
	}

	/**
	 * Decorates another Matcher, retaining its behaviour, but allowing tests to be slightly more expressive.
	 * <p>
	 * For example:
	 * <pre>assertThat(project, with(extension(named("test"))))</pre>
	 * instead of:
	 * <pre>assertThat(project, extension(named("test")))</pre>
	 */
	public static <T> Matcher<T> with(Matcher<T> matcher) {
		return new With<T>(matcher);
	}
}

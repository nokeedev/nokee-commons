package dev.nokee.commons.fixtures;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Subjects {
	String value();
}

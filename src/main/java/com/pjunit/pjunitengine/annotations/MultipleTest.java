package com.pjunit.pjunitengine.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation dedicated for multiple test execution.
 *
 * Fails if number of passed arguments is smaller than number of method params.
 * Currently method doesnt allow to pass binary, hexa and octa numeric types.
 * @throws IllegalArgumentException if argument is empty or not a Number, boolean or String
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MultipleTest {
    String[] values();
}

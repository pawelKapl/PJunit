package com.pjunit.pjunitengine.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotated with @Warmup will be executed before each test
 *
 * In case of multiple methods annotated with @Warmup - only first
 * one will be executed before tests.
 *
 * Could be utilized as test preparation 'given' stage of tests
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Warmup {
}

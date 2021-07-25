package com.pjunit.main.pjunitengine;

import java.util.Objects;

import static java.lang.String.format;

public final class Assertions {

    private Assertions() {}

    public static void assertTrue(boolean expression) {
        if (expression) return;
        throw new AssertionError(
                "\n\nExpression resulted with false, expected true!\n"
        );
    }

    public static void assertFalse(boolean expression) {
        if (!expression) return;
        throw new AssertionError(
                "\n\nExpression resulted with true, expected false!\n"
        );
    }

    public static <T extends Number> void assertNumberEquals(T expected, T actual) {
        Objects.requireNonNull(expected);
        if (expected.equals(actual)) return;

        throw new AssertionError(
                format("%nValues are not equal: %n  Expected: %s Actual: %s", expected, actual)
        );
    }
}
package com.pjunit.pjunitengine;

import java.util.Objects;
import java.util.function.BooleanSupplier;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

public final class Assertions {
    private static final AssertionError TRUE_EXPRESSION_EXPECTED = new AssertionError("\n\nExpression resulted with false, expected true!\n");
    private static final AssertionError FALSE_EXPRESSION_EXPECTED = new AssertionError("\n\nExpression resulted with true, expected false!\n");

    private Assertions() {}

    public static void assertTrue(BooleanSupplier condition) {
        ofNullable(condition)
                .filter(BooleanSupplier::getAsBoolean)
                .orElseThrow(() -> TRUE_EXPRESSION_EXPECTED)
                .getAsBoolean();
    }

    public static void assertFalse(BooleanSupplier condition) {
        ofNullable(condition)
                .filter(c -> !c.getAsBoolean())
                .orElseThrow(() -> FALSE_EXPRESSION_EXPECTED)
                .getAsBoolean();
    }

    public static void assertNotNull(Object object) {
        assertTrue(() -> object != null);
    }

    public static void assertIsNull(Object object) {
        assertTrue(() -> object == null);
    }

    public static <T extends Number> void assertNumberEquals(T expected, T actual) {
        Objects.requireNonNull(expected);
        if (expected.equals(actual)) return;

        throw new AssertionError(
                format("%nValues are not equal: %n  Expected: %s Actual: %s", expected, actual)
        );
    }
}

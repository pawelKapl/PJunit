package com.pjunit.pjunitengine.assertions;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public final class Assertions {
    private static final AssertionError TRUE_EXPRESSION_EXPECTED =
            new AssertionError("\n\nExpression resulted with false, expected true!\n");
    private static final AssertionError FALSE_EXPRESSION_EXPECTED =
            new AssertionError("\n\nExpression resulted with true, expected false!\n");

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
        requireNonNull(expected);
        if (expected.equals(actual)) return;

        throw new AssertionError(
                format("%nValues are not equal: %n  Expected: %s Actual: %s", expected, actual));
    }

    public static void assertObjectEquals(Object expected, Object actual) {
        requireNonNull(expected);
        if (expected.equals(actual)) return;

        throw new AssertionError(
                format("%nObjects are not equal: %n  Expected: %s Actual: %s", expected, actual));
    }

    public static void assertExceptionEquals(Throwable expected, Throwable actual) {
        if (!(expected.getClass().equals(actual.getClass()))) {
            throw new AssertionError(
                    format(
                            "%nExceptions are different,%n expected exception: %s,%n actual exception: %s%n",
                            expected.getClass().getName(), actual.getClass().getName()));
        }
    }

    public static void assertExceptionEquals(
            Class<? extends Throwable> expected, Throwable actual) {
        if (!expected.isInstance(actual)) {
            throw new AssertionError(
                    format(
                            "%nExceptions are different,%n expected exception: %s,%n actual exception: %s%n",
                            expected.getName(), actual.getClass().getName()));
        }
    }

    public static void assertThrows(
            Class<? extends Throwable> expectedException, Runnable runnable) {
        boolean noExc = false;
        try {
            runnable.run();
            noExc = true;
        } catch (Throwable e) {
            assertExceptionEquals(expectedException, e);
        }

        if (noExc)
            throw new AssertionError(
                    format(
                            "%nExpression not resulting with any exception! Expected: %s",
                            expectedException.getName()));
    }

    public static void assertProcessLastNoLongerThan(
            Runnable process, long duration, TimeUnit unit) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<?> runningProcess = executor.submit(process);

        try {
            runningProcess.get(duration, unit);
        } catch (Exception e) {
            throw new AssertionError(
                    format("%nCode execution took longer than: %d%s", duration, unit));
        } finally {
            runningProcess.cancel(true);
            executor.shutdown();
        }
    }
}

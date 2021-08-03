package com.pjunit.pjunitengine;

import com.pjunit.pjunitengine.annotations.ExceptionTest;
import com.pjunit.pjunitengine.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Arrays.stream;

enum TestHandler {
    TEST(Test.class) {
        boolean handleTest(final Method method, final Object testClass) {
            try {
                method.invoke(testClass);
                LOGGER.log(Level.INFO, format("Test %s passed!", getFullMethodName(method)));
                return true;
            } catch (InvocationTargetException e) {
                LOGGER.log(
                        Level.WARNING,
                        format(
                                "Test %s failed, cause: %s",
                                getFullMethodName(method), e.getCause()));
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    },

    EXCEPTION_TEST(ExceptionTest.class) {
        boolean handleTest(final Method method, final Object testClass) {
            Class<? extends Exception>[] expectedExceptions =
                    method.getAnnotation(ExceptionTest.class).value();
            try {
                method.invoke(testClass);
                LOGGER.warning(
                        format(
                                "Test %s failed, no exceptions thrown during execution!",
                                getFullMethodName(method)));
                return false;
            } catch (Exception wrappedExc) {
                for (Class<? extends Exception> e : expectedExceptions) {
                    if (e.isInstance(wrappedExc.getCause())) {
                        LOGGER.info(format("Test %s passed!", getFullMethodName(method)));
                        return true;
                    }
                }
                LOGGER.warning(
                        format(
                                "Test %s failed,%n expected exceptions: %s,%n actual exception: %s%n",
                                getFullMethodName(method),
                                Arrays.toString(expectedExceptions),
                                wrappedExc.getCause()));
                return false;
            }
        }
    };

    private final Class<?> annotation;

    private static final Logger LOGGER = Logger.getLogger(TestHandler.class.getSimpleName());

    TestHandler(final Class<?> annotation) {
        this.annotation = annotation;
    }

    abstract boolean handleTest(Method method, Object testClass);

    static Optional<TestHandler> getHandler(Annotation annotation) {
        return stream(values())
                .filter(handler -> handler.annotation.isInstance(annotation))
                .findAny();
    }

    static String getFullMethodName(Method method) {
        String className = method.getDeclaringClass().getName();
        return className.substring(className.lastIndexOf(".")).replace(".", "")
                + "."
                + method.getName();
    }
}

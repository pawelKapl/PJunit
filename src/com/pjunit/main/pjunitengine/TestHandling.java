package com.pjunit.main.pjunitengine;

import com.pjunit.main.pjunitengine.annotations.ExceptionTest;
import com.pjunit.main.pjunitengine.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Arrays.stream;

enum TestHandling {

    TEST(Test.class) {
        @Override
        boolean handle(final Method method) {
            try {
                method.invoke(null);
                LOGGER.log(Level.INFO, format("Test %s passed!", method.getName()));
                return true;
            } catch (InvocationTargetException e) {
                LOGGER.log(Level.WARNING, format("Test %s failed, cause: %s", method.getName(), e.getCause()));
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    },

    EXCEPTION_TEST(ExceptionTest.class) {
        @Override
        boolean handle(final Method method) {
            Class<? extends Exception>[] possibleExceptions = method.getAnnotation(ExceptionTest.class).value();
            try {
                method.invoke(null);
                LOGGER.warning(format("Test %s failed, no exceptions thrown during execution!", method.getName()));
                return false;
            } catch (Exception wrappedExc) {
                for (Class<? extends Exception> e : possibleExceptions) {
                    if (e.isInstance(wrappedExc.getCause())) {
                        LOGGER.info(format("Test %s passed!", method.getName()));
                        return true;
                    }
                }
                LOGGER.warning(format("Test %s failed,%n expected exceptions: %s,%n actual exception: %s%n",
                        method.getName(),
                        Arrays.toString(possibleExceptions),
                        wrappedExc.getCause()
                ));
                return false;
            }
        }
    };

    private final Class<?> annotation;
    private static final Logger LOGGER = Logger.getLogger(TestHandling.class.getSimpleName());

    TestHandling(final Class<?> annotation) {
        this.annotation = annotation;
    }

    abstract boolean handle(Method method);

    static Optional<TestHandling> parseClazz(Annotation annotation) {
        return stream(values())
                .filter(handler -> handler.annotation.isInstance(annotation))
                .findFirst();
    }
}
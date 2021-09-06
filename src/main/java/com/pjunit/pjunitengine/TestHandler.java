package com.pjunit.pjunitengine;

import static com.pjunit.pjunitengine.ArgsParser.parseParam;
import static java.lang.String.format;
import static java.util.Arrays.stream;

import com.pjunit.pjunitengine.annotations.CustomName;
import com.pjunit.pjunitengine.annotations.ExceptionTest;
import com.pjunit.pjunitengine.annotations.MultipleTest;
import com.pjunit.pjunitengine.annotations.Test;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

enum TestHandler {
    TEST(Test.class) {
        boolean handleTest(final Method method, final String[] args, final Object testClass) {
            try {
                printCustomMessageIfPresent(method);
                method.invoke(testClass);
                LOGGER.log(Level.INFO, "Test {0} passed!", getFullMethodName(method));
                return true;
            } catch (InvocationTargetException e) {
                e.printStackTrace();
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
        boolean handleTest(final Method method, final String[] args, final Object testClass) {
            Class<? extends Exception>[] expectedExceptions =
                    method.getAnnotation(ExceptionTest.class).value();
            try {
                printCustomMessageIfPresent(method);
                method.invoke(testClass);
                LOGGER.log(
                        Level.WARNING,
                        "Test {0} failed, no exceptions thrown during execution!",
                        getFullMethodName(method));
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
    },

    MULTIPLE_TEST(MultipleTest.class) {
        boolean handleTest(final Method method, final String[] args, final Object testClass) {
            Parameter[] parameters = method.getParameters();
            if (parameters.length > args.length) {
                LOGGER.log(
                        Level.WARNING,
                        format(
                                "Test %s failed! Args mismatch:%n "
                                        + "\tNumber of defined test args: %d%n"
                                        + "\tNumber of method parameters: %d",
                                method.getName(), args.length, parameters.length));
                return false;
            }
            logCustomTestDescriptionIfPresent(method, args);
            try {
                method.invoke(testClass, getMethodArgs(parameters, args));
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.log(
                        Level.WARNING,
                        format(
                                "Test %s failed for args: %s %ncause: %s",
                                getFullMethodName(method),
                                Arrays.toString(args),
                                e.getCause() != null
                                        ? e.getCause()
                                        : e.getClass().getName() + ": " + e.getMessage()));
                return false;
            }
            LOGGER.log(
                    Level.INFO,
                    format(
                            "Test %s for args: %s passed!",
                            getFullMethodName(method), Arrays.toString(args)));
            return true;
        }

        private void logCustomTestDescriptionIfPresent(Method method, Object[] args) {
            String description = method.getAnnotation(MultipleTest.class).description();
            if (!description.isEmpty()) LOGGER.log(Level.INFO, format(description, args));
        }

        private Object[] getMethodArgs(Parameter[] parameters, String[] args) {
            var methodArgs = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                methodArgs[i] = parseParam(parameters[i].getType(), args[i]);
            }
            return methodArgs;
        }
    };

    private final Class<?> annotation;

    private static final Logger LOGGER = Logger.getLogger(TestHandler.class.getSimpleName());

    TestHandler(final Class<?> annotation) {
        this.annotation = annotation;
    }

    abstract boolean handleTest(Method method, String[] args, Object testClass);

    static Optional<TestHandler> getHandler(Annotation annotation) {
        return stream(values())
                .filter(handler -> handler.annotation.isInstance(annotation))
                .findAny();
    }

    static String getFullMethodName(Method method) {
        String className = method.getDeclaringClass().getName();
        className = className.substring(className.lastIndexOf(".")).replace(".", "");
        return format("%s.%s", className, method.getName());
    }

    static void printCustomMessageIfPresent(Method testMethod) {
        CustomName customName = testMethod.getDeclaredAnnotation(CustomName.class);
        if (customName != null) {
            LOGGER.log(Level.INFO, customName.value());
        }
    }
}

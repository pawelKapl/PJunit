package com.pjunit.pjunitengine;

import static java.util.Arrays.stream;

import com.pjunit.pjunitengine.annotations.MultipleTest;
import com.pjunit.pjunitengine.annotations.Warmup;
import com.pjunit.pjunitengine.exceptions.ClassPreparationException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

final class TestExecutor {

    private final TestResults testResults;

    private static final Logger LOGGER = Logger.getLogger(TestExecutor.class.getSimpleName());

    private TestExecutor() {
        testResults = new TestResults();
    }

    static TestExecutor getExecutor() {
        return new TestExecutor();
    }

    void executeAllTests(Set<Class<?>> testClasses) {
        testClasses.forEach(
                testClazz -> {
                    for (Method method : testClazz.getDeclaredMethods()) {
                        if (method.getDeclaredAnnotation(MultipleTest.class) != null) {
                            stream(method.getDeclaredAnnotation(MultipleTest.class).values())
                                    .forEach(
                                            args ->
                                                    runTest(
                                                            TestHandler.MULTIPLE_TEST,
                                                            testClazz,
                                                            method,
                                                            getStringArgs(args)));
                        } else {
                            stream(method.getDeclaredAnnotations())
                                    .map(TestHandler::getHandler)
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .forEach(
                                            handler ->
                                                    runTest(
                                                            handler,
                                                            testClazz,
                                                            method,
                                                            new String[0]));
                        }
                    }
                });
        LOGGER.log(Level.INFO, "{0}", testResults);
    }

    private void runTest(TestHandler handler, Class<?> testClazz, Method method, String[] args) {
        if (handler.handleTest(method, args, prepareTestClass(testClazz)))
            testResults.markSuccess();
        else testResults.markFail();
    }

    private static final class TestResults {
        private int totalRate = 0;
        private int successRate = 0;
        private int failedRate = 0;

        private void markSuccess() {
            successRate++;
            totalRate++;
        }

        private void markFail() {
            failedRate++;
            totalRate++;
        }

        @Override
        public String toString() {
            return "\nTEST SUMMARY:\n  "
                    + "Total tests performed: "
                    + totalRate
                    + "\n\tTests passed: "
                    + successRate
                    + "\n\tTests failed: "
                    + failedRate;
        }
    }

    private Object prepareTestClass(Class<?> testClazz) {
        Optional<Method> warmup = getWarmupMethodIfDeclared(testClazz);
        try {
            Object testClassInstance = testClazz.getDeclaredConstructor().newInstance();
            if (warmup.isPresent()) warmup.get().invoke(testClassInstance);
            return testClassInstance;
        } catch (Exception e) {
            throw new ClassPreparationException(
                    "Error during test class instantiation and/or warmup", e);
        }
    }

    private Optional<Method> getWarmupMethodIfDeclared(Class<?> testClazz) {
        for (Method method : testClazz.getDeclaredMethods()) {
            Optional<Annotation> warmupOptional =
                    stream(method.getDeclaredAnnotations())
                            .filter(Warmup.class::isInstance)
                            .findAny();
            if (warmupOptional.isPresent()) return Optional.of(method);
        }
        return Optional.empty();
    }

    private String[] getStringArgs(String value) {
        String[] args = value.split(",");
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
        }
        return args;
    }
}

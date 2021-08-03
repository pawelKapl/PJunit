package com.pjunit.pjunitengine;

import com.pjunit.pjunitengine.annotations.Warmup;
import com.pjunit.pjunitengine.exceptions.ClassPreparationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
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
                        Arrays.stream(method.getDeclaredAnnotations())
                                .map(TestHandler::getHandler)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .forEach(handler -> runTest(handler, testClazz, method));
                    }
                });
        LOGGER.log(Level.INFO, "{0}", testResults);
    }

    private void runTest(TestHandler handler, Class<?> testClazz, Method method) {
        if (handler.handleTest(method, prepareTestClass(testClazz))) testResults.markSuccess();
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
                    Arrays.stream(method.getDeclaredAnnotations())
                            .filter(Warmup.class::isInstance)
                            .findAny();
            if (warmupOptional.isPresent()) return Optional.of(method);
        }
        return Optional.empty();
    }
}
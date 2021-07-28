package com.pjunit.pjunitengine;

import com.pjunit.pjunitengine.annotations.Warmup;
import com.pjunit.pjunitengine.exceptions.ClassPreparationException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import static java.lang.String.format;

final class TestExecutor {
    private static final Logger LOGGER = Logger.getLogger(TestExecutor.class.getSimpleName());

    private int passed = 0;
    private int failed = 0;

    private TestExecutor() {
    }

    static TestExecutor getExecutor() {
        return new TestExecutor();
    }

    void executeAllTests(Set<Class<?>> testClasses) {
        passed = 0;
        failed = 0;

        testClasses.forEach(testClazz -> {
            for (Method method : testClazz.getDeclaredMethods()) {
                Arrays.stream(method.getDeclaredAnnotations())
                        .map(TestHandling::parseClazz)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(testHandler -> {
                            if (testHandler.handle(method, prepareTestClass(testClazz))) passed++;
                            else failed++;
                        });
            }
        });
        LOGGER.info(format("SUMMARY - Test passed: %d, Tests failed: %d", passed, failed));
    }

    private Object prepareTestClass(Class<?> testClazz) {
        Optional<Method> warmup = getWarmupMethodIfDeclared(testClazz);
        try {
            Object testClassInstance = testClazz.getDeclaredConstructor().newInstance();
            if (warmup.isPresent())
                warmup.get().invoke(testClassInstance);
            return testClassInstance;
        } catch (Exception e) {
            throw new ClassPreparationException("Error during test class instantiation and/or warmup", e);
        }
    }

    private Optional<Method> getWarmupMethodIfDeclared(Class<?> testClazz) {
        for (Method method : testClazz.getDeclaredMethods()) {
            Optional<Annotation> warmupOptional = Arrays.stream(method.getDeclaredAnnotations())
                    .filter(Warmup.class::isInstance)
                    .findAny();
            if (warmupOptional.isPresent()) return Optional.of(method);
        }
        return Optional.empty();
    }
}

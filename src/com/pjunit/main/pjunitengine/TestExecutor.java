package com.pjunit.main.pjunitengine;

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

    private TestExecutor() {}

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
                        .forEach(test -> {
                            if (test.get().handle(method)) passed++;
                            else failed++;
                        });
            }
        });
        LOGGER.info(format("SUMMARY - Test passed: %d, Tests failed: %d", passed, failed));
    }
}
package com.pjunit.main.pjunitengine;

import com.pjunit.main.pjunitengine.annotations.PJunitTest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

class TestEngine {

    private static final Logger LOGGER = Logger.getLogger(TestEngine.class.getSimpleName());

    private TestEngine() {}

    public static void main(String[] args) {
        String packageName = args.length == 0 ? "com.pjunit.test" : args[0];
        TestExecutor
                .getExecutor()
                .executeAllTests(findAllClassesUsingClassLoader(packageName));
    }

    private static Set<Class<?>> findAllClassesUsingClassLoader(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(packageName.replaceAll("[.]", "/"));
        var reader = new BufferedReader(new InputStreamReader(requireNonNull(stream)));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .filter(Objects::nonNull)
                .filter(clazz -> clazz.isAnnotationPresent(PJunitTest.class))
                .collect(Collectors.toSet());
    }

    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
                    + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            LOGGER.warning(format("Couldn't create class: %s.%s", packageName, className));
        }
        return null;
    }
}
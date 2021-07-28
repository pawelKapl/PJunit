package com.pjunit.pjunitengine;

import com.pjunit.pjunitengine.annotations.PJunitTest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

final class ClassFinder {

    private ClassFinder() {}

    private static final Logger LOGGER = Logger.getLogger(ClassFinder.class.getSimpleName());

    static Set<Class<?>> findAllTestClasses(String packageName) {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(normalizePackageName(packageName));
        var reader = new BufferedReader(new InputStreamReader(requireNonNull(stream)));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .filter(Objects::nonNull)
                .filter(clazz -> clazz.isAnnotationPresent(PJunitTest.class))
                .collect(Collectors.toSet());
    }

    private static String normalizePackageName(final String packageName) {
        return packageName.replaceAll("[.]", "/");
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
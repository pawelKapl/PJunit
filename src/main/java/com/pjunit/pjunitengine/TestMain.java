package com.pjunit.pjunitengine;

import com.pjunit.pjunitengine.annotations.PJunitTest;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.net.URLClassLoader.newInstance;
import static java.nio.file.Paths.get;
import static org.reflections.util.ClasspathHelper.forPackage;
import static org.reflections.util.ClasspathHelper.staticClassLoader;

class TestMain {

    private static final Logger LOGGER = Logger.getLogger(TestMain.class.getSimpleName());

    private TestMain() {
    }

    public static void main(String[] args) throws MalformedURLException {
        Set<Class<?>> testClasses = getAllTestClasses();
        LOGGER.info(format("Loaded %s test classes in total, starting tests execution!", testClasses.size()));
        TestExecutor.getExecutor()
                .executeAllTests(testClasses);
    }

    private static Set<Class<?>> getAllTestClasses() throws MalformedURLException {
        URL testClassUrl = get("target/test-classes").toUri().toURL();
        URLClassLoader classLoader = newInstance(new URL[]{testClassUrl}, staticClassLoader());

        Reflections classFinder = new Reflections(
                new ConfigurationBuilder()
                        .addUrls(forPackage("", classLoader))
                        .addClassLoader(classLoader)
                        .setScanners(new TypeAnnotationsScanner(), new SubTypesScanner())
        );
        return classFinder.getTypesAnnotatedWith(PJunitTest.class).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
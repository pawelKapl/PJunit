package com.pjunit.pjunitengine;

import static java.net.URLClassLoader.newInstance;
import static java.nio.file.Paths.get;
import static org.reflections.util.ClasspathHelper.forPackage;
import static org.reflections.util.ClasspathHelper.staticClassLoader;

import com.pjunit.pjunitengine.annotations.PJunitTest;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;

class TestMain {

    private static final Logger LOGGER = Logger.getLogger(TestMain.class.getSimpleName());

    private TestMain() {}

    public static void main(String[] args) throws MalformedURLException {
        Set<Class<?>> testClasses = getAllTestClasses();
        LOGGER.log(
                Level.INFO,
                "Loaded {0} test classes in total, starting tests execution!",
                testClasses.size());
        TestExecutor.getExecutor().executeAllTests(testClasses);
    }

    private static Set<Class<?>> getAllTestClasses() throws MalformedURLException {
        URL testClassUrl = get("target/test-classes").toUri().toURL();
        URLClassLoader classLoader = newInstance(new URL[] {testClassUrl}, staticClassLoader());

        Reflections classFinder =
                new Reflections(
                        new ConfigurationBuilder()
                                .addUrls(forPackage("", classLoader))
                                .addClassLoader(classLoader)
                                .setScanners(new TypeAnnotationsScanner(), new SubTypesScanner()));
        return new HashSet<>(classFinder.getTypesAnnotatedWith(PJunitTest.class));
    }
}
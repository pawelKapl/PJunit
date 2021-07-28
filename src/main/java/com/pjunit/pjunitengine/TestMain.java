package com.pjunit.pjunitengine;

import java.util.logging.Logger;

import static com.pjunit.pjunitengine.ClassFinder.findAllTestClasses;
import static java.lang.String.format;

class TestMain {

    private static final Logger LOGGER = Logger.getLogger(TestMain.class.getSimpleName());

    private TestMain() {}

    public static void main(String[] args) {
        String packageName = args.length == 0 ? "com.pjunit.pjunitengine.test" : args[0];
        var testClasses = findAllTestClasses(packageName);
        LOGGER.info(format("Loaded %s test classes in total, starting tests execution!", testClasses.size()));
        TestExecutor.getExecutor()
                .executeAllTests(testClasses);
    }
}
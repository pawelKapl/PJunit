package com.pjunit.pjunitengine;

import com.pjunit.pjunitengine.annotations.ExceptionTest;
import com.pjunit.pjunitengine.annotations.MultipleTest;
import com.pjunit.pjunitengine.annotations.PJunitTest;
import com.pjunit.pjunitengine.annotations.Test;
import com.pjunit.pjunitengine.annotations.Warmup;
import com.pjunit.pjunitengine.assertions.Assertions;
import com.pjunit.pjunitengine.assertions.Helpers;

@PJunitTest
public class ExampleTestClass {

    private Integer[] intTestArray;
    private String test;

    @Warmup
    public void setUp() {
        intTestArray = new Integer[2];
        test = "String";
    }

    @Test
    public void testIfMathPowReturnsRightValue() {
        // given
        double i = 6;

        // when
        double pow = Math.pow(i, 2.0);

        // then
        Assertions.assertNumberEquals(36.0, pow);
    }

    @Test
    public void testIfTwoIntegerObjectsNotTheSame() {
        Integer a = 128;
        Integer b = 128;
        Assertions.assertFalse(() -> a == b);
    }

    @Test
    public void testIfTwoCachedIntegerObjectsAreTheSame() {
        Integer a = 127;
        Integer b = 127;
        Assertions.assertTrue(() -> a == b);
    }

    @Test
    public void testIfObjectIsNotNull() {
        Assertions.assertNotNull(test);
    }

    @Test
    public void testIfObjectIsNull() {
        Assertions.assertIsNull(intTestArray[1]);
    }

    @Test
    public void testIfStringsAreEqual() {
        // given
        String s1 = "Test";
        String s2 = "Te" + "st";

        Assertions.assertObjectEquals(s1, s2);
    }

    @ExceptionTest({NumberFormatException.class})
    public void testIfParsingThrowsNumberFormatException() {
        // given
        var integer = "12ef3";

        // when
        Integer.parseInt(integer);
    }

    @Test
    public void testIfProperExceptionThrown() {
        // given
        var integer = "12ef3";

        // when
        var throwable = Helpers.captureException(() -> Integer.parseInt(integer));

        // then
        Assertions.assertTrue(() -> throwable instanceof NumberFormatException);
    }

    @Test
    public void testIfProperExceptionThrownCCE() {
        // given
        Object i = 12;
        Integer ii = null;

        Assertions.assertThrows(() -> ((Double) i).floatValue(), ClassCastException.class);
    }

    @MultipleTest(
            description = "For given number %s and string %s, should return %s",
            values = {
                "1.001, test, true",
                "1.6, test, true",
                "0.99, test, false",
                "-999, test, false",
                "45, test, true"
            })
    public void multipleTest(float a, String b, boolean c) {
        Assertions.assertNotNull(b);
        Assertions.assertTrue(() -> a > 1 == c);
    }
}

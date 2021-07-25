package com.pjunit.test;

import com.pjunit.main.pjunitengine.annotations.ExceptionTest;
import com.pjunit.main.pjunitengine.annotations.PJunitTest;
import com.pjunit.main.pjunitengine.annotations.Test;

import static com.pjunit.main.pjunitengine.Assertions.*;

@PJunitTest
public class ExampleTestClass {

    @Test
    public static void testIfMathPowReturnsRightValue() {
        //given
        double i = 6;

        //when
        double pow = Math.pow(i, 2.0);

        //then
        assertNumberEquals(36.0, pow);
    }

    @Test
    public static void testIfTwoIntegerObjectsNotTheSame() {
        Integer a = 128;
        Integer b = 128;
        assertFalse(a == b);
    }

    @Test
    public static void testIfTwoCachedIntegerObjectsAreTheSame() {
        Integer a = 127;
        Integer b = 127;
        assertTrue(a == b);
    }

    @ExceptionTest({NumberFormatException.class})
    public static void testIfParsingThrowsNumberFormatException() {
        //given
        var integer = "12ef3";

        //when
        Integer.parseInt(integer);
    }
}

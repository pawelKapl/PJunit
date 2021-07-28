package com.pjunit.pjunitengine.test;

import com.pjunit.pjunitengine.assertions.Assertions;
import com.pjunit.pjunitengine.annotations.ExceptionTest;
import com.pjunit.pjunitengine.annotations.PJunitTest;
import com.pjunit.pjunitengine.annotations.Test;
import com.pjunit.pjunitengine.annotations.Warmup;

@PJunitTest
public class ExampleTestClass {

    private Integer[] intTestArray;
    private String test;

    @Warmup
    public void SetUp() {
        intTestArray = new Integer[2];
        test = "String";
    }

    @Test
    public void testIfMathPowReturnsRightValue() {
        //given
        double i = 6;

        //when
        double pow = Math.pow(i, 2.0);

        //then
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

    @ExceptionTest({NumberFormatException.class})
    public void testIfParsingThrowsNumberFormatException() {
        //given
        var integer = "12ef3";

        //when
        Integer.parseInt(integer);
    }
}

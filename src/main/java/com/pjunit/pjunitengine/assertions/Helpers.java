package com.pjunit.pjunitengine.assertions;

public final class Helpers {

    private Helpers() {}

    public static Throwable captureException(Runnable exception) {
        try {
            exception.run();
            return null;
        } catch (Throwable e) {
            return e;
        }
    }
}

package uk.co.jamesridgway.gradle.gitflow.plugin.utils;

import java.util.concurrent.Callable;

public class Exceptions {

    private Exceptions() {
    }

    public static <T> T propagateAnyError(final Callable<T> callable) {
        try {
            return callable.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void propagateAnyError(final Action action) {
        try {
            action.perform();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FunctionalInterface
    public interface Action {
        void perform() throws Exception;
    }
}

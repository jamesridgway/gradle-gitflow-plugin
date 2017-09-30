package uk.co.jamesridgway.gradle.gitflow.plugin.utils;

import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ExceptionsTest {

    @Test
    public void propagateAnyError() {
        assertThatThrownBy(() -> Exceptions.propagateAnyError(() -> {
            throw new IOException("This is a checked exception");
        })).isInstanceOf(RuntimeException.class)
                .hasCauseInstanceOf(IOException.class)
                .hasMessage("java.io.IOException: This is a checked exception");
    }

    @Test
    public void propagateAnyErrorReturnsValue() {
        assertThat(Exceptions.propagateAnyError(() -> 123)).isEqualTo(123);
    }

}

package uk.co.jamesridgway.gradle.gitflow.plugin.version;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UnknownVersionTest {

    @Test
    public void isUnknownVersion() {
        Version unknownVersion = new UnknownVersion();
        assertThat(unknownVersion.getMajor()).isEqualTo(0);
        assertThat(unknownVersion.getMinor()).isEqualTo(0);
        assertThat(unknownVersion.getPatch()).isEqualTo("0");
        assertThat(unknownVersion.toString()).isEqualTo("0.0.0");
    }

    @Test
    public void isNotRelease() {
        Version unknownVersion = new UnknownVersion();
        assertThat(unknownVersion.isRelease()).isFalse();
    }

}

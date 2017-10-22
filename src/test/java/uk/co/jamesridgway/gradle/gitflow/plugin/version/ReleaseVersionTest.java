package uk.co.jamesridgway.gradle.gitflow.plugin.version;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Collections.emptySet;
import static org.assertj.core.api.Assertions.assertThat;

public class ReleaseVersionTest {

    @Test
    public void parse() {
        assertThat(ReleaseVersion.parse("")).isEmpty();
        assertThat(ReleaseVersion.parse("1")).isEmpty();
        assertThat(ReleaseVersion.parse("1.0")).isEmpty();
        assertThat(ReleaseVersion.parse("1.0.")).isEmpty();
        assertThat(ReleaseVersion.parse("2.3.4-hotfix1")).isEmpty();

        ReleaseVersion releaseVersion = ReleaseVersion.parse("1.2.3").get();
        assertThat(releaseVersion.getMajor()).isEqualTo(1);
        assertThat(releaseVersion.getMinor()).isEqualTo(2);
        assertThat(releaseVersion.getPatch()).isEqualTo("3");
        assertThat(releaseVersion.toString()).isEqualTo("1.2.3");

    }

    @Test
    public void findLatest() {
        assertThat(ReleaseVersion.findLatest(emptySet())).isEmpty();

        Set<ReleaseVersion> versions = new HashSet<>();
        versions.add(new ReleaseVersion(0, 0, 0));
        versions.add(new ReleaseVersion(1, 0, 0));
        versions.add(new ReleaseVersion(0, 1, 0));
        versions.add(new ReleaseVersion(0, 10, 0));

        assertThat(ReleaseVersion.findLatest(versions))
                .contains(new ReleaseVersion(1, 0, 0));
    }

    @Test
    public void orderComparison() {
        Set<ReleaseVersion> versions = new TreeSet<>();
        versions.add(new ReleaseVersion(0, 0, 1));
        versions.add(new ReleaseVersion(0, 0, 0));
        versions.add(new ReleaseVersion(1, 0, 0));
        versions.add(new ReleaseVersion(0, 1, 0));
        versions.add(new ReleaseVersion(0, 10, 0));
        versions.add(new ReleaseVersion(0, 2, 0));
        versions.add(new ReleaseVersion(2, 0, 0));

        assertThat(versions).containsExactly(
                new ReleaseVersion(0, 0, 0),
                new ReleaseVersion(0, 0, 1),
                new ReleaseVersion(0, 1, 0),
                new ReleaseVersion(0, 2, 0),
                new ReleaseVersion(0, 10, 0),
                new ReleaseVersion(1, 0, 0),
                new ReleaseVersion(2, 0, 0));
    }

}

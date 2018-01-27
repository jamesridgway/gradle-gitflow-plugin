package uk.co.jamesridgway.gradle.gitflow.plugin.version;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UnreleasedVersionTest {

    @Test
    public void builderWithAllVariables() {
        final String template = "${major}.${minor}.${patch}-${branch}.${commitsSinceLastTag}"
                + "+sha.${commitId?substring(0,7)}${dirty?then('.dirty','')}";

        ReleaseVersion releaseVersion = new ReleaseVersion(2, 14, 3);

        UnreleasedVersion unreleasedVersion = UnreleasedVersion.build(releaseVersion, template)
                .withBranch("feature/test")
                .withCommitId("a3dcb3e")
                .withCommitsSinceLastTag(6)
                .withDirty(true)
                .create();

        assertThat(unreleasedVersion.getMajor()).isEqualTo(2);
        assertThat(unreleasedVersion.getMinor()).isEqualTo(14);
        assertThat(unreleasedVersion.getPatch()).isEqualTo("3");
        assertThat(unreleasedVersion.toString()).isEqualTo("2.14.3-feature/test.6+sha.a3dcb3e.dirty");
    }

    @Test
    public void isNotRelease() {
        ReleaseVersion releaseVersion = new ReleaseVersion(2, 14, 3);
        UnreleasedVersion unreleasedVersion = UnreleasedVersion.build(releaseVersion, "")
                .withBranch("feature/test")
                .withCommitId("a3dcb3e")
                .withCommitsSinceLastTag(6)
                .withDirty(true)
                .create();
        assertThat(unreleasedVersion.isRelease()).isFalse();
    }

}

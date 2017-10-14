package uk.co.jamesridgway.gradle.gitflow.plugin.version;

import com.google.common.base.MoreObjects;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReleaseVersion implements Version, Comparable<ReleaseVersion> {

    private static final Pattern RELEASE_FORMAT = Pattern.compile("^(\\d+).(\\d+).(\\d+)$");

    private final int major;
    private final int minor;
    private final int patch;

    static Optional<ReleaseVersion> findLatest(final Set<ReleaseVersion> versions) {
        if (versions.isEmpty()) {
            return Optional.empty();
        }
        TreeSet<ReleaseVersion> sortedVersions = new TreeSet<>(versions);
        return Optional.ofNullable(sortedVersions.last());
    }

    static Optional<ReleaseVersion> parse(final String versionString) {
        final Matcher matcher = RELEASE_FORMAT.matcher(versionString);
        if (!matcher.matches()) {
            return Optional.empty();
        }
        final int major = Integer.parseInt(matcher.group(1));
        final int minor = Integer.parseInt(matcher.group(2));
        final int patch = Integer.parseInt(matcher.group(3));
        return Optional.of(new ReleaseVersion(major, minor, patch));
    }

    ReleaseVersion(final int major, final int minor, final int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    @Override
    public int getMajor() {
        return major;
    }

    @Override
    public int getMinor() {
        return minor;
    }

    @Override
    public String getPatch() {
        return Integer.toString(patch);
    }

    @Override
    public int compareTo(final ReleaseVersion other) {
        if (other == null) {
            return 1;
        }
        int[] parts = new int[]{major, minor, patch};
        int[] otherParts = new int[]{other.major, other.minor, other.patch};
        for (int i = 0; i < parts.length; i++) {
            if (parts[i] < otherParts[i]) {
                return -1;
            }
            if (parts[i] > otherParts[i]) {
                return 1;
            }
        }

        return 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ReleaseVersion that = (ReleaseVersion) o;
        return Objects.equals(major, that.major)
                && Objects.equals(minor, that.minor)
                && Objects.equals(patch, that.patch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, patch);
    }
}

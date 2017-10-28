package uk.co.jamesridgway.gradle.gitflow.plugin.version;

import java.util.Objects;

public class UnknownVersion implements Version {

    @Override
    public int getMajor() {
        return 0;
    }

    @Override
    public int getMinor() {
        return 0;
    }

    @Override
    public String getPatch() {
        return "0";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final UnknownVersion that = (UnknownVersion) o;
        return Objects.equals(getMajor(), that.getMajor())
                && Objects.equals(getMinor(), that.getMinor())
                && Objects.equals(getPatch(), that.getPatch());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMajor(), getMinor(), getPatch());
    }

    @Override
    public String toString() {
        return "0.0.0";
    }

}

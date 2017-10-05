package uk.co.jamesridgway.gradle.gitflow.plugin.version;

import com.google.common.base.MoreObjects;

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
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("major", getMajor())
                .add("minor", getMinor())
                .add("patch", getPatch())
                .toString();
    }
}

package uk.co.jamesridgway.gradle.gitflow.plugin.version;

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
        return "0.0.0";
    }
}

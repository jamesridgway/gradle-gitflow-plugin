package uk.co.jamesridgway.gradle.gitflow.plugin.version;

public interface Version {

    int getMajor();

    int getMinor();

    String getPatch();

    default String getVersionString() {
        return String.format("%s.%s.%s", getMajor(), getMinor(), getPatch());
    }
}

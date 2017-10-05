package uk.co.jamesridgway.gradle.gitflow.plugin.version;

import org.gradle.api.Project;

public interface VersionProvider {

    Version getVersion(Project project);
}

package uk.co.jamesridgway.gradle.gitflow.plugin;

import org.gradle.api.Project;
import org.gradle.api.provider.PropertyState;

public class GitFlowPluginExtension {

    private final PropertyState<String> unreleasedVersionTemplate;

    public GitFlowPluginExtension(final Project project) {
        unreleasedVersionTemplate = project.property(String.class);
        unreleasedVersionTemplate.set("${major}.${minor}.${patch}-${branch}.${commitsSinceLastTag}+sha."
                + "${commitId?substring(0,7)}${dirty?then('.dirty','')}");
    }

    public String getUnreleaseVersionTemplate() {
        return unreleasedVersionTemplate.get();
    }



}

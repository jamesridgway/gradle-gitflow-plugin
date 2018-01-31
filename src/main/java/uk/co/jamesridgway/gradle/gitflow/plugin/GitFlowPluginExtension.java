package uk.co.jamesridgway.gradle.gitflow.plugin;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;

public class GitFlowPluginExtension {

    private final Property<String> unreleasedVersionTemplate;

    public GitFlowPluginExtension(final Project project) {
        unreleasedVersionTemplate = project.getObjects().property(String.class);
        unreleasedVersionTemplate.set("${major}.${minor}.${patch}-${branch?replace('/', '_')}."
                + "${commitsSinceLastTag}+sha.${commitId?substring(0,7)}${dirty?then('.dirty','')}");
    }

    public String getUnreleaseVersionTemplate() {
        return unreleasedVersionTemplate.get();
    }


}

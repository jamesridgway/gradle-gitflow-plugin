package uk.co.jamesridgway.gradle.gitflow.plugin;

import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import uk.co.jamesridgway.gradle.gitflow.plugin.version.GitFlowVersionConfig;

public class GitFlowPluginExtension implements GitFlowVersionConfig {

    private final Property<String> unreleasedVersionTemplate;

    public GitFlowPluginExtension(final Project project) {
        unreleasedVersionTemplate = project.getObjects().property(String.class);
        unreleasedVersionTemplate.set(GitFlowVersionConfig.DEFAULT.getUnreleaseVersionTemplate());
    }

    @Override
    public String getUnreleaseVersionTemplate() {
        return unreleasedVersionTemplate.get();
    }
}

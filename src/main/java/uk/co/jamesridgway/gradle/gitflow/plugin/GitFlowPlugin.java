package uk.co.jamesridgway.gradle.gitflow.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import uk.co.jamesridgway.gradle.gitflow.plugin.version.GitFlowVersionProvider;
import uk.co.jamesridgway.gradle.gitflow.plugin.version.Version;

public class GitFlowPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        GitFlowPluginExtension gitFlowPluginExtension = project.getExtensions()
                .create("gitflow", GitFlowPluginExtension.class, project);

        GitFlowVersionProvider versionProvider = new GitFlowVersionProvider(gitFlowPluginExtension);
        Version version = versionProvider.getVersion(project);

        project.setVersion(version);
    }

}

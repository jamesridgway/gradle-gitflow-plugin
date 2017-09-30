package uk.co.jamesridgway.gradle.gitflow.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import uk.co.jamesridgway.gradle.gitflow.plugin.version.GitFlowVersion;

public class GitFlowPlugin implements Plugin<Project> {

    @Override
    public void apply(final Project project) {
        project.getExtensions().create("gitflow", GitFlowPluginExtension.class);
        project.setVersion(new GitFlowVersion(project));
    }

}

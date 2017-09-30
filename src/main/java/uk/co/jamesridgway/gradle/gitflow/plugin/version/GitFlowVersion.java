package uk.co.jamesridgway.gradle.gitflow.plugin.version;

import org.gradle.api.Project;
import uk.co.jamesridgway.gradle.gitflow.plugin.git.GitProject;

public class GitFlowVersion {

    private GitProject gitProject;

    public GitFlowVersion(final Project project) {
        this.gitProject = new GitProject(project);
    }
}

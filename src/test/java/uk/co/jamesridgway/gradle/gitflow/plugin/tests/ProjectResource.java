package uk.co.jamesridgway.gradle.gitflow.plugin.tests;

import org.eclipse.jgit.api.Git;
import org.junit.rules.ExternalResource;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static uk.co.jamesridgway.gradle.gitflow.plugin.utils.Exceptions.propagateAnyError;

public class ProjectResource extends ExternalResource {

    private TemporaryFolder temporaryFolder;

    private File projectFolder;

    private Git git;

    ProjectResource(final TemporaryFolder temporaryFolder) {
        this.temporaryFolder = temporaryFolder;
    }

    @Override
    protected void before() throws Throwable {
        this.projectFolder = temporaryFolder.newFolder();

        this.git = propagateAnyError(() -> {
            Git.init().setDirectory(projectFolder).call();
            return Git.open(projectFolder);
        });
    }

    File getProjectFolder() {
        return projectFolder;
    }

    Git getGit() {
        return git;
    }

}

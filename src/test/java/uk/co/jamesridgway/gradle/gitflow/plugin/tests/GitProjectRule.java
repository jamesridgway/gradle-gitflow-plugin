package uk.co.jamesridgway.gradle.gitflow.plugin.tests;

import org.eclipse.jgit.api.Git;
import org.junit.rules.RuleChain;
import org.junit.rules.TemporaryFolder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GitProjectRule implements TestRule {

    private final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final ProjectResource projectResource = new ProjectResource(temporaryFolder);

    @Override
    public Statement apply(final Statement statement, final Description description) {
        return RuleChain.outerRule(temporaryFolder)
                .around(projectResource)
                .apply(statement, description);
    }

    public File getProjectFolder() {
        return projectResource.getProjectFolder();
    }

    public Git getGit() {
        return projectResource.getGit();
    }

    public File createFile(final String filename, final String contents) {
        File file = new File(projectResource.getProjectFolder(), filename);
        Path path = Paths.get(file.getAbsolutePath());
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(contents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

}

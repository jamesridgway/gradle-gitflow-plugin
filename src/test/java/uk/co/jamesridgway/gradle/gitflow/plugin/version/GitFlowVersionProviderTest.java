package uk.co.jamesridgway.gradle.gitflow.plugin.version;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.jamesridgway.gradle.gitflow.plugin.GitFlowPluginExtension;
import uk.co.jamesridgway.gradle.gitflow.plugin.utils.Exceptions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.jamesridgway.gradle.gitflow.plugin.version.GitFlowVersionProvider.UNKNOWN_VERSION;

@RunWith(MockitoJUnitRunner.class)
public class GitFlowVersionProviderTest {

    private Git git;

    private Project project;

    private GitFlowPluginExtension gitFlowPluginExtension;

    private GitFlowVersionProvider gitFlowVersionProvider;

    @Before
    public void setUp() {
        project = ProjectBuilder.builder().build();
        git = Exceptions.propagateAnyError(() -> Git.init().setDirectory(project.getRootDir()).call());
        project.getPlugins().apply("uk.co.jamesridgway.gradle.gitflow.plugin");
        gitFlowPluginExtension = new GitFlowPluginExtension(project);
        gitFlowVersionProvider = new GitFlowVersionProvider(gitFlowPluginExtension);
    }

    @Test
    public void getVersionNoCommits() {
        assertThat(gitFlowVersionProvider.getVersion(project)).isEqualTo(UNKNOWN_VERSION);
    }

    @Test
    public void getVersionForARelease() throws Exception {
        createFile("readme.txt", "Hello world");
        git.add().addFilepattern("readme.txt").call();
        git.commit().setMessage("First commit").call();
        git.tag().setName("1.0.0").call();

        createFile("readme.2txt", "Goodbye world");
        git.add().addFilepattern("readme2.txt").call();
        git.commit().setMessage("Second commit").call();
        git.tag().setName("non-version-tag").call();
        git.tag().setName("1.9.9").call();
        git.tag().setName("2.0.0").call();

        assertThat(gitFlowVersionProvider.getVersion(project)).isEqualTo(new ReleaseVersion(2, 0, 0));
    }

    @Test
    public void getUnreleasedVersion() throws Exception {
        ignoreCurrentUntrackedChanges();

        createFile("readme.txt", "Hello world");
        git.add().addFilepattern("readme.txt").call();
        git.commit().setMessage("Release commit").call();
        git.tag().setName("1.0.0").call();

        git.branchCreate().setName("feature/james/FEAT-1").call();
        git.checkout().setName("feature/james/FEAT-1").call();

        createFile("readme2.txt", "First feature branch commit");
        git.add().addFilepattern("readme2.txt").call();
        git.commit().setMessage("First feature branch commit")
                .call();

        createFile("readme3.txt", "Second feature branch commit");
        git.add().addFilepattern("readme3.txt").call();
        RevCommit lastCommit = git.commit().setMessage("Second feature branch commit")
                .call();

        String shortCommitId = lastCommit.getId().getName().substring(0, 7);

        assertThat(gitFlowVersionProvider.getVersion(project))
                .isInstanceOf(UnreleasedVersion.class)
                .hasToString("1.0.0-feature/james/FEAT-1.2+sha." + shortCommitId);
    }

    @Test
    public void getUnreleasedVersionUntrackedChanges() throws Exception {
        ignoreCurrentUntrackedChanges();

        createFile("readme.txt", "Hello world");
        git.add().addFilepattern("readme.txt").call();
        git.commit().setMessage("Release commit").call();
        git.tag().setName("1.0.0").call();

        git.branchCreate().setName("feature/james/FEAT-1").call();
        git.checkout().setName("feature/james/FEAT-1").call();

        createFile("readme2.txt", "First feature branch commit");
        git.add().addFilepattern("readme2.txt").call();
        RevCommit lastCommit = git.commit().setMessage("First feature branch commit")
                .call();

        createFile("readme3.txt", "Second feature branch commit");

        assertThat(git.status().call().getUntracked()).containsOnly("readme3.txt");

        String shortCommitId = lastCommit.getId().getName().substring(0, 7);

        assertThat(gitFlowVersionProvider.getVersion(project))
                .isInstanceOf(UnreleasedVersion.class)
                .hasToString("1.0.0-feature/james/FEAT-1.1+sha." + shortCommitId + ".dirty");
    }

    private void ignoreCurrentUntrackedChanges() throws Exception {
        String gitIgnoreContents = git.status().call()
                .getUntracked().stream()
                .collect(Collectors.joining("\n"));
        createFile(".gitignore", gitIgnoreContents);

        git.add().addFilepattern(".gitignore").call();
        git.commit().setMessage("Creating .gitignore")
                .call();

        assertThat(git.status().call().getUntracked()).isEmpty();
    }

    private File createFile(final String filename, final String contents) {
        File file = new File(project.getRootDir(), filename);
        Path path = Paths.get(file.getAbsolutePath());
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(contents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

}

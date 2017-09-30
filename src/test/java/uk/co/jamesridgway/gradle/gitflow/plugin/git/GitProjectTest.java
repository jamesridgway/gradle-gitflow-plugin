package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gradle.api.Project;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static uk.co.jamesridgway.gradle.gitflow.plugin.utils.Exceptions.propagateAnyError;

@RunWith(MockitoJUnitRunner.class)
public class GitProjectTest {

    @Rule
    public TemporaryFolder rule = new TemporaryFolder();

    @Mock
    private Project project;

    private File projectFolder;

    @Before
    public void setUp() throws Exception {
        projectFolder = rule.newFolder();
        when(project.getRootDir()).thenReturn(projectFolder);
    }

    @Test
    public void initialiseSuccessfully() {
        setupGitRepository();
        GitProject gitProject = new GitProject(project);
        assertThat(gitProject).isNotNull();
    }

    @Test
    public void getBranch() {
        setupGitRepository();
        GitProject gitProject = new GitProject(project);
        assertThat(gitProject.getBranchName()).isEqualTo("master");
    }

    @Test
    public void isNotDirty() {
        setupGitRepository();
        GitProject gitProject = new GitProject(project);
        assertThat(gitProject.isDirty()).isFalse();
    }

    @Test
    public void isDirty() {
        setupGitRepository();
        createFile(new File(projectFolder, "readme.txt"), "Hello world");

        GitProject gitProject = new GitProject(project);
        assertThat(gitProject.isDirty()).isTrue();
    }

    @Test
    public void getHeadCommit() throws Exception {
        Git git = setupGitRepository();
        createFile(new File(projectFolder, "readme.txt"), "Hello world");
        git.add().addFilepattern("readme.txt").call();
        git.commit().setMessage("First commit").call();

        createFile(new File(projectFolder, "readme.2txt"), "Goodbye world");
        git.add().addFilepattern("readme2.txt").call();
        RevCommit secondCommit = git.commit().setMessage("Second commit").call();

        GitProject gitProject = new GitProject(project);
        assertThat(gitProject.getHeadCommit()).contains(new Commit(secondCommit));
    }

    @Test
    public void getHeadCommitNoCommits() throws Exception {
        setupGitRepository();
        GitProject gitProject = new GitProject(project);
        assertThat(gitProject.getHeadCommit()).isEmpty();
    }

    @Test
    public void errorIfUnableToFindGitFolder() {
        assertThatThrownBy(() -> new GitProject(project))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Could not find git directory, expecting %s to exist.",
                        new File(projectFolder, ".git").getAbsolutePath());
    }

    private Git setupGitRepository() {
        return propagateAnyError(() -> {
            Git.init().setDirectory(projectFolder).call();
            return Git.open(projectFolder);
        });
    }

    private File createFile(final File file, final String contents) {
        Path path = Paths.get(file.getAbsolutePath());
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(contents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

}

package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import org.eclipse.jgit.revwalk.RevCommit;
import org.gradle.api.Project;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.jamesridgway.gradle.gitflow.plugin.tests.GitProjectRule;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GitProjectTest {

    @Rule
    public GitProjectRule rule = new GitProjectRule();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Mock
    private Project project;

    private File projectFolder;

    @Before
    public void setUp() throws Exception {
        projectFolder = rule.getProjectFolder();
        when(project.getRootDir()).thenReturn(projectFolder);
    }

    @Test
    public void initialiseSuccessfully() {
        GitProject gitProject = new GitProject(project);
        assertThat(gitProject).isNotNull();
    }

    @Test
    public void getBranch() {
        GitProject gitProject = new GitProject(project);
        assertThat(gitProject.getBranchName()).isEqualTo("master");
    }

    @Test
    public void isNotDirty() {
        GitProject gitProject = new GitProject(project);
        assertThat(gitProject.isDirty()).isFalse();
    }

    @Test
    public void isDirty() {
        rule.createFile("readme.txt", "Hello world");

        GitProject gitProject = new GitProject(project);
        assertThat(gitProject.isDirty()).isTrue();
    }

    @Test
    public void getHeadCommit() throws Exception {
        rule.createFile("readme.txt", "Hello world");
        rule.getGit().add().addFilepattern("readme.txt").call();
        rule.getGit().commit().setMessage("First commit").call();
        rule.getGit().tag().setName("1.0.0").call();

        rule.createFile("readme.2txt", "Goodbye world");
        rule.getGit().add().addFilepattern("readme2.txt").call();
        RevCommit secondCommit = rule.getGit().commit().setMessage("Second commit").call();
        rule.getGit().tag().setName("2.0.0").call();

        GitProject gitProject = new GitProject(project);
        assertThat(gitProject.getHeadCommit()).contains(new Commit(rule.getGit(), secondCommit));
    }

    @Test
    public void getHeadCommitNoCommits() throws Exception {
        GitProject gitProject = new GitProject(project);
        assertThat(gitProject.getHeadCommit()).isEmpty();
    }

    @Test
    public void errorIfUnableToFindGitFolder() throws Exception {
        projectFolder = temporaryFolder.newFolder();
        when(project.getRootDir()).thenReturn(projectFolder);
        assertThatThrownBy(() -> new GitProject(project))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Could not find git directory, expecting %s to exist.",
                        new File(projectFolder, ".git").getAbsolutePath());
    }

    @Test
    public void getAllTags() throws Exception {
        rule.createFile("readme.txt", "Hello world");
        rule.getGit().add().addFilepattern("readme.txt").call();
        RevCommit firstCommit = rule.getGit().commit().setMessage("First commit").call();
        rule.getGit().tag().setName("1.0.0").call();

        rule.createFile("readme.2txt", "Goodbye world");
        rule.getGit().add().addFilepattern("readme2.txt").call();
        RevCommit secondCommit = rule.getGit().commit().setMessage("Second commit").call();
        rule.getGit().tag().setName("2.0.0").call();

        GitProject gitProject = new GitProject(project);
        assertThat(gitProject.getAllTags()).containsOnly(
                new Tag(new Commit(rule.getGit(), firstCommit), "refs/tags/1.0.0"),
                new Tag(new Commit(rule.getGit(), secondCommit), "refs/tags/2.0.0")
        );
    }
}

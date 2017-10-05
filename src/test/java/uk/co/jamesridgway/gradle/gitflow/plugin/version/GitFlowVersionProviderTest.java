package uk.co.jamesridgway.gradle.gitflow.plugin.version;

import org.gradle.api.Project;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.jamesridgway.gradle.gitflow.plugin.tests.GitProjectRule;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.co.jamesridgway.gradle.gitflow.plugin.version.GitFlowVersionProvider.UNKNOWN_VERSION;

@RunWith(MockitoJUnitRunner.class)
public class GitFlowVersionProviderTest {

    @Rule
    public GitProjectRule rule = new GitProjectRule();

    @Mock
    private Project project;

    private GitFlowVersionProvider gitFlowVersionProvider;

    @Before
    public void setUp() {
        File projectFolder = rule.getProjectFolder();
        when(project.getRootDir()).thenReturn(projectFolder);
        gitFlowVersionProvider = new GitFlowVersionProvider();
    }

    @Test
    public void getVersionNoCommits() {
        assertThat(gitFlowVersionProvider.getVersion(project)).isEqualTo(UNKNOWN_VERSION);
    }

    @Test
    public void getVersionForARelease() throws Exception {
        rule.createFile("readme.txt", "Hello world");
        rule.getGit().add().addFilepattern("readme.txt").call();
        rule.getGit().commit().setMessage("First commit").call();
        rule.getGit().tag().setName("1.0.0").call();

        rule.createFile("readme.2txt", "Goodbye world");
        rule.getGit().add().addFilepattern("readme2.txt").call();
        rule.getGit().commit().setMessage("Second commit").call();
        rule.getGit().tag().setName("non-version-tag").call();
        rule.getGit().tag().setName("1.9.9").call();
        rule.getGit().tag().setName("2.0.0").call();

        assertThat(gitFlowVersionProvider.getVersion(project)).isEqualTo(new ReleaseVersion(2, 0, 0));
    }

}

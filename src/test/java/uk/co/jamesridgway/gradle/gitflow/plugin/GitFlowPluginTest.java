package uk.co.jamesridgway.gradle.gitflow.plugin;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;
import uk.co.jamesridgway.gradle.gitflow.plugin.version.UnknownVersion;
import uk.co.jamesridgway.gradle.gitflow.plugin.version.UnreleasedVersion;

import static org.assertj.core.api.Assertions.assertThat;

public class GitFlowPluginTest {

    @Test
    public void buildAndApplyPlugin() throws Exception {
        Project project = ProjectBuilder.builder().build();
        Git.init().setDirectory(project.getRootDir()).call();
        project.getPlugins().apply("uk.co.jamesridgway.gradle.gitflow.plugin");

        assertThat(project.getExtensions().getByName("gitflow"))
                .isInstanceOf(GitFlowPluginExtension.class)
                .isNotNull();

        assertThat(project.getVersion())
                .isEqualTo(new UnknownVersion());
    }

    @Test
    public void buildAndApplyPluginWithGitHistory() throws Exception {
        Project project = ProjectBuilder.builder().build();
        Git git = Git.init().setDirectory(project.getRootDir()).call();

        git.commit()
                .setMessage("commit1")
                .setAllowEmpty(true)
                .call();

        git.tag()
                .setName("1.0.0")
                .call();

        git.branchCreate().setName("feature/james/FEAT-1").call();
        git.checkout().setName("feature/james/FEAT-1").call();

        git.commit()
                .setMessage("commit2")
                .setAllowEmpty(true)
                .call();

        RevCommit commit3 = git.commit()
                .setMessage("commit3")
                .setAllowEmpty(true)
                .call();

        String shortCommitId = commit3.getId().getName().substring(0, 7);

        project.getPlugins().apply("uk.co.jamesridgway.gradle.gitflow.plugin");

        assertThat(project.getExtensions().getByName("gitflow"))
                .isInstanceOf(GitFlowPluginExtension.class)
                .isNotNull();

        assertThat(project.getVersion())
                .isInstanceOf(UnreleasedVersion.class)
                .hasToString("1.0.0-feature/james/FEAT-1.2+sha." + shortCommitId);
    }

}

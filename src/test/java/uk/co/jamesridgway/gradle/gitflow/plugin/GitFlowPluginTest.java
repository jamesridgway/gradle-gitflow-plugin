package uk.co.jamesridgway.gradle.gitflow.plugin;

import org.eclipse.jgit.api.Git;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;
import uk.co.jamesridgway.gradle.gitflow.plugin.utils.Exceptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GitFlowPluginTest {

    @Test
    public void apply() {
        ExtensionContainer extensionContainer = mock(ExtensionContainer.class);
        Project mockProject = mock(Project.class);
        when(mockProject.getExtensions()).thenReturn(extensionContainer);

        GitFlowPlugin gitFlowPlugin = new GitFlowPlugin();
        gitFlowPlugin.apply(mockProject);

        verify(extensionContainer).create("gitflow", GitFlowPluginExtension.class, mockProject);
        verify(mockProject).setVersion(any());
    }

    @Test
    public void buildAndApplyPlugin() throws Exception {
        Project project = setupTestProject();

        assertThat(project.getExtensions().getByName("gitflow"))
                .isInstanceOf(GitFlowPluginExtension.class)
                .isNotNull();
    }

    @Test
    public void isMasterBranch() throws Exception {
        Project project = setupTestProject();
        GitFlowPluginExtension pluginExtension = project.getExtensions().getByType(GitFlowPluginExtension.class);

        assertThat(pluginExtension.isMasterBranch("master")).isTrue();
        assertThat(pluginExtension.isMasterBranch("masterx")).isFalse();
        assertThat(pluginExtension.isMasterBranch("develop")).isFalse();
    }

    @Test
    public void isDevelopBranch() throws Exception {
        Project project = setupTestProject();
        GitFlowPluginExtension pluginExtension = project.getExtensions().getByType(GitFlowPluginExtension.class);

        assertThat(pluginExtension.isDevelopBranch("develop")).isTrue();
        assertThat(pluginExtension.isDevelopBranch("development")).isFalse();
        assertThat(pluginExtension.isDevelopBranch("develop/")).isFalse();
    }

    @Test
    public void isFeatureBranch() throws Exception {
        Project project = setupTestProject();
        GitFlowPluginExtension pluginExtension = project.getExtensions().getByType(GitFlowPluginExtension.class);

        assertThat(pluginExtension.isFeatureBranch("feature/xyz")).isTrue();
        assertThat(pluginExtension.isFeatureBranch("feature/")).isFalse();
        assertThat(pluginExtension.isFeatureBranch("feature")).isFalse();
        assertThat(pluginExtension.isFeatureBranch("develop")).isFalse();
    }

    @Test
    public void isReleaseBranch() throws Exception {
        Project project = setupTestProject();
        GitFlowPluginExtension pluginExtension = project.getExtensions().getByType(GitFlowPluginExtension.class);

        assertThat(pluginExtension.isReleaseBranch("release/xyz")).isTrue();
        assertThat(pluginExtension.isReleaseBranch("release")).isFalse();
        assertThat(pluginExtension.isReleaseBranch("release/")).isFalse();
        assertThat(pluginExtension.isReleaseBranch("develop")).isFalse();
    }

    @Test
    public void isHotfixBranch() throws Exception {
        Project project = setupTestProject();
        GitFlowPluginExtension pluginExtension = project.getExtensions().getByType(GitFlowPluginExtension.class);

        assertThat(pluginExtension.isHotfixBranch("hotfix/xyz")).isTrue();
        assertThat(pluginExtension.isHotfixBranch("hotfix/")).isFalse();
        assertThat(pluginExtension.isHotfixBranch("hotfix")).isFalse();
        assertThat(pluginExtension.isHotfixBranch("develop")).isFalse();
    }

    private Project setupTestProject() {
        Project project = ProjectBuilder.builder().build();
        Exceptions.propagateAnyError(() -> Git.init().setDirectory(project.getRootDir()).call());
        project.getPlugins().apply("uk.co.jamesridgway.gradle.gitflow.plugin");
        return project;
    }

}

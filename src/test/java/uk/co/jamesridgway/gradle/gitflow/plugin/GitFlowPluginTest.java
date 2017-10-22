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

    private Project setupTestProject() {
        Project project = ProjectBuilder.builder().build();
        Exceptions.propagateAnyError(() -> Git.init().setDirectory(project.getRootDir()).call());
        project.getPlugins().apply("uk.co.jamesridgway.gradle.gitflow.plugin");
        return project;
    }

}

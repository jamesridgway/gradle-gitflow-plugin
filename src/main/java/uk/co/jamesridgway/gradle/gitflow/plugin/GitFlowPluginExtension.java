package uk.co.jamesridgway.gradle.gitflow.plugin;

import org.gradle.api.Project;
import uk.co.jamesridgway.gradle.gitflow.plugin.version.GitFlowVersionConfig;

import java.util.Optional;

public class GitFlowPluginExtension implements GitFlowVersionConfig {

    private final Project project;

    public GitFlowPluginExtension(final Project project) {
        this.project = project;
    }

    @Override
    public String getUnreleasedVersionTemplate() {
        return findProperty("gitflow.unreleasedVersionTemplate").orElse("${major}.${minor}.${patch}"
                + ".${commitsSinceLastTag}-${branch?replace('/', '_')}"
                + "+sha.${commitId?substring(0,7)}${dirty?then('.dirty','')}");
    }

    private Optional<String> findProperty(final String name) {
        return Optional.ofNullable(project.findProperty(name))
                .filter(value -> project.getGradle().getParent() == null)
                .map(Object::toString);
    }

}

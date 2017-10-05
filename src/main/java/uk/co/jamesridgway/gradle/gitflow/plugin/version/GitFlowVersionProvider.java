package uk.co.jamesridgway.gradle.gitflow.plugin.version;

import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.jamesridgway.gradle.gitflow.plugin.git.Commit;
import uk.co.jamesridgway.gradle.gitflow.plugin.git.GitProject;

import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class GitFlowVersionProvider implements VersionProvider {

    private static final Logger L = LoggerFactory.getLogger(GitFlowVersionProvider.class);

    static final Version UNKNOWN_VERSION = new UnknownVersion();

    @Override
    public Version getVersion(final Project project) {

        final GitProject gitProject = new GitProject(project);

        if (!gitProject.hasHeadCommit()) {
            L.warn("No HEAD commit found, using version: {}", UNKNOWN_VERSION.getVersionString());
            return UNKNOWN_VERSION;
        }

        Commit headCommit = gitProject.getHeadCommit().get();
        if (headCommit.isTagged()) {
            Set<ReleaseVersion> candidateReleaseVersion = headCommit.getTags().stream()
                    .map(tag -> ReleaseVersion.parse(tag.getShortTagName()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(toSet());
            Optional<ReleaseVersion> releaseVersion = ReleaseVersion.findLatest(candidateReleaseVersion);
            if (releaseVersion.isPresent()) {
                return releaseVersion.get();
            }
        }

        // TODO - Else, Unreleased version

        return UNKNOWN_VERSION;
    }
}

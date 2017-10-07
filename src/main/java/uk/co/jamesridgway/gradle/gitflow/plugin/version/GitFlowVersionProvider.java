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
            Optional<ReleaseVersion> releaseVersion = inferReleaseVersion(headCommit);
            if (releaseVersion.isPresent()) {
                return releaseVersion.get();
            }
        }

        // TODO - Else, Unreleased version
        /*
         * 1. Find all tags
         * 2. Find all the tags that are ancestors of head
         * 3. Work out their distance from head
         * 4. Pick the tag with the shortest distance.
         */

        return UNKNOWN_VERSION;
    }

    private Optional<ReleaseVersion> inferReleaseVersion(final Commit headCommit) {
        Set<ReleaseVersion> candidateReleaseVersion = headCommit.getTags().stream()
                .map(tag -> ReleaseVersion.parse(tag.getShortTagName()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toSet());
        return ReleaseVersion.findLatest(candidateReleaseVersion);
    }
}

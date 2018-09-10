package uk.co.jamesridgway.gradle.gitflow.plugin.version;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.jamesridgway.gradle.gitflow.plugin.git.Commit;
import uk.co.jamesridgway.gradle.gitflow.plugin.git.DistanceToTags;
import uk.co.jamesridgway.gradle.gitflow.plugin.git.GitProject;
import uk.co.jamesridgway.gradle.gitflow.plugin.git.Tag;

import java.io.File;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class GitFlowVersionProvider {

    private static final Logger L = LoggerFactory.getLogger(GitFlowVersionProvider.class);

    static final Version UNKNOWN_VERSION = new UnknownVersion();

    private final GitFlowVersionConfig config;

    public GitFlowVersionProvider(final GitFlowVersionConfig config) {
        this.config = config;
    }

    public Version getVersion(final File file) {

        final GitProject gitProject = new GitProject(file);

        if (!gitProject.hasHeadCommit()) {
            L.warn("No HEAD commit found, using version: {}", UNKNOWN_VERSION.getVersionString());
            return UNKNOWN_VERSION;
        }

        Commit headCommit = gitProject.getHeadCommit().get();
        if (headCommit.isTagged() && !gitProject.isDirty()) {
            Optional<ReleaseVersion> releaseVersion = inferReleaseVersion(headCommit);
            if (releaseVersion.isPresent()) {
                return releaseVersion.get();
            }
        }

        return inferUnreleasedVersion(gitProject).orElse(UNKNOWN_VERSION);
    }

    private Optional<ReleaseVersion> inferReleaseVersion(final Commit headCommit) {
        Set<ReleaseVersion> candidateReleaseVersion = headCommit.getTags().stream()
                .map(tag -> ReleaseVersion.parse(tag.getShortTagName()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toSet());
        return ReleaseVersion.findLatest(candidateReleaseVersion);
    }

    private Optional<Version> inferUnreleasedVersion(final GitProject gitProject) {
        final Set<Tag> tags = gitProject.getAllTags();
        if (tags.isEmpty() || !gitProject.getHeadCommit().isPresent()) {
            return Optional.empty();
        }

        Optional<DistanceToTags> distanceToMostRecentTag = gitProject.getDistanceToMostRecentTags(tags);

        if (!distanceToMostRecentTag.isPresent()) {
            return Optional.empty();
        }

        final Set<ReleaseVersion> releaseVersions = distanceToMostRecentTag.get().getTags().stream()
                .map(tag -> ReleaseVersion.parse(tag.getShortTagName()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toSet());

        final ReleaseVersion latestReleaseVersion = ReleaseVersion.findLatest(releaseVersions).get();
        final String commitId = gitProject.getHeadCommit().get().getCommitId();
        final int distanceFromLastTag = distanceToMostRecentTag.get().getDistance();

        UnreleasedVersion unreleasedVersion = UnreleasedVersion.build(latestReleaseVersion,
                config.getUnreleasedVersionTemplate())
                .withCommitsSinceLastTag(distanceFromLastTag)
                .withCommitId(commitId)
                .withBranch(gitProject.getBranchName())
                .withDirty(gitProject.isDirty())
                .create();

        return Optional.of(unreleasedVersion);

    }
}

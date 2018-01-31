package uk.co.jamesridgway.gradle.gitflow.plugin.version;

import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.jamesridgway.gradle.gitflow.plugin.GitFlowPluginExtension;
import uk.co.jamesridgway.gradle.gitflow.plugin.git.Commit;
import uk.co.jamesridgway.gradle.gitflow.plugin.git.GitProject;
import uk.co.jamesridgway.gradle.gitflow.plugin.git.Tag;

import java.util.List;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class GitFlowVersionProvider implements VersionProvider {

    private static final Logger L = LoggerFactory.getLogger(GitFlowVersionProvider.class);

    static final Version UNKNOWN_VERSION = new UnknownVersion();

    private final GitFlowPluginExtension gitFlowPluginExtension;

    public GitFlowVersionProvider(final GitFlowPluginExtension gitFlowPluginExtension) {
        this.gitFlowPluginExtension = gitFlowPluginExtension;
    }

    @Override
    public Version getVersion(final Project project) {

        final GitProject gitProject = new GitProject(project);

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

        Commit headCommit = gitProject.getHeadCommit().get();

        NavigableMap<Integer, List<Tag>> ancestorTags = new TreeMap<>(tags.stream()
                .filter(t -> headCommit.hasAncestorOf(t.getCommit()))
                .filter(t -> ReleaseVersion.parse(t.getShortTagName()).isPresent())
                .collect(Collectors.groupingBy(t -> headCommit.getDistanceFrom(t.getCommit()))));

        if (ancestorTags.isEmpty()) {
            return Optional.empty();
        }

        final int distanceFromLastTag = ancestorTags.firstEntry().getKey();
        final Set<ReleaseVersion> closestTags = ancestorTags.firstEntry().getValue().stream()
                .map(tag -> ReleaseVersion.parse(tag.getShortTagName()).get())
                .collect(Collectors.toSet());
        final ReleaseVersion latestReleaseVersion = ReleaseVersion.findLatest(closestTags).get();

        UnreleasedVersion unreleasedVersion = UnreleasedVersion.build(latestReleaseVersion,
                gitFlowPluginExtension.getUnreleaseVersionTemplate())
                .withCommitsSinceLastTag(distanceFromLastTag)
                .withCommitId(headCommit.getCommitId())
                .withBranch(gitProject.getBranchName())
                .withDirty(gitProject.isDirty())
                .create();
        return Optional.of(unreleasedVersion);
    }
}

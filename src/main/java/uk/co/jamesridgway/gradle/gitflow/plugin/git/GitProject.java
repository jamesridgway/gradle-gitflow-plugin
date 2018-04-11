package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import uk.co.jamesridgway.gradle.gitflow.plugin.utils.Exceptions;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.groupingBy;
import static uk.co.jamesridgway.gradle.gitflow.plugin.utils.Exceptions.propagateAnyError;

public class GitProject {

    private final Git git;

    public GitProject(final File file) {
        final File gitDirectory = new File(file, ".git");
        if (!gitDirectory.exists() || !gitDirectory.isDirectory()) {
            throw new RuntimeException(String.format("Could not find git directory, expecting %s to exist.",
                    gitDirectory.getAbsolutePath()));
        }
        git = propagateAnyError(() -> Git.open(gitDirectory));
    }

    public Optional<Commit> getHeadCommit() {
        Ref ref = propagateAnyError(() -> git.getRepository().findRef(Constants.HEAD));
        if (ref.getObjectId() == null) {
            return Optional.empty();
        }
        RevCommit revCommit = propagateAnyError(() -> git.getRepository().parseCommit(ref.getObjectId()));

        return Optional.of(new Commit(git, revCommit));
    }

    public boolean hasHeadCommit() {
        return getHeadCommit().isPresent();
    }

    public Set<Tag> getAllTags() {
        List<Ref> tagRefs = Exceptions.propagateAnyError(() -> git.tagList().call());
        Set<Tag> tags = new HashSet<>();
        try (RevWalk walk = new RevWalk(git.getRepository())) {
            for (Ref tagRef : tagRefs) {
                final AnyObjectId commitId;
                if (tagRef instanceof RevTag) {
                    RevTag rev = Exceptions.propagateAnyError(() -> walk.parseTag(tagRef.getObjectId()));
                    RevObject target = Exceptions.propagateAnyError(() -> walk.peel(rev));
                    commitId = target.getId();
                } else {
                    commitId = tagRef.getObjectId();
                }
                RevCommit revCommit = propagateAnyError(() -> git.getRepository().parseCommit(commitId));
                final Commit commit = new Commit(git, revCommit);
                tags.addAll(commit.getTags());
            }
        }
        return tags;
    }

    public Optional<DistanceToTags> getDistanceToMostRecentTags(final Set<Tag> tags) {

        Map<String, List<Tag>> tagCommits = tags.stream().collect(groupingBy(t -> t.getCommit().getCommitId()));

        RevWalk walk = new RevWalk(git.getRepository());
        Ref headCommit = propagateAnyError(() -> git.getRepository().findRef(Constants.HEAD));

        propagateAnyError(() -> walk.markStart(walk.parseCommit(headCommit.getObjectId())));
        walk.sort(RevSort.COMMIT_TIME_DESC, true);
        AtomicInteger count = new AtomicInteger();

        RevCommit commit = propagateAnyError(walk::next);

        while (commit != null) {

            if (tagCommits.containsKey(commit.getName())) {
                return Optional.of(new DistanceToTags(tagCommits.get(commit.getName()), count.get()));

            }
            count.getAndIncrement();
            commit = propagateAnyError(walk::next);
        }
        walk.close();

        return Optional.empty();
    }


    public String getBranchName() {
        return propagateAnyError(() -> git.getRepository().getBranch());
    }

    public boolean isDirty() {
        return propagateAnyError(() -> !git.status().call().isClean());
    }

}

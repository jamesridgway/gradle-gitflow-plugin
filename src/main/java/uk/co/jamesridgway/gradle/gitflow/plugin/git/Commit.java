package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Collections.unmodifiableSet;
import static uk.co.jamesridgway.gradle.gitflow.plugin.utils.Exceptions.propagateAnyError;

public class Commit {

    private final Git git;
    private final RevCommit revCommit;

    Commit(final Git git, final RevCommit revCommit) {
        this.git = git;
        this.revCommit = revCommit;
    }

    public String getCommitId() {
        return revCommit.getName();
    }

    public Set<Tag> getTags() {
        List<Ref> tagRefs = propagateAnyError(() -> git.tagList().call());
        Set<Tag> tags = new HashSet<>();
        for (Ref tagRef : tagRefs) {
            Ref peeledRed = propagateAnyError(() -> git.getRepository().peel(tagRef));
            ObjectId commitId = peeledRed.getPeeledObjectId() != null ? peeledRed.getPeeledObjectId()
                    : peeledRed.getObjectId();
            if (ObjectId.toString(commitId).equals(getCommitId())) {
                tags.add(new Tag(this, tagRef.getName()));
            }
        }
        return unmodifiableSet(tags);
    }

    public boolean isTagged() {
        return !getTags().isEmpty();
    }

    public boolean hasAncestorOf(final Commit base) {
        RevWalk revWalk = new RevWalk(git.getRepository());
        try {
            RevCommit baseCommit = revWalk.lookupCommit(git.getRepository().resolve(base.getCommitId()));
            RevCommit tipCommit = revWalk.lookupCommit(git.getRepository().resolve(this.getCommitId()));
            return revWalk.isMergedInto(baseCommit, tipCommit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getDistanceFrom(final Commit commit) {
        if (!this.hasAncestorOf(commit)) {
            return -1;
        }
        Iterable<RevCommit> revCommits = propagateAnyError(() -> git.log()
                .addRange(commit.revCommit.getId(), this.revCommit.getId())
                .call());
        int distance = 0;
        for (RevCommit rc : revCommits) {
            distance++;
        }
        return distance;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Commit commit = (Commit) o;
        return Objects.equals(revCommit, commit.revCommit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(revCommit);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("revCommit", revCommit)
                .toString();
    }

}

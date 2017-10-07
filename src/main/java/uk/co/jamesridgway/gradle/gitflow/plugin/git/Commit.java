package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Collections.unmodifiableSet;

public class Commit {

    private final RevCommit revCommit;

    private final Set<Tag> tags = new HashSet<>();

    Commit(final RevCommit revCommit) {
        this.revCommit = revCommit;
    }

    Commit(final RevCommit revCommit, final Set<Tag> tags) {
        this.revCommit = revCommit;
        this.tags.addAll(tags);
    }

    public String getCommitId() {
        return revCommit.getName();
    }

    public Set<Tag> getTags() {
        return unmodifiableSet(tags);
    }

    public boolean isTagged() {
        return !tags.isEmpty();
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

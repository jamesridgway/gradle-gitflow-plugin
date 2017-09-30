package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import org.eclipse.jgit.revwalk.RevCommit;

import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

public class Commit {

    private final RevCommit revCommit;

    Commit(final RevCommit revCommit) {
        this.revCommit = revCommit;
    }

    public String getCommitId() {
        return revCommit.getName();
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

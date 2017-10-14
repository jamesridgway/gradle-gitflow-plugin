package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import uk.co.jamesridgway.gradle.gitflow.plugin.utils.Exceptions;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.google.common.base.MoreObjects.toStringHelper;
import static java.util.Collections.unmodifiableSet;

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
        List<Ref> tagRefs = Exceptions.propagateAnyError(() -> git.tagList().call());
        Set<Tag> tags = new HashSet<>();
        for (Ref tagRef : tagRefs) {
            RevWalk walk = new RevWalk(git.getRepository());
            RevTag rev = Exceptions.propagateAnyError(() -> walk.parseTag(tagRef.getObjectId()));
            RevObject target = Exceptions.propagateAnyError(() -> walk.peel(rev));
            String tagCommitId = ObjectId.toString(target.getId());
            if (ObjectId.toString(revCommit.getId()).equals(tagCommitId)) {
                tags.add(new Tag(tagRef.getName()));
            }
        }
        return unmodifiableSet(tags);
    }

    public boolean isTagged() {
        return !getTags().isEmpty();
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

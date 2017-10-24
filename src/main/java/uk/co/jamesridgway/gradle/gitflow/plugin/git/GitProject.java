package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gradle.api.Project;
import uk.co.jamesridgway.gradle.gitflow.plugin.utils.Exceptions;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static uk.co.jamesridgway.gradle.gitflow.plugin.utils.Exceptions.propagateAnyError;

public class GitProject {

    private final Git git;

    public GitProject(final Project project) {
        final File gitDirectory = new File(project.getRootDir(), ".git");
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

    @Deprecated
    public boolean isAncestorOf(final Commit base, final Commit tip) {
        RevWalk revWalk = new RevWalk(git.getRepository());
        try {
            RevCommit baseCommit = revWalk.lookupCommit(git.getRepository().resolve(base.getCommitId()));
            RevCommit tipCommit = revWalk.lookupCommit(git.getRepository().resolve(tip.getCommitId()));
            return revWalk.isMergedInto(baseCommit, tipCommit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public String getBranchName() {
        return propagateAnyError(() -> git.getRepository().getBranch());
    }

    public boolean isDirty() {
        return propagateAnyError(() -> !git.status().call().isClean());
    }

}

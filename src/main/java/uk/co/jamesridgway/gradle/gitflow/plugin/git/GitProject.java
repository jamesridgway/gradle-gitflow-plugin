package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.gradle.api.Project;
import uk.co.jamesridgway.gradle.gitflow.plugin.utils.Exceptions;

import java.io.File;
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
        Set<Tag> tags = findTags(revCommit);

        return Optional.of(new Commit(revCommit, tags));
    }

    public String getBranchName() {
        return propagateAnyError(() -> git.getRepository().getBranch());
    }

    public boolean isDirty() {
        return propagateAnyError(() -> !git.status().call().isClean());
    }

    private Set<Tag> findTags(final RevCommit revCommit) {
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

        return tags;
    }

    private ObjectId getTagObjectId(final Ref ref) {
        return (ref.getPeeledObjectId() == null) ? ref.getObjectId() : ref.getPeeledObjectId();
    }

    public boolean hasHeadCommit() {
        return getHeadCommit().isPresent();
    }
}

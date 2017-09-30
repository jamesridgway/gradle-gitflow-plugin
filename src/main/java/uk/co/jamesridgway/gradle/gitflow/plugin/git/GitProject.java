package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gradle.api.Project;

import java.io.File;
import java.util.Optional;

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
        return Optional.of(new Commit(revCommit));
    }

    public String getBranchName() {
        return propagateAnyError(() -> git.getRepository().getBranch());
    }

    public boolean isDirty() {
        return propagateAnyError(() -> !git.status().call().isClean());
    }

}

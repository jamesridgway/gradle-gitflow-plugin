package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import uk.co.jamesridgway.gradle.gitflow.plugin.tests.GitProjectRule;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class CommitTest {

    @Rule
    public GitProjectRule rule = new GitProjectRule();

    private RevCommit revCommit;

    private Commit commit;

    @Before
    public void setUp() throws Exception {
        rule.createFile("readme.txt", "Hello world");
        rule.getGit().add().addFilepattern("readme.txt").call();
        revCommit = rule.getGit().commit().setMessage("First commit").call();
        commit = new Commit(revCommit, Collections.emptySet());
    }

    @Test
    public void getCommitId() {
        assertThat(commit.getCommitId()).isEqualTo(revCommit.getName());
    }

}

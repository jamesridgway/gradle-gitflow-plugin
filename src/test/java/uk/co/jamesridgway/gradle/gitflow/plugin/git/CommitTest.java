package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Rule;
import org.junit.Test;
import uk.co.jamesridgway.gradle.gitflow.plugin.tests.GitProjectRule;

import static org.assertj.core.api.Assertions.assertThat;

public class CommitTest {

    @Rule
    public GitProjectRule rule = new GitProjectRule();

    @Test
    public void getCommitId() throws Exception {
        rule.createFile("readme.txt", "Hello world");
        rule.getGit().add().addFilepattern("readme.txt").call();
        final RevCommit revCommit = rule.getGit().commit().setMessage("First commit").call();
        final Commit commit = new Commit(rule.getGit(), revCommit);

        assertThat(commit.getCommitId()).isEqualTo(revCommit.getName());
    }

    @Test
    public void getDistanceFrom() throws Exception {
        rule.createFile("readme.txt", "Hello world");
        rule.getGit().add().addFilepattern("readme.txt").call();
         RevCommit revCommit = rule.getGit().commit().setMessage("First commit").call();
        final Commit firstCommit = new Commit(rule.getGit(), revCommit);

        rule.createFile("readme2.txt", "Hello 2");
        rule.getGit().add().addFilepattern("readme2.txt").call();
        revCommit = rule.getGit().commit().setMessage("Second commit").call();
        final Commit secondCommit = new Commit(rule.getGit(), revCommit);

        rule.createFile("readme3.txt", "Hello 3");
        rule.getGit().add().addFilepattern("readme3.txt").call();
        revCommit = rule.getGit().commit().setMessage("Third commit").call();
        final Commit thirdCommit = new Commit(rule.getGit(), revCommit);

        rule.createFile("readme4.txt", "Hello 4");
        rule.getGit().add().addFilepattern("readme4.txt").call();
        revCommit = rule.getGit().commit().setMessage("Fourth commit").call();
        final Commit fourthCommit = new Commit(rule.getGit(), revCommit);

        assertThat(firstCommit.getDistanceFrom(firstCommit)).isEqualTo(0);
        assertThat(secondCommit.getDistanceFrom(firstCommit)).isEqualTo(1);
        assertThat(firstCommit.getDistanceFrom(secondCommit)).isEqualTo(-1);
        assertThat(thirdCommit.getDistanceFrom(firstCommit)).isEqualTo(2);
        assertThat(fourthCommit.getDistanceFrom(firstCommit)).isEqualTo(3);
        assertThat(fourthCommit.getDistanceFrom(secondCommit)).isEqualTo(2);
    }

    @Test
    public void hasAncestorOfBasicTest() throws Exception {
        rule.createFile("readme.txt", "Hello world");
        rule.getGit().add().addFilepattern("readme.txt").call();
        RevCommit firstCommit = rule.getGit().commit().setMessage("First commit").call();

        rule.createFile("readme.2txt", "Goodbye world");
        rule.getGit().add().addFilepattern("readme2.txt").call();
        RevCommit secondCommit = rule.getGit().commit().setMessage("Second commit").call();

        assertThat(new Commit(rule.getGit(), secondCommit).hasAncestorOf(new Commit(rule.getGit(), firstCommit)))
                .isTrue();
        assertThat(new Commit(rule.getGit(), firstCommit).hasAncestorOf(new Commit(rule.getGit(), secondCommit)))
                .isFalse();
    }

    @Test
    public void hasAncestorOfBranchedTest() throws Exception {
        rule.createFile("readme.txt", "Hello world");
        rule.getGit().add().addFilepattern("readme.txt").call();
        RevCommit firstCommit = rule.getGit().commit().setMessage("First commit").call();

        rule.getGit().branchCreate().setName("branchA").call();
        rule.getGit().checkout().setName("branchA").call();
        rule.createFile("readme.2txt", "This is branch A");
        rule.getGit().add().addFilepattern("readme2.txt").call();
        RevCommit branchACommit = rule.getGit().commit().setMessage("Second commit, but first commit on branch A")
                .call();

        rule.getGit().checkout().setName("master").call();

        rule.getGit().branchCreate().setName("branchB").call();
        rule.getGit().checkout().setName("branchB").call();
        rule.createFile("readme.3txt", "This is branch B");
        rule.getGit().add().addFilepattern("readme3.txt").call();
        RevCommit branchBCommit = rule.getGit().commit().setMessage("Third commit, but first commit on branch B")
                .call();

        rule.createFile("readme.4txt", "This is branch B, second commit");
        rule.getGit().add().addFilepattern("readme3.txt").call();
        RevCommit branchBCommit2 = rule.getGit().commit().setMessage("Fourth commit, but second commit on branch B")
                .call();

        assertThat(new Commit(rule.getGit(), branchACommit).hasAncestorOf(new Commit(rule.getGit(), firstCommit)))
                .isTrue();
        assertThat(new Commit(rule.getGit(), firstCommit).hasAncestorOf(new Commit(rule.getGit(), branchACommit)))
                .isFalse();

        assertThat(new Commit(rule.getGit(), branchBCommit).hasAncestorOf(new Commit(rule.getGit(), firstCommit)))
                .isTrue();
        assertThat(new Commit(rule.getGit(), firstCommit).hasAncestorOf(new Commit(rule.getGit(), branchBCommit)))
                .isFalse();

        assertThat(new Commit(rule.getGit(), branchACommit).hasAncestorOf(new Commit(rule.getGit(), branchBCommit)))
                .isFalse();
        assertThat(new Commit(rule.getGit(), branchBCommit).hasAncestorOf(new Commit(rule.getGit(), branchACommit)))
                .isFalse();

        assertThat(new Commit(rule.getGit(), branchBCommit2).hasAncestorOf(new Commit(rule.getGit(), branchBCommit)))
                .isTrue();
        assertThat(new Commit(rule.getGit(), branchBCommit2).hasAncestorOf(new Commit(rule.getGit(), firstCommit)))
                .isTrue();
        assertThat(new Commit(rule.getGit(), branchBCommit2).hasAncestorOf(new Commit(rule.getGit(), branchACommit)))
                .isFalse();
    }

    @Test
    public void getTag() throws Exception {
        rule.createFile("readme.txt", "Hello world");
        rule.getGit().add().addFilepattern("readme.txt").call();
        RevCommit revCommit = rule.getGit().commit().setMessage("First commit").call();
        rule.getGit().tag().setName("tag1").call();
        final Commit firstCommit = new Commit(rule.getGit(), revCommit);

        assertThat(firstCommit.getTags()).containsOnly(new Tag(firstCommit, "refs/tags/tag1"));
    }

    @Test
    public void getTagAnnotated() throws Exception {
        rule.createFile("readme.txt", "Hello world");
        rule.getGit().add().addFilepattern("readme.txt").call();
        RevCommit revCommit = rule.getGit().commit().setMessage("First commit").call();
        rule.getGit().tag()
                .setName("tag1")
                .setMessage("Annotated tag1")
                .setAnnotated(true)
                .call();
        final Commit firstCommit = new Commit(rule.getGit(), revCommit);

        assertThat(firstCommit.getTags()).containsOnly(new Tag(firstCommit, "refs/tags/tag1"));
    }
}

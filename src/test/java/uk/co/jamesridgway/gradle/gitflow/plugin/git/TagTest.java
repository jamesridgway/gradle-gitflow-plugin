package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Rule;
import org.junit.Test;
import uk.co.jamesridgway.gradle.gitflow.plugin.tests.GitProjectRule;

import static org.assertj.core.api.Assertions.assertThat;

public class TagTest {

    @Rule
    public GitProjectRule rule = new GitProjectRule();

    @Test
    public void checkTagDetails() throws Exception {
        rule.createFile("readme.txt", "Hello world");
        rule.getGit().add().addFilepattern("readme.txt").call();
        RevCommit revCommit = rule.getGit().commit().setMessage("First commit").call();
        rule.getGit().tag().setName("tag1").call();
        final Commit firstCommit = new Commit(rule.getGit(), revCommit);

        assertThat(firstCommit.getTags()).hasSize(1);

        Tag tag = firstCommit.getTags().iterator().next();
        assertThat(tag.getCommit()).isEqualTo(firstCommit);
        assertThat(tag.getTagName()).isEqualTo("refs/tags/tag1");
        assertThat(tag.getShortTagName()).isEqualTo("tag1");
    }

}

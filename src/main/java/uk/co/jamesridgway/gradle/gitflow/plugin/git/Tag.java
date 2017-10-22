package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.MoreObjects.toStringHelper;

public class Tag {

    private static final Pattern SHORT_TAG_PATTERN = Pattern.compile("^refs/tags/(.*)");

    private final Commit commit;
    private final String tag;
    private final String shortTag;

    Tag(final Commit commit, final String tag) {
        this.commit = commit;
        this.tag = tag;
        Matcher matcher = SHORT_TAG_PATTERN.matcher(tag);
        if (matcher.matches()) {
            this.shortTag = matcher.group(1);
        } else {
            this.shortTag = tag;
        }
    }

    public String getTagName() {
        return tag;
    }

    public String getShortTagName() {
        return shortTag;
    }

    public Commit getCommit() {
        return commit;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Tag tag1 = (Tag) o;
        return Objects.equals(commit, tag1.commit)
                && Objects.equals(tag, tag1.tag)
                && Objects.equals(shortTag, tag1.shortTag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commit, tag, shortTag);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("commit", commit)
                .add("tag", tag)
                .add("shortTag", shortTag)
                .toString();
    }
}

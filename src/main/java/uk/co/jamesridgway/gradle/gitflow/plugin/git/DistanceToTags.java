package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.joining;

public class DistanceToTags {

    private final List<Tag> tags = new ArrayList<>();
    private final int distance;

    public DistanceToTags(final List<Tag> tags, final int distance) {
        checkArgument(!tags.isEmpty(), "Must have at least one tag");
        this.tags.addAll(tags);
        this.distance = distance;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("distance", getDistance())
                .add("commit", tags.get(0).getCommit().getCommitId())
                .add("tags", tags.stream().map(Tag::getShortTagName).collect(joining(",")))
                .toString();
    }
}

package uk.co.jamesridgway.gradle.gitflow.plugin.git;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DistanceToTags that = (DistanceToTags) o;
        return Objects.equals(tags, that.tags)
                && Objects.equals(distance, that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tags, distance);
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

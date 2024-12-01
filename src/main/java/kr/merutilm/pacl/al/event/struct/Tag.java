package kr.merutilm.pacl.al.event.struct;

import javax.annotation.Nonnull;

import kr.merutilm.base.struct.Struct;
import kr.merutilm.base.struct.StructBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public record Tag(@Nonnull List<String> tags) implements Struct<Tag> {
    @Override
    @Nonnull
    public List<String> tags() {
        return Collections.unmodifiableList(tags);
    }

    public static Tag of(List<String> tags) {
        return new Tag(tags);
    }

    public static Tag of(String... tags) {
        return new Tag(Arrays.stream(tags).toList());
    }

    public boolean isEmpty() {
        return tags.isEmpty() || tags.get(0).isBlank();
    }

    @Nonnull
    public static Tag convert(String value) {
        if (value == null || value.isBlank()) {
            return new Tag(Collections.nCopies(1, ""));
        }
        return new Tag(Arrays.stream(value.split("[ ,]")).toList());
    }

    @Override
    public Builder edit() {
        return new Builder(new ArrayList<>(tags));
    }

    public static final class Builder implements StructBuilder<Tag> {
        private List<String> tags;

        public Builder(@Nonnull List<String> tags) {
            this.tags = tags;
        }

        public Builder setTag(@Nonnull String tag) {
            this.tags = new ArrayList<>();
            this.tags.add(tag);
            return this;
        }

        public Builder setTag(int index, @Nonnull String tag) {
            this.tags.set(index, tag);
            return this;
        }

        public Builder setTags(@Nonnull String... tags) {
            this.tags.addAll(Arrays.stream(tags).toList());
            return this;
        }

        public Builder addTag(@Nonnull String tag) {
            this.tags.add(tag);
            return this;
        }

        public Builder addTagAll(@Nonnull List<String> tag) {
            this.tags.addAll(tag);
            return this;
        }

        public Builder removeTag(@Nonnull String tag) {
            this.tags.remove(tag);
            return this;
        }

        @Override
        public Tag build() {
            return new Tag(Collections.unmodifiableList(tags));
        }
    }
    @Nonnull
    @Override
    public String toString() {
        return String.join(" ", tags);
    }
}

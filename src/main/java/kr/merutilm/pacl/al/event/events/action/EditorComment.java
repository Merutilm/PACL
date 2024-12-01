package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record EditorComment(Boolean active,
                            String comment) implements Actions {

    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setComment(comment);
    }

    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private String comment = "Write your own comment here!\\n\\nMulti-lines and <color=#00FF00>colored</color> texts are also supported.";
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setComment(@Nullable String comment) {
            this.comment = comment;
            return this;
        }

        @Override
        public EditorComment build() {
            return new EditorComment(active, comment);
        }
    }
}

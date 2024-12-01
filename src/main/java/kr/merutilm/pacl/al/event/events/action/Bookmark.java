package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record Bookmark(Boolean active) implements Actions {

    @Nonnull
    @Override
    public Builder edit() {
        return new Builder();
    }

    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        @Override
        public Bookmark build() {
            return new Bookmark(active);
        }
    }
}

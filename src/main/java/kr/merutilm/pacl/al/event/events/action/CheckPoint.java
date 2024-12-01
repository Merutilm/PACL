package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record CheckPoint(Boolean active,
                         Integer tileOffset) implements Actions {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setTileOffset(tileOffset);
    }

    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private Integer tileOffset = 0;

        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setTileOffset(@Nullable Integer tileOffset) {
            this.tileOffset = tileOffset;
            return this;
        }


        @Override
        public CheckPoint build() {
            return new CheckPoint(active, tileOffset);
        }
    }
}

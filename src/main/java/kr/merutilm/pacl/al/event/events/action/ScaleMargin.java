package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record ScaleMargin(Boolean active,
                          Double scale) implements Actions {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setScale(scale);
    }

    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private Double scale = 0d;
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setScale(@Nullable Double scale) {
            this.scale = scale;
            return this;
        }


        @Override
        public ScaleMargin build() {
            return new ScaleMargin(active, scale);
        }
    }
}

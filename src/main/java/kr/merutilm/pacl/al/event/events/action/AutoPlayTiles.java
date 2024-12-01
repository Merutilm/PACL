package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record AutoPlayTiles(Boolean active,
                            Boolean enabled,
                            Boolean safetyTiles) implements Actions {

    @Nonnull
    public Builder edit() {
        return new Builder()
                .setEnabled(enabled)
                .setSafetyTiles(safetyTiles);

    }

    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private Boolean enabled = true;
        private Boolean safetyTiles = false;
        public Builder setEnabled(@Nullable Boolean enabled) {
            this.enabled = enabled;
            return this;
        }
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setSafetyTiles(@Nullable Boolean safetyTiles) {
            this.safetyTiles = safetyTiles;
            return this;
        }

        @Override
        public AutoPlayTiles build() {
            return new AutoPlayTiles(active, enabled, safetyTiles);
        }
    }
}

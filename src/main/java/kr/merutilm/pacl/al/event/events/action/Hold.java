package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.events.IntDurationEvent;
import kr.merutilm.pacl.al.event.events.IntDurationEventBuilder;

public record Hold(Boolean active,
                   Integer duration,
                   Double distanceMultiplier,
                   Boolean landingAnimation) implements Actions, IntDurationEvent {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setDuration(duration)
                .setDistanceMultiplier(distanceMultiplier)
                .setLandingAnimation(landingAnimation);
    }


    public static final class Builder implements ActionsBuilder, IntDurationEventBuilder {
        private Boolean active;
        private Integer duration = 0;
        private Double distanceMultiplier = 100d;
        private Boolean landingAnimation = false;
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setDuration(@Nullable Integer duration) {
            this.duration = duration;
            return this;
        }


        public Builder setDistanceMultiplier(@Nullable Double distanceMultiplier) {
            this.distanceMultiplier = distanceMultiplier;
            return this;
        }


        public Builder setLandingAnimation(@Nullable Boolean landingAnimation) {
            this.landingAnimation = landingAnimation;
            return this;
        }

        @Override
        public Hold build() {
            return new Hold(active, duration, distanceMultiplier, landingAnimation);
        }

    }
}

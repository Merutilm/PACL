package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.selectable.TrackAppearAnimation;
import kr.merutilm.pacl.al.event.selectable.TrackDisappearAnimation;


public record AnimateTrack(Boolean active,
                           TrackAppearAnimation appearAnimation,
                           Double beatsAhead,
                           TrackDisappearAnimation disappearAnimation,
                           Double beatsBehind) implements Actions {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setAppearAnimation(appearAnimation)
                .setBeatsAhead(beatsAhead)
                .setDisappearAnimation(disappearAnimation)
                .setBeatsBehind(beatsBehind);
    }
    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private TrackAppearAnimation appearAnimation = TrackAppearAnimation.NONE;
        private Double beatsAhead = 3d;
        private TrackDisappearAnimation disappearAnimation = TrackDisappearAnimation.NONE;
        private Double beatsBehind = 4d;

        public Builder setAppearAnimation(@Nullable TrackAppearAnimation animation) {
            this.appearAnimation = animation;
            return this;
        }
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setBeatsAhead(@Nullable Double beatsAhead) {
            this.beatsAhead = beatsAhead;
            return this;
        }

        public Builder setDisappearAnimation(@Nullable TrackDisappearAnimation disappearAnimation) {
            this.disappearAnimation = disappearAnimation;
            return this;
        }

        public Builder setBeatsBehind(@Nullable Double beatsBehind) {
            this.beatsBehind = beatsBehind;
            return this;
        }

        @Override
        public AnimateTrack build() {
            return new AnimateTrack(active, appearAnimation, beatsAhead, disappearAnimation, beatsBehind);
        }
    }
}

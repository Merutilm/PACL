package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.events.*;
import kr.merutilm.pacl.al.event.struct.Tag;


public record ShakeScreen(Boolean active,
                          Double duration,
                          Double strength,
                          Double intensity,
                          Boolean fadeOut,
                          Double angleOffset,
                          Tag eventTag) implements Actions, AngleOffsetEvent, DoubleDurationEvent, EventTagEvent {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setDuration(duration)
                .setStrength(strength)
                .setIntensity(intensity)
                .setFadeOut(fadeOut)
                .setAngleOffset(angleOffset)
                .setEventTag(eventTag);
    }

    public static final class Builder implements ActionsBuilder, DoubleDurationEventBuilder, AngleOffsetEventBuilder, EventTagEventBuilder {
        private Boolean active;
        private Double duration = 1d;
        private Double strength = 100d;
        private Double intensity = 100d;
        private Boolean fadeOut = true;
        private Double angleOffset = 0d;
        private Tag eventTag = Tag.of();
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setDuration(@Nullable Double duration) {
            this.duration = duration;
            return this;
        }

        public Builder setStrength(@Nullable Double strength) {
            this.strength = strength;
            return this;
        }

        public Builder setIntensity(@Nullable Double intensity) {
            this.intensity = intensity;
            return this;
        }

        public Builder setFadeOut(@Nullable Boolean fadeOut) {
            this.fadeOut = fadeOut;
            return this;
        }

        public Builder setAngleOffset(@Nullable Double angleOffset) {
            this.angleOffset = angleOffset;
            return this;
        }

        public Builder setEventTag(@Nullable Tag eventTag) {
            this.eventTag = eventTag;
            return this;
        }

        @Override
        public ShakeScreen build() {
            return new ShakeScreen(active, duration, strength, intensity, fadeOut, angleOffset, eventTag);
        }
    }
}

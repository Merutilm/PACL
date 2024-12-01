package kr.merutilm.pacl.al.event.events.action;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.pacl.al.event.events.*;
import kr.merutilm.pacl.al.event.selectable.TargetPlanet;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record ScalePlanets(Boolean active,
                           Double duration,
                           TargetPlanet targetPlanet,
                           Double scale,
                           Double angleOffset,
                           Ease ease,
                           Tag eventTag) implements Actions, EaseEvent, DoubleDurationEvent, AngleOffsetEvent, EventTagEvent {

    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setDuration(duration)
                .setTargetPlanet(targetPlanet)
                .setScale(scale)
                .setAngleOffset(angleOffset)
                .setEase(ease)
                .setEventTag(eventTag);
    }

    public static final class Builder implements ActionsBuilder, EaseEventBuilder, DoubleDurationEventBuilder, AngleOffsetEventBuilder, EventTagEventBuilder {
        private Boolean active;
        private Double duration = 0d;
        private TargetPlanet targetPlanet = TargetPlanet.FIRE;
        private Double scale = 100.0;
        private Double angleOffset = 0d;
        private Ease ease = Ease.LINEAR;
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

        public Builder setTargetPlanet(@Nullable TargetPlanet targetPlanet) {
            this.targetPlanet = targetPlanet;
            return this;
        }

        public Builder setScale(@Nullable Double scale) {
            this.scale = scale;
            return this;
        }


        public Builder setAngleOffset(@Nullable Double angleOffset) {
            this.angleOffset = angleOffset;
            return this;
        }

        public Builder setEase(@Nullable Ease ease) {
            this.ease = ease;
            return this;
        }

        public Builder setEventTag(@Nullable Tag eventTag) {
            this.eventTag = eventTag;
            return this;
        }

        @Override
        public ScalePlanets build() {
            return new ScalePlanets(active, duration, targetPlanet, scale, angleOffset, ease, eventTag);
        }
    }
}

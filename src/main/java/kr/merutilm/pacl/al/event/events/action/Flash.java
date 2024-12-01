package kr.merutilm.pacl.al.event.events.action;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.pacl.al.event.events.*;
import kr.merutilm.pacl.al.event.selectable.Plane;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record Flash(Boolean active,
                    Double duration,
                    Plane plane,
                    HexColor startColor,
                    Double startOpacity,
                    HexColor endColor,
                    Double endOpacity,
                    Double angleOffset,
                    Ease ease,
                    Tag eventTag) implements Actions, EaseEvent, DoubleDurationEvent, AngleOffsetEvent, EventTagEvent {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setDuration(duration)
                .setPlane(plane)
                .setStartColor(startColor)
                .setStartOpacity(startOpacity)
                .setEndColor(endColor)
                .setEndOpacity(endOpacity)
                .setAngleOffset(angleOffset)
                .setEase(ease)
                .setEventTag(eventTag);
    }

    public static final class Builder implements ActionsBuilder, EaseEventBuilder, DoubleDurationEventBuilder, AngleOffsetEventBuilder, EventTagEventBuilder {
        private Boolean active;
        private Double duration = 1d;
        private Plane plane = Plane.BACKGROUND;
        private HexColor startColor = HexColor.WHITE;
        private Double startOpacity = 100d;
        private HexColor endColor = HexColor.WHITE;
        private Double endOpacity = 0d;
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

        public Builder setPlane(@Nullable Plane plane) {
            this.plane = plane;
            return this;
        }


        public Builder setStartColor(@Nullable HexColor startColor) {
            this.startColor = startColor;
            return this;
        }


        public Builder setStartOpacity(@Nullable Double startOpacity) {
            this.startOpacity = startOpacity;
            return this;
        }


        public Builder setEndColor(@Nullable HexColor endColor) {
            this.endColor = endColor;
            return this;
        }


        public Builder setEndOpacity(@Nullable Double endOpacity) {
            this.endOpacity = endOpacity;
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
        public Flash build() {
            return new Flash(active, duration, plane, startColor, startOpacity, endColor, endOpacity, angleOffset, ease, eventTag);
        }
    }
}

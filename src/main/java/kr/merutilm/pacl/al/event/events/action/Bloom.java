package kr.merutilm.pacl.al.event.events.action;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.pacl.al.event.events.*;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record Bloom(Boolean active,
                    Boolean enabled,
                    Double threshold,
                    Double intensity,
                    HexColor color,
                    Double duration,
                    Ease ease,
                    Double angleOffset,
                    Tag eventTag) implements Actions, EaseEvent, DoubleDurationEvent, AngleOffsetEvent, EventTagEvent {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setEnabled(enabled)
                .setThreshold(threshold)
                .setIntensity(intensity)
                .setColor(color)
                .setDuration(duration)
                .setEase(ease)
                .setAngleOffset(angleOffset)
                .setEventTag(eventTag);
    }

    public static final class Builder implements ActionsBuilder, EaseEventBuilder, DoubleDurationEventBuilder, AngleOffsetEventBuilder, EventTagEventBuilder {
        private Boolean active;
        private Boolean enabled = true;
        private Double threshold = 50d;
        private Double intensity = 100d;
        private HexColor color = HexColor.WHITE;
        private Double duration = 0d;
        private Ease ease = Ease.LINEAR;
        private Double angleOffset = 0d;
        private Tag eventTag = Tag.of();
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setEnabled(@Nullable Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder setThreshold(@Nullable Double threshold) {
            this.threshold = threshold;
            return this;
        }

        public Builder setIntensity(@Nullable Double intensity) {
            this.intensity = intensity;
            return this;
        }

        public Builder setColor(@Nullable HexColor color) {
            this.color = color;
            return this;
        }

        public Builder setDuration(@Nullable Double duration) {
            this.duration = duration;
            return this;
        }

        public Builder setEase(@Nullable Ease ease) {
            this.ease = ease;
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
        public Bloom build() {
            return new Bloom(active, enabled, threshold, intensity, color, duration, ease, angleOffset, eventTag);
        }
    }
}

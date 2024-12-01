package kr.merutilm.pacl.al.event.events.action;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.pacl.al.event.events.*;
import kr.merutilm.pacl.al.event.selectable.Filter;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record SetFilter(Boolean active,
                        Boolean enabled,
                        Filter filter,
                        Double intensity,
                        Double duration,
                        Ease ease,
                        Boolean disableOthers,
                        Double angleOffset,
                        Tag eventTag) implements Actions, EaseEvent, DoubleDurationEvent, AngleOffsetEvent, EventTagEvent {

    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setEnabled(enabled)
                .setFilter(filter)
                .setIntensity(intensity)
                .setDisableOthers(disableOthers)
                .setDuration(duration)
                .setEase(ease)
                .setAngleOffset(angleOffset)
                .setEventTag(eventTag);
    }

    public static final class Builder implements ActionsBuilder, EaseEventBuilder, DoubleDurationEventBuilder, AngleOffsetEventBuilder, EventTagEventBuilder {
        private Boolean active;
        private Boolean enabled = true;
        private Filter filter = Filter.GRAYSCALE;
        private Double intensity = 100d;
        private Double duration = 0d;
        private Ease ease = Ease.LINEAR;
        private Boolean disableOthers = false;
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

        public Builder setFilter(@Nullable Filter filter) {
            this.filter = filter;
            return this;
        }

        public Builder setIntensity(@Nullable Double intensity) {
            this.intensity = intensity;
            return this;
        }

        public Builder setDisableOthers(@Nullable Boolean disableOthers) {
            this.disableOthers = disableOthers;
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
        public SetFilter build() {
            return new SetFilter(active, enabled, filter, intensity, duration, ease, disableOthers, angleOffset, eventTag);
        }
    }
}

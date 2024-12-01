package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.events.AngleOffsetEvent;
import kr.merutilm.pacl.al.event.events.AngleOffsetEventBuilder;
import kr.merutilm.pacl.al.event.events.EventTagEvent;
import kr.merutilm.pacl.al.event.events.EventTagEventBuilder;
import kr.merutilm.pacl.al.event.struct.Tag;


public record HallOfMirrors(Boolean active,
                            Boolean enabled,
                            Double angleOffset,
                            Tag eventTag) implements Actions, AngleOffsetEvent, EventTagEvent {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setEnabled(enabled)
                .setAngleOffset(angleOffset)
                .setEventTag(eventTag);
    }

    public static final class Builder implements ActionsBuilder, AngleOffsetEventBuilder, EventTagEventBuilder {
        private Boolean active;
        private Boolean enabled = true;
        private Double angleOffset = 0d;
        private Tag eventTag = Tag.of();

        public Builder setEnabled(@Nullable Boolean enabled) {
            this.enabled = enabled;
            return this;
        }
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
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
        public HallOfMirrors build() {
            return new HallOfMirrors(active, enabled, angleOffset, eventTag);
        }
    }
}

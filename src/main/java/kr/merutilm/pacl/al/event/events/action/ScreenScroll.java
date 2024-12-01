package kr.merutilm.pacl.al.event.events.action;

import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.event.events.AngleOffsetEvent;
import kr.merutilm.pacl.al.event.events.AngleOffsetEventBuilder;
import kr.merutilm.pacl.al.event.events.EventTagEvent;
import kr.merutilm.pacl.al.event.events.EventTagEventBuilder;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record ScreenScroll(Boolean active,
                           Point2D scroll,
                           Double angleOffset,
                           Tag eventTag) implements Actions, AngleOffsetEvent, EventTagEvent {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setScroll(scroll)
                .setAngleOffset(angleOffset)
                .setEventTag(eventTag);
    }
    public static final class Builder implements ActionsBuilder, AngleOffsetEventBuilder, EventTagEventBuilder {
        private Boolean active;
        private Point2D scroll = new Point2D(0, 0);
        private Double angleOffset = 0d;
        private Tag eventTag = Tag.of();
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setScroll(@Nullable Point2D scroll) {
            this.scroll = scroll;
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
        public ScreenScroll build() {
            return new ScreenScroll(active, scroll, angleOffset, eventTag);
        }
    }
}

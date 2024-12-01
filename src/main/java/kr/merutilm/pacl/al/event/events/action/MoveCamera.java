package kr.merutilm.pacl.al.event.events.action;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.event.events.*;
import kr.merutilm.pacl.al.event.selectable.RelativeTo;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record MoveCamera(Boolean active,
                         Double duration,
                         RelativeTo.Camera relativeTo,
                         Point2D position,
                         Double rotation,
                         Double zoom,
                         Double angleOffset,
                         Ease ease,
                         Boolean doNotDisable,
                         Boolean minVfxOnly,
                         Tag eventTag) implements Actions, EaseEvent, DoubleDurationEvent, AngleOffsetEvent, EventTagEvent {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setDuration(duration)
                .setRelativeTo(relativeTo)
                .setPosition(position)
                .setRotation(rotation)
                .setZoom(zoom)
                .setAngleOffset(angleOffset)
                .setEase(ease)
                .setDoNotDisable(doNotDisable)
                .setMinVfxOnly(minVfxOnly)
                .setEventTag(eventTag);
    }

    public static final class Builder implements ActionsBuilder, EaseEventBuilder, DoubleDurationEventBuilder, AngleOffsetEventBuilder, EventTagEventBuilder {
        private Boolean active;
        private Double duration = 1d;
        private RelativeTo.Camera relativeTo = RelativeTo.Camera.PLAYER;
        private Point2D position = new Point2D(0.0, 0.0);
        private Double rotation;
        private Double zoom;
        private Double angleOffset = 0d;
        private Ease ease = Ease.LINEAR;
        private Boolean doNotDisable = false;
        private Boolean minVfxOnly = false;
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

        public Builder setRelativeTo(@Nullable RelativeTo.Camera relativeTo) {
            this.relativeTo = relativeTo;
            return this;
        }


        public Builder setPosition(@Nullable Point2D position) {
            this.position = position;
            return this;
        }


        public Builder setRotation(@Nullable Double rotation) {
            this.rotation = rotation;
            return this;
        }


        public Builder setZoom(@Nullable Double zoom) {
            this.zoom = zoom;
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

        public Builder setDoNotDisable(@Nullable Boolean doNotDisable) {
            this.doNotDisable = doNotDisable;
            return this;
        }

        public Builder setMinVfxOnly(@Nullable Boolean minVfxOnly) {
            this.minVfxOnly = minVfxOnly;
            return this;
        }


        public Builder setEventTag(@Nullable Tag eventTag) {
            this.eventTag = eventTag;
            return this;
        }

        @Override
        public MoveCamera build() {
            return new MoveCamera(active, duration, relativeTo, position, rotation, zoom, angleOffset, ease, doNotDisable, minVfxOnly, eventTag);
        }
    }
}

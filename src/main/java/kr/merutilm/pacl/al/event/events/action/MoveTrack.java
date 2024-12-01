package kr.merutilm.pacl.al.event.events.action;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.event.events.*;
import kr.merutilm.pacl.al.event.selectable.RelativeTo;
import kr.merutilm.pacl.al.event.struct.RelativeTile;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record MoveTrack(Boolean active,
                        RelativeTile startTile,
                        RelativeTile endTile,
                        Integer gap,
                        Double duration,
                        Point2D positionOffset,
                        Double rotationOffset,
                        Point2D scale,
                        Double opacity,
                        Double angleOffset,
                        Ease ease,
                        Boolean maxVfxOnly,
                        Tag eventTag) implements Actions, EaseEvent, DoubleDurationEvent, AngleOffsetEvent, EventTagEvent, RelativeTileEvent {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setStartTile(startTile)
                .setEndTile(endTile)
                .setGap(gap)
                .setDuration(duration)
                .setPositionOffset(positionOffset)
                .setRotationOffset(rotationOffset)
                .setScale(scale)
                .setOpacity(opacity)
                .setAngleOffset(angleOffset)
                .setEase(ease)
                .setMaxVfxOnly(maxVfxOnly)
                .setEventTag(eventTag);
    }


    public static final class Builder implements ActionsBuilder, EaseEventBuilder, DoubleDurationEventBuilder, AngleOffsetEventBuilder, EventTagEventBuilder, RelativeTileEventBuilder {
        private Boolean active;
        private RelativeTile startTile = new RelativeTile(0, RelativeTo.Tile.THIS_TILE);
        private RelativeTile endTile = new RelativeTile(0, RelativeTo.Tile.THIS_TILE);
        private Integer gap = 0;
        private Double duration = 1d;
        private Point2D positionOffset = new Point2D(0.0, 0.0);
        private Double rotationOffset;
        private Point2D scale;
        private Double opacity;
        private Double angleOffset = 0d;
        private Ease ease = Ease.LINEAR;
        private Boolean maxVfxOnly = false;
        private Tag eventTag = Tag.of();
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setStartTile(@Nullable RelativeTile startTile) {
            this.startTile = startTile;
            return this;
        }


        public Builder setEndTile(@Nullable RelativeTile endTile) {
            this.endTile = endTile;
            return this;
        }


        public Builder setGap(@Nullable Integer gap) {
            this.gap = gap;
            return this;
        }


        public Builder setDuration(@Nullable Double duration) {
            this.duration = duration;
            return this;
        }


        public Builder setPositionOffset(@Nullable Point2D positionOffset) {
            this.positionOffset = positionOffset;
            return this;
        }


        public Builder setRotationOffset(@Nullable Double rotationOffset) {
            this.rotationOffset = rotationOffset;
            return this;
        }


        public Builder setScale(@Nullable Point2D scale) {
            this.scale = scale;
            return this;
        }


        public Builder setOpacity(@Nullable Double opacity) {
            this.opacity = opacity;
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


        public Builder setMaxVfxOnly(@Nullable Boolean maxVfxOnly) {
            this.maxVfxOnly = maxVfxOnly;
            return this;
        }


        public Builder setEventTag(@Nullable Tag eventTag) {
            this.eventTag = eventTag;
            return this;
        }

        @Override
        public MoveTrack build() {
            return new MoveTrack(active, startTile, endTile, gap, duration, positionOffset, rotationOffset, scale, opacity, angleOffset, ease, maxVfxOnly, eventTag);
        }
    }
}

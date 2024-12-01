package kr.merutilm.pacl.al.event.events.action;

import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.event.selectable.RelativeTo;
import kr.merutilm.pacl.al.event.struct.RelativeTile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record PositionTrack(Boolean active,
                            Point2D positionOffset,
                            RelativeTile relativeTo,
                            Double rotation,
                            Double scale,
                            Double opacity,
                            Boolean justThisTile,
                            Boolean editorOnly,
                            Boolean stickToFloors) implements Actions {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setPositionOffset(positionOffset)
                .setRelativeTo(relativeTo)
                .setRotation(rotation)
                .setScale(scale)
                .setOpacity(opacity)
                .setJustThisTile(justThisTile)
                .setEditorOnly(editorOnly)
                .setStickToFloors(stickToFloors);
    }

    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private Point2D positionOffset = new Point2D(0.0, 0.0);
        private RelativeTile relativeTo = new RelativeTile(0, RelativeTo.Tile.THIS_TILE);
        private Double rotation = 0d;
        private Double scale = 100d;
        private Double opacity = 100d;
        private Boolean justThisTile = false;
        private Boolean editorOnly = false;
        private Boolean stickToFloors;

        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setPositionOffset(@Nullable Point2D positionOffset) {
            this.positionOffset = positionOffset;
            return this;
        }

        public Builder setRelativeTo(@Nullable RelativeTile relativeTo) {
            this.relativeTo = relativeTo;
            return this;
        }


        public Builder setRotation(@Nullable Double rotation) {
            this.rotation = rotation;
            return this;
        }


        public Builder setScale(@Nullable Double scale) {
            this.scale = scale;
            return this;
        }


        public Builder setOpacity(@Nullable Double opacity) {
            this.opacity = opacity;
            return this;
        }

        public Builder setJustThisTile(@Nullable Boolean justThisTile) {
            this.justThisTile = justThisTile;
            return this;
        }


        public Builder setEditorOnly(@Nullable Boolean editorOnly) {
            this.editorOnly = editorOnly;
            return this;
        }

        public Builder setStickToFloors(Boolean stickToFloors) {
            this.stickToFloors = stickToFloors;
            return this;
        }

        @Override
        public PositionTrack build() {
            return new PositionTrack(active, positionOffset, relativeTo, rotation, scale, opacity, justThisTile, editorOnly, stickToFloors);
        }
    }
}

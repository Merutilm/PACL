package kr.merutilm.pacl.al.event.events.decoration;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.event.selectable.BlendMode;
import kr.merutilm.pacl.al.event.selectable.FailHitBox;
import kr.merutilm.pacl.al.event.selectable.MaskingType;
import kr.merutilm.pacl.al.event.selectable.RelativeTo;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record AddDecoration(Boolean visible,
                            Boolean locked,
                            ImageFile image,
                            Point2D position,
                            RelativeTo.AddDecoration relativeTo,
                            Point2D pivotOffset,
                            Double rotation,
                            Boolean lockRotation,
                            Point2D scale,
                            Boolean lockScale,
                            Point2D tile,
                            HexColor color,
                            Double opacity,
                            Short depth,
                            Point2D parallax,
                            Point2D parallaxOffset,
                            Tag tag,
                            Boolean smoothing,
                            BlendMode blendMode,
                            MaskingType maskingType,
                            Boolean useMaskingDepth,
                            Short maskingFrontDepth,
                            Short maskingBackDepth,
                            Boolean failBox,
                            FailHitBox failType,
                            Point2D failScale,
                            Point2D failPosition,
                            Double failRotation
) implements Decorations {

    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setVisible(visible)
                .setLocked(locked)
                .setDecorationImage(image)
                .setPosition(position)
                .setRelativeTo(relativeTo)
                .setPivotOffset(pivotOffset)
                .setRotation(rotation)
                .setLockRotation(lockRotation)
                .setScale(scale)
                .setLockScale(lockScale)
                .setTile(tile)
                .setColor(color)
                .setOpacity(opacity)
                .setDepth(depth)
                .setParallax(parallax)
                .setParallaxOffset(parallaxOffset)
                .setTag(tag)
                .setSmoothing(smoothing)
                .setBlendMode(blendMode)
                .setMaskingType(maskingType)
                .setUseMaskingDepth(useMaskingDepth)
                .setMaskingFrontDepth(maskingFrontDepth)
                .setMaskingBackDepth(maskingBackDepth)
                .setFailBox(failBox)
                .setFailType(failType)
                .setFailScale(failScale)
                .setFailPosition(failPosition)
                .setFailRotation(failRotation);
    }

    public static final class Builder implements DecorationsBuilder {
        private Boolean visible = true;
        private Boolean locked = true;
        private ImageFile imageFile = new ImageFile("");
        private Point2D position = new Point2D(0.0, 0.0);
        private RelativeTo.AddDecoration relativeTo = RelativeTo.AddDecoration.TILE;
        private Point2D pivotOffset = new Point2D(0.0, 0.0);
        private Double rotation = 0d;
        private Boolean lockRotation = false;
        private Point2D scale = new Point2D(100.0, 100.0);
        private Boolean lockScale = false;
        private Point2D tile = new Point2D(1.0, 1.0);
        private HexColor color = HexColor.WHITE;
        private Double opacity = 100d;
        private Short depth = -1;
        private Point2D parallax = new Point2D(0.0, 0.0);
        private Point2D parallaxOffset = new Point2D(0.0, 0.0);
        private Tag tag = Tag.of();
        private Boolean smoothing = true;
        private BlendMode blendMode = BlendMode.NONE;
        private MaskingType maskingType = MaskingType.NONE;
        private Boolean useMaskingDepth = false;
        private Short maskingFrontDepth = -1;
        private Short maskingBackDepth = -1;
        private Boolean failBox = false;
        private FailHitBox failType = FailHitBox.BOX;
        private Point2D failScale = new Point2D(100.0, 100.0);
        private Point2D failPosition = new Point2D(0.0, 0.0);
        private Double failRotation = 0d;

        public Builder setVisible(Boolean visible) {
            this.visible = visible;
            return this;
        }

        @Override
        public Builder setLocked(Boolean locked) {
            this.locked = locked;
            return this;
        }

        public Builder setDecorationImage(@Nullable ImageFile imageFile) {
            this.imageFile = imageFile;
            return this;
        }

        public Builder setPosition(@Nullable Point2D position) {
            this.position = position;
            return this;
        }

        @Override
        public Builder setRelativeTo(@Nullable RelativeTo.AddDecoration decoRelativeTo) {
            this.relativeTo = decoRelativeTo;
            return this;
        }

        public Builder setPivotOffset(@Nullable Point2D pivotOffset) {
            this.pivotOffset = pivotOffset;
            return this;
        }

        public Builder setRotation(@Nullable Double rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder setLockRotation(Boolean lockRotation) {
            this.lockRotation = lockRotation;
            return this;
        }

        public Builder setScale(@Nullable Point2D scale) {
            this.scale = scale;
            return this;
        }

        public Builder setLockScale(Boolean lockScale) {
            this.lockScale = lockScale;
            return this;
        }

        public Builder setTile(@Nullable Point2D tile) {
            this.tile = tile;
            return this;
        }

        public Builder setColor(@Nullable HexColor color) {
            this.color = color;
            return this;
        }

        public Builder setOpacity(@Nullable Double opacity) {
            this.opacity = opacity;
            return this;
        }

        public Builder setDepth(@Nullable Short depth) {
            this.depth = depth;
            return this;
        }

        public Builder setParallax(@Nullable Point2D parallax) {
            this.parallax = parallax;
            return this;
        }

        public Builder setParallaxOffset(Point2D parallaxOffset) {
            this.parallaxOffset = parallaxOffset;
            return this;
        }

        @Override
        public Builder setTag(@Nullable Tag tag) {
            this.tag = tag;
            return this;
        }

        public Builder setSmoothing(@Nullable Boolean smoothing) {
            this.smoothing = smoothing;
            return this;
        }

        public Builder setBlendMode(BlendMode blendMode) {
            this.blendMode = blendMode;
            return this;
        }

        public Builder setMaskingType(MaskingType maskingType) {
            this.maskingType = maskingType;
            return this;
        }

        public Builder setUseMaskingDepth(Boolean useMaskingDepth) {
            this.useMaskingDepth = useMaskingDepth;
            return this;
        }

        public Builder setMaskingFrontDepth(Short maskingFrontDepth) {
            this.maskingFrontDepth = maskingFrontDepth;
            return this;
        }

        public Builder setMaskingBackDepth(Short maskingBackDepth) {
            this.maskingBackDepth = maskingBackDepth;
            return this;
        }

        public Builder setFailBox(@Nullable Boolean failBox) {
            this.failBox = failBox;
            return this;
        }

        public Builder setFailType(@Nullable FailHitBox failType) {
            this.failType = failType;
            return this;
        }

        public Builder setFailScale(@Nullable Point2D failScale) {
            this.failScale = failScale;
            return this;
        }

        public Builder setFailPosition(@Nullable Point2D failPosition) {
            this.failPosition = failPosition;
            return this;
        }

        public Builder setFailRotation(@Nullable Double failRotation) {
            this.failRotation = failRotation;
            return this;
        }


        @Override
        public AddDecoration build() {
            return new AddDecoration(visible, locked, imageFile, position, relativeTo, pivotOffset, rotation, lockRotation, scale, lockScale, tile, color, opacity, depth, parallax, parallaxOffset, tag, smoothing, blendMode, maskingType, useMaskingDepth, maskingFrontDepth, maskingBackDepth, failBox, failType, failScale, failPosition, failRotation);
        }
    }

}

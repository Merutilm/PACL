package kr.merutilm.pacl.al.event.events.action;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.event.events.*;
import kr.merutilm.pacl.al.event.selectable.MaskingType;
import kr.merutilm.pacl.al.event.selectable.RelativeTo;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record MoveDecorations(Boolean active,
                              Double duration,
                              Tag tag,
                              Boolean visible,
                              RelativeTo.MoveDecoration relativeTo,
                              ImageFile image,
                              Point2D positionOffset,
                              Point2D pivotOffset,
                              Double rotationOffset,
                              Point2D scale,
                              HexColor color,
                              Double opacity,
                              Short depth,
                              Point2D parallax,
                              Point2D parallaxOffset,
                              Double angleOffset,
                              Ease ease,
                              Tag eventTag,
                              MaskingType maskingType,
                              Boolean useMaskingDepth,
                              Short maskingFrontDepth,
                              Short maskingBackDepth) implements Actions, EaseEvent, DoubleDurationEvent, AngleOffsetEvent, EventTagEvent {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setDuration(duration)
                .setTag(tag)
                .setVisible(visible)
                .setRelativeTo(relativeTo)
                .setImage(image)
                .setPositionOffset(positionOffset)
                .setPivotOffset(pivotOffset)
                .setRotationOffset(rotationOffset)
                .setScale(scale)
                .setColor(color)
                .setOpacity(opacity)
                .setDepth(depth)
                .setParallax(parallax)
                .setParallaxOffset(parallaxOffset)
                .setAngleOffset(angleOffset)
                .setEase(ease)
                .setEventTag(eventTag)
                .setMaskingType(maskingType)
                .setUseMaskingDepth(useMaskingDepth)
                .setMaskingFrontDepth(maskingFrontDepth)
                .setMaskingBackDepth(maskingBackDepth);
    }

    public static final class Builder implements ActionsBuilder, EaseEventBuilder, DoubleDurationEventBuilder, AngleOffsetEventBuilder, EventTagEventBuilder {
        private Boolean active;
        private Double duration = 0d;
        private Tag tag = Tag.of("sampleTag");
        private Boolean visible;
        private RelativeTo.MoveDecoration relativeTo;
        private ImageFile image;
        private Point2D positionOffset;
        private Point2D pivotOffset;
        private Double rotationOffset;
        private Point2D scale;
        private HexColor color;
        private Double opacity;
        private Short depth;
        private Point2D parallax;
        private Point2D parallaxOffset;
        private Double angleOffset = 0d;
        private Ease ease = Ease.LINEAR;
        private Tag eventTag = Tag.of();
        private MaskingType maskingType;
        private Boolean useMaskingDepth;
        private Short maskingFrontDepth;
        private Short maskingBackDepth;
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setDuration(@Nullable Double duration) {
            this.duration = duration;
            return this;
        }


        public Builder setTag(@Nullable Tag tag) {
            this.tag = tag;
            return this;
        }

        public Builder setVisible(Boolean visible) {
            this.visible = visible;
            return this;
        }

        public Builder setRelativeTo(RelativeTo.MoveDecoration relativeTo) {
            this.relativeTo = relativeTo;
            return this;
        }

        public Builder setImage(@Nullable ImageFile imageFile) {
            this.image = imageFile;
            return this;
        }


        public Builder setPositionOffset(@Nullable Point2D positionOffset) {
            this.positionOffset = positionOffset;
            return this;
        }

        public Builder setPivotOffset(Point2D pivotOffset) {
            this.pivotOffset = pivotOffset;
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

        @Override
        public MoveDecorations build() {
            return new MoveDecorations(active, duration, tag, visible, relativeTo, image, positionOffset, pivotOffset, rotationOffset, scale, color, opacity, depth, parallax, parallaxOffset, angleOffset, ease, eventTag, maskingType, useMaskingDepth, maskingFrontDepth, maskingBackDepth);
        }
    }

}

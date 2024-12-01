package kr.merutilm.pacl.al.event.events.action;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.event.selectable.BGDisplayMode;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record CustomBackground(Boolean active,
                               HexColor backgroundColor,
                               ImageFile image,
                               HexColor imageColor,
                               Point2D parallax,
                               BGDisplayMode bgDisplayMode,
                               Boolean imageSmoothing,
                               Boolean lockRot,
                               Boolean loop,
                               Integer scalingRatio,
                               Double angleOffset,
                               Tag eventTag) implements Actions {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setBackgroundColor(backgroundColor)
                .setImage(image)
                .setImageColor(imageColor)
                .setParallax(parallax)
                .setBgDisplayMode(bgDisplayMode)
                .setImageSmoothing(imageSmoothing)
                .setLockRot(lockRot)
                .setLoop(loop)
                .setScalingRatio(scalingRatio)
                .setAngleOffset(angleOffset)
                .setEventTag(eventTag);
    }

    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private HexColor backgroundColor = HexColor.BLACK;
        private ImageFile image = new ImageFile("");
        private HexColor imageColor = HexColor.WHITE;
        private Point2D parallax = new Point2D(100, 100);
        private BGDisplayMode bgDisplayMode = BGDisplayMode.FIT_TO_SCREEN;
        private Boolean imageSmoothing = true;
        private Boolean lockRot = false;
        private Boolean loop = false;
        private Integer scalingRatio = 100;
        private Double angleOffset = 0d;
        private Tag eventTag = Tag.of();

        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setBackgroundColor(HexColor backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder setImage(ImageFile image) {
            this.image = image;
            return this;
        }

        public Builder setImageColor(HexColor imageColor) {
            this.imageColor = imageColor;
            return this;
        }

        public Builder setParallax(Point2D parallax) {
            this.parallax = parallax;
            return this;
        }

        public Builder setBgDisplayMode(BGDisplayMode bgDisplayMode) {
            this.bgDisplayMode = bgDisplayMode;
            return this;
        }

        public Builder setImageSmoothing(Boolean imageSmoothing) {
            this.imageSmoothing = imageSmoothing;
            return this;
        }

        public Builder setLockRot(Boolean lockRot) {
            this.lockRot = lockRot;
            return this;
        }

        public Builder setLoop(Boolean loop) {
            this.loop = loop;
            return this;
        }

        public Builder setScalingRatio(Integer scalingRatio) {
            this.scalingRatio = scalingRatio;
            return this;
        }

        public Builder setAngleOffset(Double angleOffset) {
            this.angleOffset = angleOffset;
            return this;
        }

        public Builder setEventTag(Tag eventTag) {
            this.eventTag = eventTag;
            return this;
        }

        public CustomBackground build() {
            return new CustomBackground(active, backgroundColor, image, imageColor, parallax, bgDisplayMode, imageSmoothing, lockRot, loop, scalingRatio, angleOffset, eventTag);
        }
    }
}

package kr.merutilm.pacl.data;

import kr.merutilm.base.struct.*;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;
import kr.merutilm.pacl.al.event.selectable.MaskingType;
import kr.merutilm.pacl.al.event.selectable.RelativeTo;

import java.util.Optional;

/**
 * 프레임 장식
 */
public record VFXDecoration(int floor,
                     AddDecoration target,
                     boolean visible,
                     RelativeTo.MoveDecoration relativeTo,
                     ImageFile image,
                     Point2D positionOffset,
                     Point2D pivotOffset,
                     Point2D parallaxOffset,
                     double rotationOffset,
                     Point2D scale,
                     HexColor color,
                     double opacity,
                     short depth,
                     Point2D parallax,
                     MaskingType maskingType,
                     boolean useMaskingDepth,
                     IntRange maskingDepthRange) implements State, Struct<VFXDecoration> {

    @Override
    public Builder edit() {
        return new Builder(floor, target, visible, relativeTo, image, positionOffset, pivotOffset, parallaxOffset, rotationOffset, scale, color, opacity, depth, parallax, maskingType, useMaskingDepth, maskingDepthRange);
    }

    static final class Builder implements StructBuilder<VFXDecoration> {
        private int floor;
        private AddDecoration target;
        private boolean visible;
        private RelativeTo.MoveDecoration relativeTo;
        private ImageFile image;
        private Point2D positionOffset;
        private Point2D pivotOffset;
        private Point2D parallaxOffset;
        private double rotationOffset;
        private Point2D scale;
        private HexColor color;
        private double opacity;
        private short depth;
        private Point2D parallax;
        private MaskingType maskingType;
        private boolean useMaskingDepth;
        private IntRange maskingDepthRange;

        Builder(int floor, AddDecoration target, boolean visible, RelativeTo.MoveDecoration relativeTo, ImageFile image, Point2D positionOffset, Point2D pivotOffset, Point2D parallaxOffset, double rotationOffset, Point2D scale, HexColor color, double opacity, short depth, Point2D parallax, MaskingType maskingType, boolean useMaskingDepth, IntRange maskingDepthRange) {
            this.floor = floor;
            this.target = target;
            this.visible = visible;
            this.relativeTo = relativeTo;
            this.image = image;
            this.positionOffset = positionOffset;
            this.pivotOffset = pivotOffset;
            this.parallaxOffset = parallaxOffset;
            this.rotationOffset = rotationOffset;
            this.scale = scale;
            this.color = color;
            this.opacity = opacity;
            this.depth = depth;
            this.parallax = parallax;
            this.maskingType = maskingType;
            this.useMaskingDepth = useMaskingDepth;
            this.maskingDepthRange = maskingDepthRange;
        }

        public Builder setFloor(int floor) {
            this.floor = floor;
            return this;
        }

        public Builder setTarget(AddDecoration target) {
            this.target = target;
            return this;
        }

        public Builder setVisible(boolean visible) {
            this.visible = visible;
            return this;
        }

        public Builder setRelativeTo(RelativeTo.MoveDecoration relativeTo) {
            this.relativeTo = relativeTo;
            return this;
        }

        public Builder setImage(ImageFile image) {
            this.image = image;
            return this;
        }

        public Builder setPositionOffset(Point2D positionOffset) {
            this.positionOffset = positionOffset;
            return this;
        }

        public Builder setPivotOffset(Point2D pivotOffset) {
            this.pivotOffset = pivotOffset;
            return this;
        }

        public Builder setParallaxOffset(Point2D parallaxOffset) {
            this.parallaxOffset = parallaxOffset;
            return this;
        }

        public Builder setRotationOffset(double rotationOffset) {
            this.rotationOffset = rotationOffset;
            return this;
        }

        public Builder setScale(Point2D scale) {
            this.scale = scale;
            return this;
        }

        public Builder setColor(HexColor color) {
            this.color = color;
            return this;
        }

        public Builder setOpacity(double opacity) {
            this.opacity = opacity;
            return this;
        }

        public Builder setDepth(short depth) {
            this.depth = depth;
            return this;
        }

        public Builder setParallax(Point2D parallax) {
            this.parallax = parallax;
            return this;
        }

        public Builder setMaskingType(MaskingType maskingType) {
            this.maskingType = maskingType;
            return this;
        }

        public Builder setUseMaskingDepth(boolean useMaskingDepth) {
            this.useMaskingDepth = useMaskingDepth;
            return this;
        }

        public Builder setMaskingDepthRange(IntRange maskingDepthRange) {
            this.maskingDepthRange = maskingDepthRange;
            return this;
        }

        @Override
        public VFXDecoration build() {
            return new VFXDecoration(floor, target, visible, relativeTo, image, positionOffset, pivotOffset, parallaxOffset, rotationOffset, scale, color, opacity, depth, parallax, maskingType, useMaskingDepth, maskingDepthRange);
        }
    }

    public boolean isVisible() {
        return this.visible && this.opacity() > 0 && this.scale().x() != 0 && this.scale().y() != 0 && this.color().a() > 0;
    }



    public static VFXDecoration fromAddDecoration(EIN.EventData e) {

        AddDecoration d = (AddDecoration) e.event();

        int minDepth = Math.min(d.maskingFrontDepth(), d.maskingBackDepth());
        int maxDepth = Math.max(d.maskingFrontDepth(), d.maskingBackDepth());

        return new VFXDecoration(
                e.eventID().floor(),
                d,
                Optional.ofNullable(d.visible()).orElse(true),
                RelativeTo.MoveDecoration.typeOf(d.relativeTo().toString()),
                d.image(),
                d.position(),
                d.pivotOffset(),
                d.parallaxOffset(),
                d.rotation(),
                d.scale(),
                d.color(),
                d.opacity(),
                d.depth(),
                d.parallax(),
                d.maskingType(),
                d.useMaskingDepth(),
                new IntRange(minDepth, maxDepth)
        );
    }


}

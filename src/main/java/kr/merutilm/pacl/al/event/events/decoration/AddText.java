package kr.merutilm.pacl.al.event.events.decoration;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.event.selectable.Font;
import kr.merutilm.pacl.al.event.selectable.RelativeTo;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record AddText(Boolean visible,
                      Boolean locked,
                      String decText,
                      Font font,
                      Point2D position,
                      RelativeTo.AddDecoration relativeTo,
                      Point2D pivotOffset,
                      Double rotation,
                      Boolean lockScale,
                      Point2D scale,
                      Boolean lockRotation,
                      HexColor color,
                      Double opacity,
                      Short depth,
                      Point2D parallax,
                      Point2D parallaxOffset,
                      Tag tag
) implements Decorations {

    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setVisible(visible)
                .setLocked(locked)
                .setDecText(decText)
                .setFont(font)
                .setPosition(position)
                .setRelativeTo(relativeTo)
                .setPivotOffset(pivotOffset)
                .setRotation(rotation)
                .setScale(scale)
                .setColor(color)
                .setOpacity(opacity)
                .setDepth(depth)
                .setParallax(parallax)
                .setParallaxOffset(parallaxOffset)
                .setTag(tag);
    }


    public static final class Builder implements DecorationsBuilder {
        private Boolean visible = true;
        private Boolean locked = true;
        private String decText = "Text";
        private Font font;
        private Point2D position = new Point2D(0.0, 0.0);
        private RelativeTo.AddDecoration relativeTo = RelativeTo.AddDecoration.TILE;
        private Point2D pivotOffset = new Point2D(0.0, 0.0);
        private Double rotation = 0d;
        private Boolean lockScale = false;
        private Point2D scale = new Point2D(100.0, 100.0);
        private Boolean lockRotation = false;
        private HexColor color = HexColor.WHITE;
        private Double opacity = 100d;
        private Short depth = 0;
        private Point2D parallax = new Point2D(0.0, 0.0);
        private Point2D parallaxOffset = new Point2D(0.0, 0.0);
        private Tag tag = Tag.of();

        @Override
        public Builder setVisible(Boolean visible) {
            this.visible = visible;
            return this;
        }

        @Override
        public Builder setLocked(Boolean locked) {
            this.locked = locked;
            return this;
        }

        public Builder setDecText(@Nullable String decText) {
            this.decText = decText;
            return this;
        }

        public Builder setFont(@Nullable Font font) {
            this.font = font;
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

        @Override
        public AddText build() {
            return new AddText(visible, locked, decText, font, position, relativeTo, pivotOffset, rotation, lockRotation, scale, lockScale, color, opacity, depth, parallax, parallaxOffset, tag);
        }
    }

}

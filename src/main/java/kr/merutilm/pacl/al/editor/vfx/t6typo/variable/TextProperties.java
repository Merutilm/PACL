package kr.merutilm.pacl.al.editor.vfx.t6typo.variable;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.struct.Struct;
import kr.merutilm.base.struct.StructBuilder;

public record TextProperties(String text,
                             Point2D position,
                             double rotation,
                             double spacingX,
                             double scale,
                             HexColor color) implements Struct<TextProperties> {
    @Override
    public Builder edit() {
        return new TextProperties.Builder(text, position, rotation, spacingX, scale, color);
    }

    public static final class Builder implements StructBuilder<TextProperties> {
        private String text;
        private Point2D position;
        private double rotation;
        private double spacingX;
        private double scale;
        private HexColor color;

        public Builder(String text, Point2D position, double rotation, double spacingX, double scale, HexColor color) {
            this.text = text;
            this.position = position;
            this.rotation = rotation;
            this.spacingX = spacingX;
            this.scale = scale;
            this.color = color;
        }

        public Builder setPosition(Point2D position) {
            this.position = position;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setRotation(double rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder setSpacingX(double spacingX) {
            this.spacingX = spacingX;
            return this;
        }

        public Builder setScale(double scale) {
            this.scale = scale;
            return this;
        }

        public Builder setColor(HexColor color) {
            this.color = color;
            return this;
        }

        @Override
        public TextProperties build() {
            return new TextProperties(text, position, rotation, spacingX, scale, color);
        }
    }
}

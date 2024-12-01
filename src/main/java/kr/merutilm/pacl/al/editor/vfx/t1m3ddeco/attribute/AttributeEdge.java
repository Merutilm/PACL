package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.attribute;

import kr.merutilm.pacl.al.editor.vfx.Attribute;
import kr.merutilm.pacl.al.editor.vfx.AttributeBuilder;

@SuppressWarnings("UnusedReturnValue")
public record AttributeEdge(boolean showing,
                            double opacity,
                            double brightnessMultiplier,
                            double thickness) implements Attribute {

    @Override
    public AttributeBuilder edit() {
        return new Builder(showing, opacity, brightnessMultiplier, thickness);
    }

    public static final class Builder implements AttributeBuilder {

        /**
         * 모서리 보이기
         */
        private boolean showing;
        /**
         * 투명도
         */
        private double opacity;
        /**
         * 두 면 사이에 낀 모서리 밝기 배율
         */
        private double brightnessMultiplier;

        /**
         * 두께
         */
        private double thickness;

        public Builder(boolean showing, double opacity, double brightnessMultiplier, double thickness) {
            this.showing = showing;
            this.opacity = opacity;
            this.brightnessMultiplier = brightnessMultiplier;
            this.thickness = thickness;
        }

        public Builder setShowing(boolean showing) {
            this.showing = showing;
            return this;
        }

        public Builder setOpacity(double opacity) {
            this.opacity = opacity;
            return this;
        }

        public Builder setBrightnessMultiplier(double brightnessMultiplier) {
            this.brightnessMultiplier = brightnessMultiplier;
            return this;
        }

        public Builder setThickness(double thickness) {
            this.thickness = thickness;
            return this;
        }

        @Override
        public AttributeEdge build() {
            return new AttributeEdge(showing, opacity, brightnessMultiplier, thickness);
        }
    }
}

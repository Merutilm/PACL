package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable;

import javax.annotation.Nonnull;

import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.struct.Struct;
import kr.merutilm.base.struct.StructBuilder;

public record Particle(ImageFile image,
                       DotState state,
                       int layers,
                       double delaySec,
                       double scaleMultiplierPerLayer,
                       double opacityMultiplierPerLayer) implements Struct<Particle> {

    @Override
    public Builder edit() {
        return new Builder(image, state, layers, delaySec, scaleMultiplierPerLayer, opacityMultiplierPerLayer);
    }

    public static final class Builder implements StructBuilder<Particle> {

        private ImageFile image;
        private DotState state;
        private int layers;
        private double delaySec;
        private double scaleMultiplierPerLayer;

        private double opacityMultiplierPerLayer;

        public Builder(@Nonnull ImageFile image, @Nonnull DotState state, int layers, double delaySec, double scaleMultiplierPerLayer, double opacityMultiplierPerLayer) {
            this.image = image;
            this.state = state;
            this.layers = layers;
            this.delaySec = delaySec;
            this.scaleMultiplierPerLayer = scaleMultiplierPerLayer;
            this.opacityMultiplierPerLayer = opacityMultiplierPerLayer;
        }

        public Builder setImage(ImageFile image) {
            this.image = image;
            return this;
        }

        public Builder setState(@Nonnull DotState state) {
            this.state = state;
            return this;
        }

        public Builder setLayers(int layers) {
            this.layers = layers;
            return this;
        }

        public Builder setDelaySec(double delaySec) {
            this.delaySec = delaySec;
            return this;
        }

        public Builder setScaleMultiplierPerLayer(double scaleMultiplierPerLayer) {
            this.scaleMultiplierPerLayer = scaleMultiplierPerLayer;
            return this;
        }

        public Builder setOpacityMultiplierPerLayer(double opacityMultiplierPerLayer) {
            this.opacityMultiplierPerLayer = opacityMultiplierPerLayer;
            return this;
        }

        @Override
        public Particle build() {
            return new Particle(image, state, layers, delaySec, scaleMultiplierPerLayer, opacityMultiplierPerLayer);
        }

    }
}

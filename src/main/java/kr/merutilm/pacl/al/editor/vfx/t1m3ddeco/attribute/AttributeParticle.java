package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.attribute;

import kr.merutilm.pacl.al.editor.vfx.Attribute;
import kr.merutilm.pacl.al.editor.vfx.AttributeBuilder;
import kr.merutilm.pacl.al.event.selectable.BlendMode;

@SuppressWarnings("UnusedReturnValue")
public record AttributeParticle(BlendMode particleBlendMode,
                                double minScale,
                                double opacity,
                                double opacitySquaring) implements Attribute {

    @Override
    public AttributeBuilder edit() {
        return new Builder(particleBlendMode, minScale, opacity, opacitySquaring);
    }

    public static final class Builder implements AttributeBuilder {
        /**
         * 파티클 블렌드 모드
         */
        private BlendMode particleBlendMode;
        /**
         * 최소 크기, 최소 크기보다 작은 장식은 투명도가 0이 됩니다.
         */
        private double minScale;

        /**
         * 파티클 투명도
         */
        private double opacity;

        /**
         * 거리에 따른 불투명도 승수. 클수록 크기에 따른 불투명도 감소량이 줄어들며, 최소 크기 이하일때 투명도가 0이 됩니다.
         */
        private double opacitySquaring;

        public Builder(BlendMode particleBlendMode, double minScale, double opacity, double opacitySquaring) {
            this.particleBlendMode = particleBlendMode;
            this.minScale = minScale;
            this.opacity = opacity;
            this.opacitySquaring = opacitySquaring;
        }

        public Builder setParticleBlendMode(BlendMode particleBlendMode) {
            this.particleBlendMode = particleBlendMode;
            return this;
        }

        public Builder setMinScale(double minScale) {
            this.minScale = minScale;
            return this;
        }

        public Builder setOpacity(double opacity) {
            this.opacity = opacity;
            return this;
        }

        public Builder setOpacitySquaring(double opacitySquaring) {
            this.opacitySquaring = opacitySquaring;
            return this;
        }

        @Override
        public AttributeParticle build() {
            return new AttributeParticle(particleBlendMode, minScale, opacity, opacitySquaring);
        }
    }
}

package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.attribute;

import kr.merutilm.pacl.al.editor.vfx.Attribute;
import kr.merutilm.pacl.al.editor.vfx.AttributeBuilder;

@SuppressWarnings("UnusedReturnValue")
public record AttributeAsset(double defaultParallax,
                             double zDepthMultiplier,
                             double fps,
                             double tileZ,
                             int renderStartFloor,
                             double renderDuration,
                             boolean smoothTransition) implements Attribute {

    @Override
    public AttributeBuilder edit() {
        return new Builder(defaultParallax, zDepthMultiplier, fps, tileZ, renderStartFloor, renderDuration, smoothTransition);
    }

    public static final class Builder implements AttributeBuilder {
        /**
         * 기본 시차
         */
        private double defaultParallax;
        /**
         * z 좌표에 따른 장식 깊이 배율
         */
        private double zDepthMultiplier;
        /**
         * 렌더링 시 초당 프레임률
         */
        private double fps;
        /**
         * 타일 z 좌표 (양수)
         */
        private double tileZ;

        /**
         * 렌더링 시작 타일
         */
        private int renderStartFloor;

        /**
         * 렌더링 시작 타일을 밟은 직후 렌더링할 시간
         */
        private double renderDuration;

        /**
         * 부드러운 전환
         */
        private boolean smoothTransition;

        public Builder(double defaultParallax, double zDepthMultiplier, double fps, double tileZ, int renderStartFloor, double renderDuration, boolean smoothTransition) {
            this.defaultParallax = defaultParallax;
            this.zDepthMultiplier = zDepthMultiplier;
            this.fps = fps;
            this.tileZ = tileZ;
            this.renderStartFloor = renderStartFloor;
            this.renderDuration = renderDuration;
            this.smoothTransition = smoothTransition;
        }

        public Builder setDefaultParallax(double defaultParallax) {
            this.defaultParallax = defaultParallax;
            return this;
        }

        public Builder setZDepthMultiplier(double zDepthMultiplier) {
            this.zDepthMultiplier = zDepthMultiplier;
            return this;
        }

        public Builder setFps(double fps) {
            this.fps = fps;
            return this;
        }

        public Builder setTileZ(double tileZ) {
            this.tileZ = tileZ;
            return this;
        }

        public Builder setRenderStartFloor(int renderStartFloor) {
            this.renderStartFloor = renderStartFloor;
            return this;
        }

        public Builder setRenderDuration(double renderDuration) {
            this.renderDuration = renderDuration;
            return this;
        }

        public Builder setSmoothTransition(boolean smoothTransition) {
            this.smoothTransition = smoothTransition;
            return this;
        }

        @Override
        public AttributeAsset build() {
            return new AttributeAsset(defaultParallax, zDepthMultiplier, fps, tileZ, renderStartFloor, renderDuration, smoothTransition);
        }
    }
}

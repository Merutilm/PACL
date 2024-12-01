package kr.merutilm.pacl.al.editor.vfx.t3physics.attribute;

import kr.merutilm.pacl.al.editor.vfx.Attribute;
import kr.merutilm.pacl.al.editor.vfx.AttributeBuilder;

@SuppressWarnings("UnusedReturnValue")
public record AttributeAsset(double defaultParallax,
                             double fps,
                             int renderStartFloor,
                             double renderDuration,
                             boolean smoothTransition
) implements Attribute {

    @Override
    public AttributeBuilder edit() {
        return new Builder(defaultParallax, fps, renderStartFloor, renderDuration, smoothTransition);
    }

    public static final class Builder implements AttributeBuilder {
        /**
         * 기본 시차
         */
        private double defaultParallax;
        /**
         * 렌더링 시 초당 프레임률
         */
        private double fps;

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

        public Builder(double defaultParallax, double fps, int renderStartFloor, double renderDuration, boolean smoothTransition) {
            this.defaultParallax = defaultParallax;
            this.fps = fps;
            this.renderStartFloor = renderStartFloor;
            this.renderDuration = renderDuration;
            this.smoothTransition = smoothTransition;
        }

        public Builder setDefaultParallax(double defaultParallax) {
            this.defaultParallax = defaultParallax;
            return this;
        }


        public Builder setFps(double fps) {
            this.fps = fps;
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
            return new AttributeAsset(defaultParallax, fps, renderStartFloor, renderDuration, smoothTransition);
        }
    }
}

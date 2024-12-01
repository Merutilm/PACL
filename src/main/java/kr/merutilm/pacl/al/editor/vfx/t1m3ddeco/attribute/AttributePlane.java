package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.attribute;

import kr.merutilm.pacl.al.editor.vfx.Attribute;
import kr.merutilm.pacl.al.editor.vfx.AttributeBuilder;

@SuppressWarnings("UnusedReturnValue")
public record AttributePlane(boolean showing,
                             int maxSeparateTAmount,
                             double separateTDepthDispersion,
                             double depthDensity) implements Attribute {

    @Override
    public AttributeBuilder edit() {
        return new Builder(showing, maxSeparateTAmount, separateTDepthDispersion, depthDensity);
    }

    public static final class Builder implements AttributeBuilder {

        /**
         * 면 보이기
         */
        private boolean showing;

        /**
         * 도형을 표현하는 데 분할할 삼각형의 최대 개수
         */
        private int maxSeparateTAmount;

        /**
         * 삼각형을 세 개로 분리할 때 깊이 분산 (기준값 : 무게중심 = 1). 높은 값을 줄수록 깊이 기준점이 가장자리에 가까워지고 낮은 값을 줄수록 깊이 기준점이 안쪽에 가까워집니다.
         */
        private double separateTDepthDispersion;

        /**
         * 깊이 밀집도 (0~1), 커질수록 깊이가 중심점에 가까워집니다.
         */
        private double depthDensity;

        public Builder(boolean showing, int maxSeparateTAmount, double separateTDepthDispersion, double depthDensity) {
            this.showing = showing;
            this.maxSeparateTAmount = maxSeparateTAmount;
            this.separateTDepthDispersion = separateTDepthDispersion;
            this.depthDensity = depthDensity;
        }

        public Builder setShowing(boolean showing) {
            this.showing = showing;
            return this;
        }

        public Builder setMaxSeparateTAmount(int maxSeparateTAmount) {
            this.maxSeparateTAmount = maxSeparateTAmount;
            return this;
        }

        public Builder setSeparateTDepthDispersion(double separateTDepthDispersion) {
            this.separateTDepthDispersion = separateTDepthDispersion;
            return this;
        }

        public Builder setDepthDensity(double depthDensity) {
            this.depthDensity = depthDensity;
            return this;
        }

        @Override
        public AttributePlane build() {
            return new AttributePlane(showing, maxSeparateTAmount, separateTDepthDispersion, depthDensity);
        }
    }
}

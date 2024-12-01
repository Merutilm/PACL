package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.attribute;

import kr.merutilm.pacl.al.editor.vfx.Attribute;
import kr.merutilm.pacl.al.editor.vfx.AttributeBuilder;

@SuppressWarnings("UnusedReturnValue")
public record AttributeShadow(boolean showing,
                              double strength,
                              int depthSubtract) implements Attribute {

    @Override
    public AttributeBuilder edit() {
        return new Builder(showing, strength, depthSubtract);
    }

    public static final class Builder implements AttributeBuilder {

        /**
         * 그림자 보이기
         */
        private boolean showing;

        /**
         * 그림자 강도 (0~100)
         */
        private double strength;
        /**
         * 그림자가 비칠 면에서 뺄 깊이 수치
         */
        private int depthSubtract;

        public Builder(boolean showing, double strength, int depthSubtract) {
            this.showing = showing;
            this.strength = strength;
            this.depthSubtract = depthSubtract;
        }


        public Builder setShowing(boolean showing) {
            this.showing = showing;
            return this;
        }


        public Builder setStrength(double strength) {
            this.strength = strength;
            return this;
        }

        public Builder setDepthSubtract(int depthSubtract) {
            this.depthSubtract = depthSubtract;
            return this;
        }

        @Override
        public AttributeShadow build() {
            return new AttributeShadow(showing, strength, depthSubtract);
        }
    }
}

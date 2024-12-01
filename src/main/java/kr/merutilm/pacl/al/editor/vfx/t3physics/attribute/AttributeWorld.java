package kr.merutilm.pacl.al.editor.vfx.t3physics.attribute;

import kr.merutilm.pacl.al.editor.vfx.Attribute;
import kr.merutilm.pacl.al.editor.vfx.AttributeBuilder;

public record AttributeWorld(
        double gravity) implements Attribute {
    @Override
    public Builder edit() {
        return new Builder(gravity);
    }

    public static final class Builder implements AttributeBuilder {

        private double gravity;

        public Builder(double gravity) {
            this.gravity = gravity;
        }

        public Builder setGravity(double gravity) {
            this.gravity = gravity;
            return this;
        }

        @Override
        public AttributeWorld build() {
            return new AttributeWorld(gravity);
        }
    }
}

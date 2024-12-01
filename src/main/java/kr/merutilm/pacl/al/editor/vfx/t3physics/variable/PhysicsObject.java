package kr.merutilm.pacl.al.editor.vfx.t3physics.variable;

import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.struct.Struct;
import kr.merutilm.base.struct.StructBuilder;

public record PhysicsObject(
        ImageFile image,
        short depth,
        double opacity,
        double appearingAngle,
        PhysicsObjectState state

) implements Struct<PhysicsObject> {

    @Override
    public Builder edit() {
        return new Builder(image, depth, opacity, appearingAngle, state);
    }

    public static final class Builder implements StructBuilder<PhysicsObject> {

        private ImageFile image;
        private short depth;
        private double opacity;
        private double appearingAngle;
        private PhysicsObjectState state;

        public Builder(ImageFile image, short depth, double opacity, double appearingAngle, PhysicsObjectState state) {
            this.image = image;
            this.depth = depth;
            this.appearingAngle = appearingAngle;
            this.opacity = opacity;
            this.state = state;
        }

        public Builder setImage(ImageFile image) {
            this.image = image;
            return this;
        }

        public Builder setDepth(short depth) {
            this.depth = depth;
            return this;
        }

        public Builder setOpacity(double opacity) {
            this.opacity = opacity;
            return this;
        }

        public Builder setAppearingAngle(double appearingAngle) {
            this.appearingAngle = appearingAngle;
            return this;
        }

        public Builder setState(PhysicsObjectState state) {
            this.state = state;
            return this;
        }

        @Override
        public PhysicsObject build() {
            return new PhysicsObject(image, depth, opacity, appearingAngle, state);
        }
    }
}

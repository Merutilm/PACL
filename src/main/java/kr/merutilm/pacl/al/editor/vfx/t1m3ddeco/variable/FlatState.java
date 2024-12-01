package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable;

import kr.merutilm.base.struct.Point3D;
import kr.merutilm.base.struct.Struct;
import kr.merutilm.base.struct.StructBuilder;

public record FlatState(Point3D centerPosition,
                        Point3D rotation,
                        double scale) implements Struct<FlatState> {

    @Override
    public Builder edit() {
        return new Builder(centerPosition, rotation, scale);
    }
    public static final class Builder implements StructBuilder<FlatState> {
        private Point3D centerPosition;
        private Point3D rotation;
        private double scale;

        public Builder(Point3D centerPosition, Point3D rotation, double scale) {
            this.centerPosition = centerPosition;
            this.rotation = rotation;
            this.scale = scale;
        }

        public Builder setCenterPosition(Point3D centerPosition) {
            this.centerPosition = centerPosition;
            return this;
        }

        public Builder setRotation(Point3D rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder setScale(double scale) {
            this.scale = scale;
            return this;
        }

        @Override
        public FlatState build() {
            return new FlatState(centerPosition, rotation, scale);
        }
    }

}


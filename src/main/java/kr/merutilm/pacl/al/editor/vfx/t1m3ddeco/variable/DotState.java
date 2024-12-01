package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.Point3D;
import kr.merutilm.base.struct.Struct;
import kr.merutilm.base.struct.StructBuilder;

public record DotState(Point3D position, Point3D centerRotatePosition, Point3D rotation, double scale,
                       HexColor color) implements Struct<DotState> {
    public static DotState get(Point3D position,
                               Point3D rotation,
                               double scale) {
        return get(position, new Point3D(0, 0, 0), rotation, scale);
    }

    public static DotState get(Point3D position,
                               Point3D centerRotatePosition,
                               Point3D rotation,
                               double scale) {
        return new DotState(position, centerRotatePosition, rotation, scale, HexColor.WHITE);
    }

    @Override
    public Builder edit() {
        return new Builder(position, centerRotatePosition, rotation, scale, color);
    }

    public static final class Builder implements StructBuilder<DotState> {
        private Point3D centerPosition;
        private Point3D centerRotatePosition;
        private Point3D rotation;
        private double scale;
        private HexColor color;

        public Builder(Point3D centerPosition, Point3D centerRotatePosition, Point3D rotation, double scale, HexColor color) {
            this.centerPosition = centerPosition;
            this.centerRotatePosition = centerRotatePosition;
            this.rotation = rotation;
            this.scale = scale;
            this.color = color;
        }

        public Builder setCenterPosition(Point3D centerPosition) {
            this.centerPosition = centerPosition;
            return this;
        }

        public Builder setCenterRotatePosition(Point3D centerRotatePosition) {
            this.centerRotatePosition = centerRotatePosition;
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

        public Builder setColor(HexColor color) {
            this.color = color;
            return this;
        }

        @Override
        public DotState build() {
            return new DotState(centerPosition, centerRotatePosition, rotation, scale, color);
        }
    }

}


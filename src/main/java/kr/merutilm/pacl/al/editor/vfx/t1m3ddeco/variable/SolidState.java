package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable;

import javax.annotation.Nonnull;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.Point3D;
import kr.merutilm.base.struct.Struct;
import kr.merutilm.base.struct.StructBuilder;

/**
 * @param centerPosition       중심 좌표
 * @param centerRotatePosition 회전 중심 좌표
 * @param rotation             회전 오프셋
 * @param scale                크기
 * @param color                색상
 */
public record SolidState(Point3D centerPosition,
                         Point3D centerRotatePosition,
                         Point3D rotation,
                         Point3D scale,
                         HexColor color) implements Struct<SolidState> {

    public static SolidState get(@Nonnull Point3D centerPosition) {
        return get(centerPosition, HexColor.WHITE);
    }

    public static SolidState get(@Nonnull Point3D centerPosition,
                                 @Nonnull HexColor color) {
        return get(centerPosition, new Point3D(100, 100, 100), color);
    }

    public static SolidState get(@Nonnull Point3D centerPosition,
                                 @Nonnull Point3D scale) {
        return get(centerPosition, scale, HexColor.WHITE);
    }

    public static SolidState get(@Nonnull Point3D centerPosition,
                                 @Nonnull Point3D scale,
                                 @Nonnull HexColor color) {
        return get(centerPosition, new Point3D(0, 0, 0), scale, color);
    }

    public static SolidState get(@Nonnull Point3D centerPosition,
                                 @Nonnull Point3D rotation,
                                 @Nonnull Point3D scale) {
        return get(centerPosition, rotation, scale, HexColor.WHITE);
    }

    public static SolidState get(@Nonnull Point3D centerPosition,
                                 @Nonnull Point3D rotation,
                                 @Nonnull Point3D scale,
                                 @Nonnull HexColor color) {
        return new SolidState(centerPosition, centerPosition, rotation, scale, color);
    }

    @Override
    public Builder edit() {
        return new Builder(centerPosition, centerRotatePosition, rotation, scale, color);
    }

    public static final class Builder implements StructBuilder<SolidState> {
        private Point3D centerPosition;
        private Point3D centerRotatePosition;
        private Point3D rotation;
        private Point3D scale;
        private HexColor color;

        public Builder(Point3D centerPosition, Point3D centerRotatePosition, Point3D rotation, Point3D scale, HexColor color) {
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

        public Builder setScale(Point3D scale) {
            this.scale = scale;
            return this;
        }

        public Builder setColor(HexColor color) {
            this.color = color;
            return this;
        }

        @Override
        public SolidState build() {
            return new SolidState(centerPosition, centerRotatePosition, rotation, scale, color);
        }
    }

}


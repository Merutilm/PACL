package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.attribute;

import kr.merutilm.base.struct.Point3D;
import kr.merutilm.pacl.al.editor.vfx.Attribute;
import kr.merutilm.pacl.al.editor.vfx.AttributeBuilder;

@SuppressWarnings("UnusedReturnValue")
public record AttributeCamera(Point3D cameraPosition,
                              Point3D cameraRotation,
                              Point3D viewPosition,
                              Point3D viewRotation) implements Attribute {

    @Override
    public AttributeBuilder edit() {
        return new Builder(cameraPosition, cameraRotation, viewPosition, viewRotation);
    }
    public static final class Builder implements AttributeBuilder {
        /**
         * 카메라 위치
         */
        private Point3D cameraPosition;
        /**
         * 카메라 축 회전
         */
        private Point3D cameraRotation;
        /**
         * 카메라 기준 시점 위치
         */
        private Point3D viewPosition;
        /**
         * 카메라 기준 시점 회전 (카메라 중심)
         */
        private Point3D viewRotation;


        public final Point3D defaultCameraPosition;

        public final Point3D defaultCameraRotation;

        public final Point3D defaultViewPosition;

        public final Point3D defaultViewRotation;
        public Builder(Point3D cameraPosition, Point3D cameraRotation) {
            this.cameraPosition = cameraPosition;
            this.cameraRotation = cameraRotation;
            this.viewPosition = new Point3D(0, 0, 0);
            this.viewRotation = new Point3D(0, 0, 0);

            this.defaultCameraPosition = cameraPosition;
            this.defaultCameraRotation = cameraRotation;
            this.defaultViewPosition = new Point3D(0, 0, 0);
            this.defaultViewRotation = new Point3D(0, 0, 0);

        }

        public Builder(Point3D cameraPosition, Point3D cameraRotation, Point3D viewPosition, Point3D viewRotation) {
            this.cameraPosition = cameraPosition;
            this.cameraRotation = cameraRotation;
            this.viewPosition = viewPosition;
            this.viewRotation = viewRotation;

            this.defaultCameraPosition = cameraPosition;
            this.defaultCameraRotation = cameraRotation;
            this.defaultViewPosition = viewPosition;
            this.defaultViewRotation = viewRotation;
        }

        public Builder setCameraPosition(Point3D cameraPosition) {
            this.cameraPosition = cameraPosition;
            return this;
        }

        public Builder setCameraRotation(Point3D cameraRotation) {
            this.cameraRotation = cameraRotation;
            return this;
        }

        public Builder setViewPosition(Point3D viewPosition) {
            this.viewPosition = viewPosition;
            return this;
        }

        public Builder setViewRotation(Point3D viewRotation) {
            this.viewRotation = viewRotation;
            return this;
        }

        public AttributeCamera build() {
            return new AttributeCamera(cameraPosition, cameraRotation, viewPosition, viewRotation);
        }
    }
    }

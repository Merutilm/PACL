package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.attribute;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.struct.Point3D;
import kr.merutilm.pacl.al.editor.vfx.Attribute;
import kr.merutilm.pacl.al.editor.vfx.AttributeBuilder;

@SuppressWarnings("UnusedReturnValue")
public record AttributeLight(ImageFile lightImage,
                             HexColor lightColor,
                             double scale,
                             boolean showing,
                             boolean spreading,
                             Point3D position,
                             double intensity,
                             double shadowIntensity,
                             double sideReflectivity,
                             double minSpreadingIntensity,
                             double maxSpreadingIntensity,
                             boolean lockDepth,
                             short lockDepthValue,
                             HexColor spreadColor) implements Attribute {

    @Override
    public AttributeBuilder edit() {
        return new Builder(lightImage, lightColor, scale, showing, spreading, position, intensity, shadowIntensity, sideReflectivity, minSpreadingIntensity, maxSpreadingIntensity, lockDepth, lockDepthValue, spreadColor);
    }

    public static final class Builder implements AttributeBuilder {
        /**
         * 광원 이미지
         */
        private ImageFile lightImage;
        /**
         * 광원 색상
         */
        private HexColor lightColor;
        /**
         * 광원 크기
         */
        private double scale;
        /**
         * 광원 표시
         */
        private boolean showing;
        /**
         * 광원 퍼짐 표시
         */
        private boolean spreading;
        /**
         * 광원 위치. 거리에 따라 최소 밝기에 영향을 줍니다.
         */
        private Point3D position;
        /**
         * 광원 강도. 최대 밝기에 영향을 줍니다.
         */
        private double intensity;
        /**
         * 그림자 강도. 최소 밝기에 영향을 줍니다.
         */
        private double shadowIntensity;
        /**
         * 전면 빛에 대한 옆면의 민감도 (0.5 : 중간 색상, 0 : 최소 밝기, 1: 매우 밝음)
         */
        private double sideReflectivity;
        /**
         * 최소 광원 퍼짐 강도
         */
        private double minSpreadingIntensity;

        /**
         * 최대 광원 퍼짐 강도
         */
        private double maxSpreadingIntensity;
        /**
         * 깊이 잠금
         */
        private boolean lockDepth;
        /**
         * 잠금 깊이 값
         */
        private short lockDepthValue;
        /**
         * 광원 퍼짐 색상
         */
        private HexColor spreadColor;

        public Builder(ImageFile lightImage, HexColor lightColor, double scale, boolean showing, boolean spreading, Point3D position, double intensity, double shadowIntensity, double sideReflectivity, double minSpreadingIntensity, double maxSpreadingIntensity, boolean lockDepth, short lockDepthValue, HexColor spreadColor) {
            this.lightImage = lightImage;
            this.lightColor = lightColor;
            this.scale = scale;
            this.showing = showing;
            this.spreading = spreading;
            this.position = position;
            this.intensity = intensity;
            this.shadowIntensity = shadowIntensity;
            this.sideReflectivity = sideReflectivity;
            this.minSpreadingIntensity = minSpreadingIntensity;
            this.maxSpreadingIntensity = maxSpreadingIntensity;
            this.lockDepth = lockDepth;
            this.lockDepthValue = lockDepthValue;
            this.spreadColor = spreadColor;
        }

        public Builder setLightImage(ImageFile lightImage) {
            this.lightImage = lightImage;
            return this;
        }

        public Builder setLightColor(HexColor lightColor) {
            this.lightColor = lightColor;
            return this;
        }

        public Builder setScale(double scale) {
            this.scale = scale;
            return this;
        }

        public Builder setShowing(boolean showing) {
            this.showing = showing;
            return this;
        }

        public Builder setSpreading(boolean spreading) {
            this.spreading = spreading;
            return this;
        }

        public Builder setPosition(Point3D position) {
            this.position = position;
            return this;
        }

        public Builder setIntensity(double intensity) {
            this.intensity = intensity;
            return this;
        }

        public Builder setShadowIntensity(double shadowIntensity) {
            this.shadowIntensity = shadowIntensity;
            return this;
        }

        public Builder setSideReflectivity(double sideReflectivity) {
            this.sideReflectivity = sideReflectivity;
            return this;
        }

        public Builder setMinSpreadingIntensity(double minSpreadingIntensity) {
            this.minSpreadingIntensity = minSpreadingIntensity;
            return this;
        }

        public Builder setMaxSpreadingIntensity(double maxSpreadingIntensity) {
            this.maxSpreadingIntensity = maxSpreadingIntensity;
            return this;
        }

        public Builder setLockDepth(boolean lockDepth) {
            this.lockDepth = lockDepth;
            return this;
        }

        public Builder setLockDepthValue(short lockDepthValue) {
            this.lockDepthValue = lockDepthValue;
            return this;
        }

        public Builder setSpreadColor(HexColor spreadColor) {
            this.spreadColor = spreadColor;
            return this;
        }

        @Override
        public AttributeLight build() {
            return new AttributeLight(lightImage, lightColor, scale, showing, spreading, position, intensity, shadowIntensity, sideReflectivity, minSpreadingIntensity, maxSpreadingIntensity, lockDepth, lockDepthValue, spreadColor);
        }
    }

}

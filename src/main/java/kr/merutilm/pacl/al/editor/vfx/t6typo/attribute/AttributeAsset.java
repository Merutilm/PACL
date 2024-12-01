package kr.merutilm.pacl.al.editor.vfx.t6typo.attribute;

import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.editor.vfx.Attribute;
import kr.merutilm.pacl.al.editor.vfx.AttributeBuilder;

@SuppressWarnings("UnusedReturnValue")
public record AttributeAsset(Point2D defaultParallax,
                             short defaultDepth,
                             boolean lockToCamera,
                             String decorationImagePrefix) implements Attribute {

    @Override
    public AttributeBuilder edit() {
        return new Builder(defaultParallax, defaultDepth, lockToCamera, decorationImagePrefix);
    }

    public static final class Builder implements AttributeBuilder {
        /**
         * 기본 시차
         */
        private Point2D defaultParallax;
        /**
         * 기본 깊이
         */
        private short defaultDepth;
        /**
         * 카메라 고정
         */
        private boolean lockToCamera;

        /**
         * 장식 파일 접두어
         */
        private String decorationImagePrefix;

        public Builder(Point2D defaultParallax, short defaultDepth, boolean lockToCamera, String decorationImagePrefix) {
            this.defaultParallax = defaultParallax;
            this.defaultDepth = defaultDepth;
            this.lockToCamera = lockToCamera;
            this.decorationImagePrefix = decorationImagePrefix;
        }

        public Builder setDefaultParallax(Point2D defaultParallax) {
            this.defaultParallax = defaultParallax;
            return this;
        }

        public Builder setDefaultDepth(short defaultDepth) {
            this.defaultDepth = defaultDepth;
            return this;
        }

        public Builder setLockToCamera(boolean lockToCamera) {
            this.lockToCamera = lockToCamera;
            return this;
        }

        public Builder setDecorationImagePrefix(String decorationImagePrefix) {
            this.decorationImagePrefix = decorationImagePrefix;
            return this;
        }

        @Override
        public AttributeAsset build() {
            return new AttributeAsset(defaultParallax, defaultDepth, lockToCamera, decorationImagePrefix);
        }
    }
}

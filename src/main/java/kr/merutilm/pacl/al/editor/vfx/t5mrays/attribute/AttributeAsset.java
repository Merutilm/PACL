package kr.merutilm.pacl.al.editor.vfx.t5mrays.attribute;

import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.pacl.al.editor.vfx.Attribute;
import kr.merutilm.pacl.al.editor.vfx.AttributeBuilder;

@SuppressWarnings("UnusedReturnValue")
public record AttributeAsset(ImageFile imageFile) implements Attribute {

    @Override
    public AttributeBuilder edit() {
        return new Builder(imageFile);
    }

    public static final class Builder implements AttributeBuilder {
        /**
         * 기본 이미지
         */
        private ImageFile imageFile;

        public Builder(ImageFile imageFile) {
            this.imageFile = imageFile;
        }

        public Builder setDecorationImage(ImageFile imageFile) {
            this.imageFile = imageFile;
            return this;
        }

        @Override
        public AttributeAsset build() {
            return new AttributeAsset(imageFile);
        }
    }
}

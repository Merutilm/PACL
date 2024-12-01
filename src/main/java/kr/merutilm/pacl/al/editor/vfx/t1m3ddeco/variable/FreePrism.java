package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable;

import javax.annotation.Nonnull;

import kr.merutilm.base.struct.Shape;

/**
 * 각기둥
 *
 * @param undersideShape       밑면 모양
 */
public record FreePrism(@Nonnull Shape undersideShape,
                        @Nonnull SolidState state) implements Solid {

    @Override
    public Builder edit() {
        return new Builder(undersideShape, state);
    }

    public static final class Builder implements SolidBuilder {
        private Shape undersideShape;
        private SolidState state;

        public Builder(@Nonnull Shape undersideShape, @Nonnull SolidState state) {
            this.undersideShape = undersideShape;
            this.state =state;
        }
        public Builder setUndersideShape(Shape undersideShape){
            this.undersideShape = undersideShape;
            return this;
        }
        public Builder setSolidState(SolidState state){
            this.state = state;
            return this;
        }

        @Override
        public Solid build() {
            return new FreePrism(undersideShape, state);
        }
    }
}

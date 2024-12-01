package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable;

import javax.annotation.Nonnull;


/**
 * 정다면체
 *
 * @param solidType 형태
 */
public record Polyhedron(@Nonnull SolidType solidType,
                         @Nonnull SolidState state) implements Solid {

    @Override
    public Builder edit() {
        return new Builder(solidType, state);
    }

    public static final class Builder implements SolidBuilder {
        private SolidType solidType;
        private SolidState state;

        public Builder(@Nonnull SolidType solidType, @Nonnull SolidState state) {
            this.solidType = solidType;
            this.state = state;
        }

        public Builder setShapeType(SolidType solidType) {
            this.solidType = solidType;
            return this;
        }

        @Override
        public Builder setSolidState(SolidState state) {
            this.state = state;
            return this;
        }

        @Override
        public Solid build() {
            return new Polyhedron(solidType, state);
        }
    }
}














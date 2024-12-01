package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable;

import javax.annotation.Nonnull;

import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.struct.Shape;

import java.util.Arrays;

/**
 * 밑면이 정다각형인 각기둥
 *
 * @param edgesAmount          밑면 꼭짓점 개수
 */
public record Prism(int edgesAmount,
                    @Nonnull SolidState state) implements Solid {
    @Override
    public Builder edit() {
        return new Builder(edgesAmount, state);
    }

    public static final class Builder implements SolidBuilder {
        private int edgesAmount;
        private SolidState state;

        public Builder(int edgesAmount, @Nonnull SolidState state) {
            this.edgesAmount = edgesAmount;
            this.state = state;
        }

        public Builder setShapeType(int edgesAmount) {
            this.edgesAmount = edgesAmount;
            return this;
        }

        @Override
        public Builder setSolidState(SolidState state) {
            this.state = state;
            return this;
        }

        @Override
        public Solid build() {
            return new Prism(edgesAmount, state);
        }
    }

    public FreePrism convertToFreePrism() {
        Point2D[] undersideShape = new Point2D[edgesAmount];
        for (int i = 0; i < edgesAmount; i++) {
            double angle = Math.toRadians(i * 360.0 / edgesAmount);
            double x = Math.cos(angle) / 2;
            double y = Math.sin(angle) / 2;
            undersideShape[i] = new Point2D(x, y);
        }
        Shape shape = new Shape(Arrays.stream(undersideShape)
                .map(v -> v.edit()
                        .rotate(0, 0, 90)
                        .build())
                .toArray(Point2D[]::new));
        return new FreePrism(shape, state);
    }
}

package kr.merutilm.pacl.al.editor.vfx.t3physics.variable;

import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.struct.PolarPoint;
import kr.merutilm.base.struct.Struct;
import kr.merutilm.base.struct.StructBuilder;

public record PhysicsObjectState(
        Point2D coordinate,
        PolarPoint velocity,
        double mass,
        double dragCoefficient,
        Point2D scale

) implements Struct<PhysicsObjectState> {

    public PhysicsObjectState(Point2D coordinate, PolarPoint velocity, double mass, Point2D scale){
        this(coordinate, velocity, mass, 0, scale);
    }

    @Override
    public Builder edit() {
        return new Builder(coordinate, velocity, mass, dragCoefficient, scale);
    }

    public static final class Builder implements StructBuilder<PhysicsObjectState> {
        private Point2D coordinate;
        private PolarPoint velocity;
        private double mass;
        private double dragCoefficient;
        private Point2D scale;

        public Builder(Point2D coordinate, PolarPoint velocity, double mass, double dragCoefficient, Point2D scale) {
            this.coordinate = coordinate;
            this.velocity = velocity;
            this.mass = mass;
            this.dragCoefficient = dragCoefficient;
            this.scale = scale;
        }

        public Builder setCoordinate(Point2D coordinate) {
            this.coordinate = coordinate;
            return this;
        }

        public Builder setVelocity(PolarPoint velocity) {
            this.velocity = velocity;
            return this;
        }

        public Builder setMass(double mass) {
            this.mass = mass;
            return this;
        }

        public Builder setDragCoefficient(double dragCoefficient){
            this.dragCoefficient = dragCoefficient;
            return this;
        }

        public Builder setScale(Point2D scale){
            this.scale = scale;
            return this;
        }

        @Override
        public PhysicsObjectState build() {
            return new PhysicsObjectState(coordinate, velocity, mass, dragCoefficient, scale);
        }
    }
}

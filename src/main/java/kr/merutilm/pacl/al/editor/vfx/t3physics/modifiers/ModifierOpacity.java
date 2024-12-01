package kr.merutilm.pacl.al.editor.vfx.t3physics.modifiers;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.pacl.al.editor.vfx.initmodifier.ModifierBuilder;

import javax.annotation.Nonnull;

public record ModifierOpacity(int floor,
                              int groupID,
                              double duration,
                              Double destination,
                              FunctionEase ease,
                              double angleOffset

) implements MPSModifier<ModifierOpacity, Double> {

    @Override
    public Class<ModifierOpacity> type() {
        return ModifierOpacity.class;
    }

    @Override
    public ModifierBuilder<ModifierOpacity, Double> edit() {
        return new Builder(floor, groupID, duration, destination, ease, angleOffset);
    }

    public static final class Builder implements ModifierBuilder<ModifierOpacity, Double> {
        private int floor;
        private int groupID;
        private double duration;
        private Double destination;
        private FunctionEase ease;
        private double angleOffset;

        public Builder(int floor, int groupID, double duration, Double destination, FunctionEase ease, double angleOffset) {
            this.floor = floor;
            this.groupID = groupID;
            this.duration = duration;
            this.destination = destination;
            this.ease = ease;
            this.angleOffset = angleOffset;
        }

        @Override
        public Builder setFloor(int floor) {
            this.floor = floor;
            return this;
        }

        public Builder setGroupID(int groupID) {
            this.groupID = groupID;
            return this;
        }

        @Override
        public Builder setDuration(double duration) {
            this.duration = duration;
            return this;
        }

        @Override
        public Builder setDestination(Double destination) {
            this.destination = destination;
            return this;
        }

        @Override
        public Builder setEase(@Nonnull FunctionEase ease) {
            this.ease = ease;
            return this;
        }

        @Override
        public Builder setAngleOffset(double angleOffset) {
            this.angleOffset = angleOffset;
            return this;
        }

        @Override
        public ModifierOpacity build() {
            return new ModifierOpacity(floor, groupID, duration, destination, ease, angleOffset);
        }
    }
}

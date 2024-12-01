package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.modifiers;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.pacl.al.editor.vfx.initmodifier.ModifierBuilder;

import javax.annotation.Nonnull;

/**
 * 3D 개체 이동 트리거
 *
 * @param floor       실행 기준 타일
 * @param groupID     개체 아이디
 * @param duration    기간
 * @param destination 목적지
 * @param ease        가감속
 * @param angleOffset 각도 오프셋
 */
public record ModifierParticleColor(int floor,
                                    int groupID,
                                    double duration,
                                    @Nonnull HexColor destination,
                                    FunctionEase ease,
                                    double angleOffset) implements M3CModifier<ModifierParticleColor, HexColor> {

    @Override
    public Class<ModifierParticleColor> type() {
        return ModifierParticleColor.class;
    }

    @Override
    public ModifierBuilder<ModifierParticleColor, HexColor> edit() {
        return new Builder(floor, groupID, duration, destination, ease, angleOffset);
    }

    public static final class Builder implements ModifierBuilder<ModifierParticleColor, HexColor> {
        private int floor;
        private int groupID;
        private double duration;
        private HexColor destination;
        private FunctionEase ease;
        private double angleOffset;

        public Builder(int floor, int groupID, double duration, HexColor destination, FunctionEase ease, double angleOffset) {
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
        public Builder setDestination(@Nonnull HexColor destination) {
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
        public ModifierParticleColor build() {
            return new ModifierParticleColor(floor, groupID, duration, destination, ease, angleOffset);
        }
    }
}

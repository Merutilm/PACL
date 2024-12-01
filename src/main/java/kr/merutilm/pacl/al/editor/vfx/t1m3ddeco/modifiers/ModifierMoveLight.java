package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.modifiers;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.base.struct.Point3D;
import kr.merutilm.pacl.al.editor.vfx.initmodifier.ModifierBuilder;
import kr.merutilm.pacl.al.editor.vfx.initmodifier.SingleGroupModifier;

import javax.annotation.Nonnull;


/**
 * 광원 이동 트리거
 *
 * @param floor       실행 기준 타일
 * @param duration    기간
 * @param destination 목적지
 * @param ease        가감속
 * @param angleOffset 각도 오프셋
 */
@SingleGroupModifier
public record ModifierMoveLight(int floor,
                                double duration,
                                @Nonnull Point3D destination,
                                FunctionEase ease,
                                double angleOffset) implements M3CModifier<ModifierMoveLight, Point3D> {

    @Override
    public Class<ModifierMoveLight> type() {
        return ModifierMoveLight.class;
    }

    @Override
    public int groupID() {
        return 1;
    }

    public ModifierBuilder<ModifierMoveLight, Point3D> edit() {
        return new Builder(floor, duration, destination, ease, angleOffset);
    }

    public static final class Builder implements ModifierBuilder<ModifierMoveLight, Point3D> {
        private int floor;
        private double duration;
        private Point3D destination;
        private FunctionEase ease;
        private double angleOffset;

        public Builder(int floor, double duration, @Nonnull Point3D destination, FunctionEase ease, double angleOffset) {
            this.floor = floor;
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
        @Override
        public Builder setDuration(double duration) {
            this.duration = duration;
            return this;
        }
        @Override
        public Builder setDestination(@Nonnull Point3D destination) {
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
        public ModifierMoveLight build() {
            return new ModifierMoveLight(floor, duration, destination, ease, angleOffset);
        }
    }

}

package kr.merutilm.pacl.al.editor.vfx.t3physics.triggers;

import kr.merutilm.base.struct.PolarPoint;
import kr.merutilm.pacl.al.editor.vfx.inittrigger.TriggerBuilder;

/**
 * 물체에 힘을 가합니다.
 *
 * @param floor       실행 기준 타일
 * @param groupID     개체 아이디
 * @param duration    작용 기간
 * @param value       가할 힘(벡터)
 * @param angleOffset 각도 오프셋
 */
public record TriggerForce(int floor,
                           int groupID,
                           double duration,
                           PolarPoint value,
                           double angleOffset) implements MPSTrigger<TriggerForce, PolarPoint> {
    @Override
    public Builder edit() {
        return new Builder(floor, groupID, duration, value, angleOffset);
    }

    @Override
    public Class<TriggerForce> type() {
        return TriggerForce.class;
    }

    public static final class Builder implements TriggerBuilder<TriggerForce, PolarPoint> {
        private int floor;
        private int groupID;
        private double duration;
        private PolarPoint value;
        private double angleOffset;

        public Builder(int floor, int groupID, double duration, PolarPoint value, double angleOffset) {
            this.floor = floor;
            this.groupID = groupID;
            this.duration = duration;
            this.value = value;
            this.angleOffset = angleOffset;
        }

        public Builder setFloor(int floor) {
            this.floor = floor;
            return this;
        }

        public Builder setGroupID(int groupID) {
            this.groupID = groupID;
            return this;
        }

        public Builder setDuration(double duration) {
            this.duration = duration;
            return this;
        }

        public Builder setValue(PolarPoint value) {
            this.value = value;
            return this;
        }

        public Builder setAngleOffset(double angleOffset) {
            this.angleOffset = angleOffset;
            return this;
        }

        @Override
        public TriggerForce build() {
            return new TriggerForce(floor, groupID, duration, value, angleOffset);
        }
    }
}

package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.events.AngleOffsetEvent;
import kr.merutilm.pacl.al.event.events.AngleOffsetEventBuilder;
import kr.merutilm.pacl.al.event.selectable.SpeedType;


public record SetSpeed(Boolean active,
                       SpeedType speedType,
                       Double bpm,
                       Double multiplier,
                       Double angleOffset) implements Actions, AngleOffsetEvent {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setSpeedType(speedType)
                .setBpm(bpm)
                .setMultiplier(multiplier)
                .setAngleOffset(angleOffset);
    }

    public static final class Builder implements ActionsBuilder, AngleOffsetEventBuilder {
        private Boolean active;
        private SpeedType speedType = SpeedType.BPM;
        private Double bpm = 100d;
        private Double multiplier = 1d;
        private Double angleOffset = 0d;
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setSpeedType(@Nullable SpeedType speedType) {
            this.speedType = speedType;
            return this;
        }


        public Builder setBpm(@Nullable Double bpm) {
            this.bpm = bpm;
            return this;
        }


        public Builder setMultiplier(@Nullable Double multiplier) {
            this.multiplier = multiplier;
            return this;
        }

        public Builder setAngleOffset(@Nullable Double angleOffset) {
            this.angleOffset = angleOffset;
            return this;
        }

        @Override
        public SetSpeed build() {
            return new SetSpeed(active, speedType, bpm, multiplier, angleOffset);
        }

    }
}

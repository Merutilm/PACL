package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.events.DoubleDurationEvent;
import kr.merutilm.pacl.al.event.events.DoubleDurationEventBuilder;


public record Pause(Boolean active,
                    Double duration,
                    Integer countdownTicks,
                    Integer angleCorrectionDir) implements Actions, DoubleDurationEvent {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setDuration(duration)
                .setCountdownTicks(countdownTicks)
                .setAngleCorrectionDir(angleCorrectionDir);
    }

    public static final class Builder implements ActionsBuilder, DoubleDurationEventBuilder {
        private Boolean active;
        private Double duration = 1d;
        private Integer countdownTicks = 0;
        private Integer angleCorrectionDir = -1;
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setDuration(@Nullable Double duration) {
            this.duration = duration;
            return this;
        }


        public Builder setCountdownTicks(@Nullable Integer countdownTicks) {
            this.countdownTicks = countdownTicks;
            return this;
        }

        public Builder setAngleCorrectionDir(@Nullable Integer angleCorrectionDir) {
            this.angleCorrectionDir = angleCorrectionDir;
            return this;
        }

        @Override
        public Pause build() {
            return new Pause(active, duration, countdownTicks, angleCorrectionDir);
        }
    }
}

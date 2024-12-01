package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.selectable.RepeatType;
import kr.merutilm.pacl.al.event.struct.Tag;

public record RepeatEvents(Boolean active,
                           RepeatType repeatType,
                           Integer repetitions,
                           Integer floorCount,
                           Double interval,
                           Boolean executeOnCurrentFloor,
                           Tag tag) implements Actions {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setRepeatType(repeatType)
                .setRepetitions(repetitions)
                .setFloorCount(floorCount)
                .setInterval(interval)
                .setExecuteOnCurrentFloor(executeOnCurrentFloor)
                .setTag(tag);
    }


    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private RepeatType repeatType = RepeatType.BEAT;
        private Integer repetitions = 1;
        private Integer floorCount = 1;
        private Double interval = 1d;
        private Boolean executeOnCurrentFloor = false;

        private Tag tag = Tag.of();

        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setRepeatType(RepeatType repeatType) {
            this.repeatType = repeatType;
            return this;
        }

        public Builder setRepetitions(@Nullable Integer repetitions) {
            this.repetitions = repetitions;
            return this;
        }

        public Builder setFloorCount(Integer floorCount) {
            this.floorCount = floorCount;
            return this;
        }

        public Builder setInterval(@Nullable Double interval) {
            this.interval = interval;
            return this;
        }

        public Builder setExecuteOnCurrentFloor(Boolean executeOnCurrentFloor) {
            this.executeOnCurrentFloor = executeOnCurrentFloor;
            return this;
        }

        public Builder setTag(@Nullable Tag tag) {
            this.tag = tag;
            return this;
        }

        @Override
        public RepeatEvents build() {
            return new RepeatEvents(active, repeatType, repetitions, floorCount, interval, executeOnCurrentFloor, tag);
        }

    }
}

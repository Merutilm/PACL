package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.events.AngleOffsetEvent;
import kr.merutilm.pacl.al.event.events.AngleOffsetEventBuilder;
import kr.merutilm.pacl.al.event.events.EventTagEvent;
import kr.merutilm.pacl.al.event.events.EventTagEventBuilder;
import kr.merutilm.pacl.al.event.selectable.HitSound;
import kr.merutilm.pacl.al.event.struct.Tag;


public record PlaySound(Boolean active,
                        HitSound hitSound,
                        Integer volume,
                        Double angleOffset,
                        Tag eventTag) implements Actions, AngleOffsetEvent, EventTagEvent {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setHitSound(hitSound)
                .setVolume(volume)
                .setAngleOffset(angleOffset)
                .setEventTag(eventTag);
    }

    public static final class Builder implements ActionsBuilder, AngleOffsetEventBuilder, EventTagEventBuilder {
        private Boolean active;
        private HitSound hitSound = HitSound.KICK;
        private Integer volume = 100;
        private Double angleOffset = 0d;
        private Tag eventTag = Tag.of();
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setHitSound(@Nullable HitSound hitSound) {
            this.hitSound = hitSound;
            return this;
        }

        public Builder setVolume(@Nullable Integer volume) {
            this.volume = volume;
            return this;
        }

        public Builder setAngleOffset(@Nullable Double angleOffset) {
            this.angleOffset = angleOffset;
            return this;
        }

        public Builder setEventTag(@Nullable Tag eventTag) {
            this.eventTag = eventTag;
            return this;
        }

        @Override
        public PlaySound build() {
            return new PlaySound(active, hitSound, volume, angleOffset, eventTag);
        }
    }
}

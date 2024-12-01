package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.selectable.GameSound;
import kr.merutilm.pacl.al.event.selectable.HitSound;


public record SetHitsound(Boolean active,
                          GameSound gameSound,
                          HitSound hitSound,
                          Integer volume) implements Actions {

    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setGameSound(gameSound)
                .setHitSound(hitSound)
                .setVolume(volume);
    }

    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private GameSound gameSound = GameSound.HITSOUND;
        private HitSound hitSound = HitSound.KICK;
        private Integer volume = 100;

        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setGameSound(@Nullable GameSound gameSound) {
            this.gameSound = gameSound;
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

        @Override
        public SetHitsound build() {
            return new SetHitsound(active, gameSound, hitSound, volume);
        }
    }
}

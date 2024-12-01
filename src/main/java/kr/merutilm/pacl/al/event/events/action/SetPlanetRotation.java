package kr.merutilm.pacl.al.event.events.action;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.pacl.al.event.selectable.EasePartBehavior;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record SetPlanetRotation(Boolean active,
                                Ease ease,
                                Integer easeParts,
                                EasePartBehavior easePartBehavior) implements Actions {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setEase(ease)
                .setEaseParts(easeParts)
                .setEasePartBehavior(easePartBehavior);
    }

    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private Ease ease = Ease.LINEAR;
        private Integer easeParts = 1;
        private EasePartBehavior easePartBehavior = EasePartBehavior.MIRROR;

        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setEase(@Nullable Ease ease) {
            this.ease = ease;
            return this;
        }

        public Builder setEaseParts(@Nullable Integer easeParts) {
            this.easeParts = easeParts;
            return this;
        }

        public Builder setEasePartBehavior(@Nullable EasePartBehavior easePartBehavior) {
            this.easePartBehavior = easePartBehavior;
            return this;
        }

        @Override
        public SetPlanetRotation build() {
            return new SetPlanetRotation(active, ease, easeParts, easePartBehavior);
        }
    }
}

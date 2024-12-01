package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record Hide(Boolean active,
                   Boolean hideJudgment,
                   Boolean hideTileIcon) implements Actions {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setHideJudgment(hideJudgment)
                .setHideTileIcon(hideTileIcon);
    }

    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private Boolean hideJudgment = false;
        private Boolean hideTileIcon = false;
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setHideJudgment(@Nullable Boolean hideJudgment) {
            this.hideJudgment = hideJudgment;
            return this;
        }

        public Builder setHideTileIcon(@Nullable Boolean hideTileIcon) {
            this.hideTileIcon = hideTileIcon;
            return this;
        }


        @Override
        public Hide build() {
            return new Hide(active, hideJudgment, hideTileIcon);
        }
    }
}

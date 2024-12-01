package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record SetConditionalEvents(Boolean active,
                                   String perfectTag,
                                   String earlyPerfectTag,
                                   String latePerfectTag,
                                   String veryEarlyTag,
                                   String veryLateTag,
                                   String tooEarlyTag,
                                   String tooLateTag,
                                   String lossTag) implements Actions {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setPerfectTag(perfectTag)
                .setEarlyPerfectTag(earlyPerfectTag)
                .setLatePerfectTag(latePerfectTag)
                .setVeryEarlyTag(veryEarlyTag)
                .setVeryLateTag(veryLateTag)
                .setTooEarlyTag(tooEarlyTag)
                .setTooLateTag(tooLateTag)
                .setLossTag(lossTag);
    }

    public static final String NONE = "NONE";

    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private String perfectTag = NONE;
        private String earlyPerfectTag = NONE;
        private String latePerfectTag = NONE;
        private String veryEarlyTag = NONE;
        private String veryLateTag = NONE;
        private String tooEarlyTag = NONE;
        private String tooLateTag = NONE;
        private String lossTag = NONE;

        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setPerfectTag(String perfectTag) {
            this.perfectTag = perfectTag;
            return this;
        }
        public Builder setEarlyPerfectTag(String earlyPerfectTag) {
            this.earlyPerfectTag = earlyPerfectTag;
            return this;
        }

        public Builder setLatePerfectTag(String latePerfectTag) {
            this.latePerfectTag = latePerfectTag;
            return this;
        }

        public Builder setVeryEarlyTag(String veryEarlyTag) {
            this.veryEarlyTag = veryEarlyTag;
            return this;
        }

        public Builder setVeryLateTag(String veryLateTag) {
            this.veryLateTag = veryLateTag;
            return this;
        }

        public Builder setTooEarlyTag(String tooEarlyTag) {
            this.tooEarlyTag = tooEarlyTag;
            return this;
        }

        public Builder setTooLateTag(String tooLateTag) {
            this.tooLateTag = tooLateTag;
            return this;
        }

        public Builder setLossTag(String lossTag) {
            this.lossTag = lossTag;
            return this;
        }

        @Override
        public SetConditionalEvents build() {
            return new SetConditionalEvents(active, perfectTag, earlyPerfectTag, latePerfectTag, veryEarlyTag, veryLateTag, tooEarlyTag, tooLateTag, lossTag);
        }
    }
}

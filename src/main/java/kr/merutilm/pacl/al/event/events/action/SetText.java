package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.events.AngleOffsetEvent;
import kr.merutilm.pacl.al.event.events.AngleOffsetEventBuilder;
import kr.merutilm.pacl.al.event.events.EventTagEvent;
import kr.merutilm.pacl.al.event.events.EventTagEventBuilder;
import kr.merutilm.pacl.al.event.struct.Tag;


public record SetText(Boolean active,
                      String decText,
                      Tag tag,
                      Double angleOffset,
                      Tag eventTag) implements Actions, AngleOffsetEvent, EventTagEvent {

    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setDecText(decText)
                .setTag(tag)
                .setAngleOffset(angleOffset)
                .setEventTag(eventTag);
    }

    public static final class Builder implements ActionsBuilder, AngleOffsetEventBuilder, EventTagEventBuilder {

        private Boolean active;
        private String decText = "Text";
        private Tag tag = Tag.of();
        private Double angleOffset = 0d;
        private Tag eventTag = Tag.of();

        @Override
        public Builder setActive(Boolean active) {
            this.active = active;
            return this;
        }

        public Builder setDecText(@Nullable String decText) {
            this.decText = decText;
            return this;
        }

        public Builder setTag(@Nullable Tag tag) {
            this.tag = tag;
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
        public SetText build() {
            return new SetText(active, decText, tag, angleOffset, eventTag);
        }
    }
}

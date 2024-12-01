package kr.merutilm.pacl.al.event.events.decoration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.selectable.RelativeTo;
import kr.merutilm.pacl.al.event.struct.Tag;

import java.util.LinkedHashMap;
import java.util.Map;


public record UnknownDecorations(String eventType,
                                 Boolean visible,
                                 Boolean locked,
                                 RelativeTo.AddDecoration relativeTo,
                                 LinkedHashMap<String, String> options,
                                 Tag tag
) implements Decorations {

    @Nonnull
    @Override
    public Builder edit() {
        return new Builder(eventType)
                .replaceOptions(options)
                .setVisible(visible)
                .setLocked(locked)
                .setRelativeTo(relativeTo)
                .setTag(tag);
    }

    public static final class Builder implements DecorationsBuilder {
        private final String eventType;
        private Boolean visible = true;
        private Boolean locked = true;
        private RelativeTo.AddDecoration relativeTo = RelativeTo.AddDecoration.TILE;
        private LinkedHashMap<String, String> options;
        private Tag tag = Tag.of();

        public Builder(String eventType) {
            this.eventType = eventType;
        }

        @Override
        public Builder setVisible(Boolean visible) {
            this.visible = visible;
            return this;
        }

        @Override
        public Builder setLocked(Boolean locked) {
            this.locked = locked;
            return this;
        }

        @Override
        public Builder setRelativeTo(RelativeTo.AddDecoration relativeTo) {
            this.relativeTo = relativeTo;
            return this;
        }

        public Builder replaceOptions(@Nonnull Map<String, String> options) {
            this.options = new LinkedHashMap<>(options);
            return this;
        }

        @Override
        public Builder setTag(@Nullable Tag tag) {
            this.tag = tag;
            return this;
        }

        @Override
        public UnknownDecorations build() {
            return new UnknownDecorations(eventType, visible, locked, relativeTo, options, tag);
        }


    }

}

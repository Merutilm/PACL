package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.Map;


public record UnknownActions(Boolean active,
                             String eventType,
                             LinkedHashMap<String, String> options
) implements Actions {
    @Nonnull
    @Override
    public Builder edit() {
        return new Builder(eventType)
                .replaceOptions(options);
    }

    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private final String eventType;
        private LinkedHashMap<String, String> options;

        public Builder(String eventType) {
            this.eventType = eventType;
        }

        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder replaceOptions(@Nonnull Map<String, String> options) {
            this.options = new LinkedHashMap<>(options);
            return this;
        }

        @Override
        public UnknownActions build() {
            return new UnknownActions(active, eventType, options);
        }


    }

}

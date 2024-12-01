package kr.merutilm.pacl.al.event.events.decoration;

import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.events.EventBuilder;
import kr.merutilm.pacl.al.event.selectable.RelativeTo;
import kr.merutilm.pacl.al.event.struct.Tag;

public interface DecorationsBuilder extends EventBuilder {
    DecorationsBuilder setVisible(@Nullable Boolean visible);
    DecorationsBuilder setLocked(@Nullable Boolean locked);
    DecorationsBuilder setRelativeTo(@Nullable RelativeTo.AddDecoration relativeTo);
    DecorationsBuilder setTag(@Nullable Tag tag);
    Decorations build();
}

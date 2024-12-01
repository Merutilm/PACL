package kr.merutilm.pacl.al.event.events.decoration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.events.Event;
import kr.merutilm.pacl.al.event.selectable.RelativeTo;
import kr.merutilm.pacl.al.event.struct.Tag;

public interface Decorations extends Event {
    @Nullable
    Boolean visible();
    @Nullable
    Boolean locked();
    @Nullable
    RelativeTo.AddDecoration relativeTo();
    @Nullable
    Tag tag();

    @Nonnull
    @Override
    DecorationsBuilder edit();
}

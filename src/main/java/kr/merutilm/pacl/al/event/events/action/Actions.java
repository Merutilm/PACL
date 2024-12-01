package kr.merutilm.pacl.al.event.events.action;

import javax.annotation.Nonnull;

import kr.merutilm.pacl.al.event.events.Event;

public interface Actions extends Event {
    @Nonnull
    @Override
    ActionsBuilder edit();

    Boolean active();
}

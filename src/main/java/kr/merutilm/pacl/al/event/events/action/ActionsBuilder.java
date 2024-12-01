package kr.merutilm.pacl.al.event.events.action;


import javax.annotation.Nullable;

import kr.merutilm.pacl.al.event.events.EventBuilder;

public interface ActionsBuilder extends EventBuilder {

    Actions build();

    ActionsBuilder setActive(@Nullable Boolean active);
}

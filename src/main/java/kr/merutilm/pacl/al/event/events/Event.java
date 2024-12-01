package kr.merutilm.pacl.al.event.events;


import javax.annotation.Nonnull;

import kr.merutilm.pacl.al.event.events.action.*;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;
import kr.merutilm.pacl.al.event.events.decoration.AddText;

import java.util.Optional;

public interface Event {
    @Nonnull
    static Event copyOf(@Nonnull Event event) {
        return event.edit().build();
    }

    @Nonnull
    default String eventType() {
        return getClass().getSimpleName();
    }

    default boolean isActive(){
        return !(this instanceof Actions a) || Optional.ofNullable(a.active()).orElse(true);
    }

    default boolean eventTypeEquals(@Nonnull Class<? extends Event> eventClass) {
        return this.getClass() == eventClass;
    }

    /**
     * 해당 이벤트가 타이밍에 관련되어있는지 확인합니다.
     */
    default boolean isRelatedToTiming() {
        return eventTypeEquals(SetSpeed.class) ||
               eventTypeEquals(Twirl.class) ||
               eventTypeEquals(Pause.class) ||
               eventTypeEquals(Hold.class) ||
               eventTypeEquals(MultiPlanet.class);
    }

    /**
     * 한 타일에 해당 이벤트가 여러개 들어갈 수 있는지 검사합니다.
     */
    default boolean canDuplicated() {
        return !eventTypeEquals(Twirl.class) &&
               !eventTypeEquals(Pause.class) &&
               !eventTypeEquals(Hold.class) &&
               !eventTypeEquals(MultiPlanet.class) &&
               !eventTypeEquals(Bookmark.class) &&
               !eventTypeEquals(CheckPoint.class) &&
               !eventTypeEquals(Hide.class) &&
               !eventTypeEquals(SetPlanetRotation.class) &&
               !eventTypeEquals(UnknownActions.class) &&
               !eventTypeEquals(AnimateTrack.class) &&
               !eventTypeEquals(PositionTrack.class) &&
               !eventTypeEquals(ColorTrack.class) &&
               !eventTypeEquals(ScaleMargin.class) &&
               !eventTypeEquals(SetHitsound.class) &&
               !eventTypeEquals(AutoPlayTiles.class) &&
               !eventTypeEquals(SetConditionalEvents.class) &&
               !eventTypeEquals(ScaleRadius.class);
    }

    default boolean isVFX(){
        return eventTypeEquals(MoveCamera.class) ||
               eventTypeEquals(MoveTrack.class) ||
               eventTypeEquals(AddDecoration.class) ||
               eventTypeEquals(AddText.class) ||
               eventTypeEquals(MoveDecorations.class) ||
               eventTypeEquals(RepeatEvents.class) ||
               eventTypeEquals(SetFilter.class);
    }

    @Nonnull
    EventBuilder edit();
}

package kr.merutilm.pacl.data;


import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.pacl.al.event.events.AngleOffsetEvent;
import kr.merutilm.pacl.al.event.events.DurationEvent;
import kr.merutilm.pacl.al.event.events.Event;

import java.util.List;

record FrameGenerator(CustomLevel level) {
    public <E extends Event> void generate(List<EIN.EventData> listedAction,
                                           Class<E> targetEventClazz,
                                           double traveled,
                                           Consumer<E> function
    ) {

        if (listedAction.isEmpty()) {
            return;
        }

        Event firstEvent = listedAction.get(0).event();
        if(firstEvent.getClass() != targetEventClazz){
            throw new IllegalArgumentException("Required " + targetEventClazz.getSimpleName() + ", but provided " + firstEvent.eventType() + ".");
        }

        for (int i = 0; i < listedAction.size() && level.getTraveledAngle(listedAction.get(i).eventID().floor(), listedAction.get(0).event() instanceof AngleOffsetEvent ? ((AngleOffsetEvent) listedAction.get(i).event()).angleOffset() : 0) <= traveled; i++) {
            EIN.EventData currentListAction = listedAction.get(i);
            E event = targetEventClazz.cast(currentListAction.event());
            int floor = currentListAction.eventID().floor();
            double startTraveled = level.getTraveledAngle(floor, event instanceof AngleOffsetEvent e ? e.angleOffset() : 0);
            double endTraveled = level.getTraveledAngle(floor, (event instanceof AngleOffsetEvent e ? e.angleOffset() : 0) + (event instanceof DurationEvent<?> e && e.duration() != null ? e.duration().doubleValue() * 180 : 0));
            double ratio = startTraveled == endTraveled ? 1 : Math.min(1, AdvancedMath.getRatio(startTraveled, endTraveled, traveled));
            function.accept(floor, event, ratio);
        }
    }

    interface Consumer<E extends Event> {
        void accept(int floor, E event, double ratio);
    }
}

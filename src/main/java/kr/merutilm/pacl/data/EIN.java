package kr.merutilm.pacl.data;

import kr.merutilm.pacl.al.event.events.Event;
import kr.merutilm.pacl.al.event.struct.EID;


public class EIN {

    private EIN(){

    }
    /**
     * Event 정보
     *
     * @param eventID 주소
     * @param event   이벤트
     */

    public record EventData(
            EID eventID,
            Event event
    ) {
        @Override
        public boolean equals(Object obj) {
            return obj instanceof EventData e && e.event == event;
        }

    }
    public record ABSEventData(
            int floor,
            Event event
    ) {

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ABSEventData e && e.event == event;
        }
    }

}

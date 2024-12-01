package kr.merutilm.pacl.data;

import kr.merutilm.pacl.al.event.events.Event;
import kr.merutilm.pacl.al.event.struct.EID;

/**
 * 현재 이벤트 실행 위치
 */
record EventOffset(Event event, EID eventID, double startOffset, double ratio) {
}

package kr.merutilm.pacl.data;

import kr.merutilm.customswing.CSValueInputGroupPanel;
import kr.merutilm.pacl.al.event.events.EventBuilder;

/**
 * 이벤트 편집기
 */
public record EventEditor(CSValueInputGroupPanel targetPanel,
                   EventBuilder builder,
                   CSTemplates.MutableEID eid) {

}

package kr.merutilm.pacl.data;

import kr.merutilm.customswing.CSToggleButton;
import kr.merutilm.pacl.al.event.events.AngleOffsetEventBuilder;
import kr.merutilm.pacl.al.event.events.Event;
import kr.merutilm.pacl.al.event.events.EventBuilder;

import java.io.Serial;

public final class CSEventToggleButton extends CSToggleButton {
    @Serial
    private static final long serialVersionUID = 4935681503092645856L;

    public CSEventToggleButton(CSAnalysisPanel analysisPanel, CSTimelinePanel.Painter painter, Event event) {
        super(painter.getMainFrame(), event.eventType());
        addLeftClickAction(e -> {
            EventBuilder eventBuilder = event.edit();
            if (painter.getMainFrame().isShiftPressed() && eventBuilder instanceof AngleOffsetEventBuilder a) {
                a.setAngleOffset(painter.getCenterAngleOffset());
            }

            Event toCreate = eventBuilder.build();
            painter.createEventButton(toCreate);
            analysisPanel.openEditor();
        });
    }
}

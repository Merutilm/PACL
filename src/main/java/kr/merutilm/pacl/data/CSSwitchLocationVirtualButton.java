package kr.merutilm.pacl.data;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.util.TaskManager;
import kr.merutilm.pacl.al.event.events.Event;
import kr.merutilm.pacl.al.event.events.action.Actions;
import kr.merutilm.pacl.al.event.events.action.UnknownActions;
import kr.merutilm.pacl.al.event.events.decoration.Decorations;
import kr.merutilm.pacl.al.event.events.decoration.UnknownDecorations;
import kr.merutilm.pacl.al.event.struct.EID;

import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 정보만 저장하는 가상 버튼
 */
public final class CSSwitchLocationVirtualButton {

    private final CSAnalysisPanel analysisPanel;
    private final CSTimelinePanel timelinePanel;
    private ButtonAttribute attribute;
    private CSSwitchLocationButton realButton = null;

    public ButtonAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(ButtonAttribute attribute) {
        this.attribute = attribute;
    }

    /**
     * 가상 버튼 (정보만 있고 표시되지 않는 버튼)
     */
    CSSwitchLocationVirtualButton(CSAnalysisPanel analysisPanel, CSTimelinePanel timelinePanel, ButtonAttribute attribute) {
        this.analysisPanel = analysisPanel;
        this.timelinePanel = timelinePanel;
        this.attribute = attribute;
    }

    /**
     * 가상 버튼을 렌더링하여 시각적으로 보이게 합니다.
     */
    void render(Graphics2D g) {
        if (attribute.isShowing()) {
            if (realButton == null) { //realButton 생성 필요
                createButton();
            }

            realButton.setButtonAttribute(attribute);
            realButton.refresh(g);
        }
    }

    /**
     * 실제 버튼 생성
     */
    private void createButton() {

        kr.merutilm.pacl.al.event.events.Event event = attribute.getEventInfo().event();

        //템플릿 불러오기
        realButton = new CSSwitchLocationButton(timelinePanel.getPainter(), attribute, CSTemplates.getEventBaseColor(event));

        AtomicReference<Thread> colorAnimation = new AtomicReference<>(new Thread(() -> {
        }));

        //새로 불러오는 레벨이 아닐 때, 레벨 새로고침
        timelinePanel.getPainter().reloadLevelIfChanged();
        //스위치 생성
        timelinePanel.getPainter().getLinkedButtons().link(this);
        if (!(event instanceof UnknownDecorations || event instanceof UnknownActions)) {

            realButton.addSelectAction(a -> colorAnimation.set(TaskManager.runTask(() -> {
                try {

                    for (long i = 0; i < Long.MAX_VALUE; i++) {
                        if (realButton.isMarked()) {
                            long duration = 2500;
                            realButton.colorAnimation(80, 80, 120, 150, 150, 240, duration);
                            realButton.colorAnimation(150, 150, 240, 80, 80, 120, duration);
                        } else {
                            long duration = 1500;
                            realButton.colorAnimation(60, 120, 60, 120, 240, 120, duration);
                            realButton.colorAnimation(120, 240, 120, 60, 120, 60, duration);
                        }
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            })));

            realButton.addDeselectAction(a -> {
                colorAnimation.get().interrupt();
                a.setMergerColor(HexColor.WHITE);
                analysisPanel.closeEditor(attribute.getEventInfo().event());
            });
        }
    }

    /**
     * 해당 버튼을 선택합니다.
     */
    void select() {
        if (realButton == null) { //realButton 생성 필요
            createButton();
        }
        realButton.select();
    }

    void resetEditor() {
        if (realButton != null) {
            realButton.setEditor(null);
        }
    }

    /**
     * 실제 버튼 얻기 / 없으면 생성
     */
    public CSSwitchLocationButton getRealButton() {
        if (realButton == null) {
            createButton();
        }
        return realButton;
    }

    /**
     * 에디터가 열린 상태에서 값이 수정된경우, 위치 정보를 새로고침할 수 있습니다.
     */
    void refreshIfChangedOnEditor() {

        //에디터가 열린 상태이므로 Nonnull
        assert realButton.getEditor() != null;

        CustomLevel level = ((CSMainFrame) timelinePanel.getMainFrame()).getEditor().level();
        Event e = getRealButton().getEditor().builder().build();
        EID id = getRealButton().getEditor().eid().eventID;
        CSTimelinePanel.Painter painter = timelinePanel.getPainter();
        painter.eventComparatorButtonMap.remove(new EventComparator(attribute.getEventInfo().event()));
        painter.eventComparatorButtonMap.put(new EventComparator(e), this);
        analysisPanel.setCurrentlyEditingEvent(e);

        if (e instanceof Actions actions) {
            level.setAction(getRealButton().getEditor().eid().eventID, actions);
        }
        if (e instanceof Decorations decorations) {
            level.setDecoration(getRealButton().getEditor().eid().eventID, decorations);
        }

        timelinePanel.getPainter().reloadLevelIfChanged();
        painter.createModifiedLog("Event Value Changed : " + e.eventType());

        timelinePanel.getPainter().createIfAbsent(id, e);
        timelinePanel.runTimelineFixedAction();
    }

    /**
     * 에디터를 열지 않은 상태에서 이벤트 값의 수정이 일어날 경우 해당 메서드를 수행하여 에디터를 새로고침할 수 있습니다.<p>
     * 에디터가 새로 생성되므로, 닫힌 상태에서 실행합니다.
     */
    void refreshIfChangedRemotely(EID address, Event event) {
        CustomLevel level = ((CSMainFrame) timelinePanel.getMainFrame()).getEditor().level();
        realButton.setEditor(null); //버튼 속성 변경으로, 에디터 새로 생성

        if (event instanceof Actions a) {
            level.setAction(address, a);
        }
        if (event instanceof Decorations d) {
            level.setDecoration(address, d);
        }

        timelinePanel.getPainter().eventComparatorButtonMap.put(new EventComparator(event), this);
        timelinePanel.getPainter().createIfAbsent(address, event);
    }

}

package kr.merutilm.pacl.data;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.util.TaskManager;
import kr.merutilm.customswing.*;
import kr.merutilm.pacl.al.event.events.Event;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.Serial;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class CSAnalysisPanel extends CSPanel {
    @Serial
    private static final long serialVersionUID = -8668530194797057726L;
    private transient CSTimelinePanel timelinePanel;
    private transient CSKeyFrameViewer keyFrameViewer;
    private transient CSDecorationViewer decorationViewer;
    private transient CSSettingsPanel settingsPanel;

    private boolean generated = false;
    /**
     * 단독 선택된 Event 을 저장함.
     * 편집할때 사용됩니다
     */
    private transient Event currentlyEditingEvent = null;

    private final Map<AnalysisPanelType, Boolean> analysisPanelEnabledMap = new EnumMap<>(AnalysisPanelType.class);

    enum AnalysisPanelType {
        DECORATION, KEYFRAME, SETTINGS
    }

    /**
     * 패널 애니메이션 기간
     */
    public static final int PANEL_ANIMATE_DURATION = 400;

    CSAnalysisPanel(CSMainFrame master) {
        super(master);
        setLayout(null);
        setBackground(new Color(20, 20, 20));
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        closeEditor();
        if(isGenerated()){
            keyFrameViewer.setBounds(0, y, w, h);
            decorationViewer.setBounds(0, y, w, h);
            settingsPanel.setBounds(0, y, w, h);
            hideToolPanels();
        }
    }

    public void setTimeline(@Nonnull CSTimelinePanel timelinePanel) {

        this.timelinePanel = timelinePanel;

        this.keyFrameViewer = new CSKeyFrameViewer(((CSMainFrame) getMainFrame()), timelinePanel);
        this.decorationViewer = new CSDecorationViewer(((CSMainFrame) getMainFrame()), this, timelinePanel);
        this.settingsPanel = new CSSettingsPanel(((CSMainFrame) getMainFrame()), timelinePanel);

        add(keyFrameViewer);
        add(decorationViewer);
        add(settingsPanel);

        generated = true;
    }

    public boolean isGenerated() {
        return generated;
    }

    /**
     * 편집기 열기
     */
    public void openEditor() {

        if (toolPanelShowing()) {
            return;
        }

        List<CSSwitchLocationVirtualButton> buttons = timelinePanel.getPainter().getLinkedButtons().getSelectedButtons();
        if (buttons.size() == 1) {
            CSSwitchLocationVirtualButton virtualButton = buttons.get(0);
            CSSwitchLocationButton button = virtualButton.getRealButton();
            currentlyEditingEvent = virtualButton.getAttribute().getEventInfo().event();

            if (button.getEditor() == null) {
                button.setEditor(CSTemplates.fromEvent(((CSMainFrame) getMainFrame()), currentlyEditingEvent, new CSTemplates.MutableEID(virtualButton.getAttribute().getEventInfo().eventID())));
                button.getEditor().targetPanel().addPropertyChangedAction(() -> {
                    virtualButton.refreshIfChangedOnEditor();
                    repaint();
                });

            }
            button.getEditor().targetPanel().moveAnimation(0, new Point2D(0, -button.getEditor().targetPanel().getHeight()), Ease.OUT_CUBIC.fun());
            button.getEditor().targetPanel().setVisible(true);
            button.getEditor().targetPanel().moveAnimation(PANEL_ANIMATE_DURATION, Point2D.ORIGIN, Ease.OUT_CUBIC.fun());
        }
    }

    public void closeEditor() {
        closeEditor(currentlyEditingEvent);
    }

    /**
     * 편집기 모두 닫기
     */
    public void closeEditor(Event event) {

        ((CSMainFrame) getMainFrame()).setHotKeyAllowed(true);
        CSTimelinePanel.Painter painter = timelinePanel.getPainter();
        CSSwitchLocationVirtualButton virtualButton = painter.getEventButton(event);
        if (event == null || virtualButton == null) {
            return;
        }

        CSSwitchLocationButton button = virtualButton.getRealButton();
        EventEditor editor = button.getEditor();
        if (editor != null) {

            editor.targetPanel().closeAllOpenedInputs();
            editor.targetPanel().moveAnimation(PANEL_ANIMATE_DURATION, new Point2D(0, -editor.targetPanel().getHeight()), Ease.OUT_CUBIC.fun());
            TaskManager.runTask(editor.targetPanel()::closeAllOpenedInputs, PANEL_ANIMATE_DURATION);
        }
        currentlyEditingEvent = null;
    }

    public CSKeyFrameViewer getKeyFrameViewer() {
        return keyFrameViewer;
    }

    public CSDecorationViewer getDecorationViewer() {
        return decorationViewer;
    }

    public void setCurrentlyEditingEvent(Event currentlyEditingEvent) {
        this.currentlyEditingEvent = currentlyEditingEvent;
    }

    public Event getCurrentlyEditingEvent() {
        return currentlyEditingEvent;
    }

    void changePanelShowing(AnalysisPanelType type) {
        if (timelinePanel.getPainter().isLoading()) {
            timelinePanel.getPainter().sendWaitMessage();
            return;
        }

        if (Boolean.FALSE.equals(analysisPanelEnabledMap.computeIfAbsent(type, e -> false))) {
            hideToolPanels();
        }

        analysisPanelEnabledMap.put(type, !analysisPanelEnabledMap.get(type));

        renderPanel(type);

    }

    void renderPanel(AnalysisPanelType type) {

        boolean show = analysisPanelEnabledMap.computeIfAbsent(type, e -> false);

        if (show) {
            closeEditor();
        } else {
            openEditor();
        }

        switch (type) {

            case KEYFRAME -> {
                timelinePanel.tryRefreshFrameAnalyzerPanel();
                List<CSSwitchLocationVirtualButton> buttons = timelinePanel.getPainter().getLinkedButtons().getSelectedButtons();
                if (show) {
                    keyFrameViewer.setOpen(true);
                    showToolPanel(keyFrameViewer);
                    if (buttons.size() == 1) {
                        EIN.EventData data = buttons.iterator().next().getAttribute().getEventInfo();
                        if (data.event() instanceof AddDecoration) {
                            keyFrameViewer.getPainter().refresh(data);
                        } else {
                            keyFrameViewer.getPainter().update();
                        }
                    } else {
                        keyFrameViewer.getPainter().update();
                    }
                } else {
                    hideToolPanel(keyFrameViewer);
                    keyFrameViewer.setOpen(false);
                }
            }
            case SETTINGS -> {
                if (show) {
                    settingsPanel.reload();
                    showToolPanel(settingsPanel);
                } else {
                    hideToolPanel(settingsPanel);
                    CSValueInputGroupPanel groupPanel = settingsPanel.getGroupPanel();
                    if(groupPanel != null){
                        groupPanel.closeAllOpenedInputs();
                    }
                }
            }
            case DECORATION -> {
                timelinePanel.tryRefreshFrameAnalyzerPanel();
                if (show) {
                    decorationViewer.setOpen(true);
                    showToolPanel(decorationViewer);
                    decorationViewer.getPainter().update();

                } else {
                    hideToolPanel(decorationViewer);
                    decorationViewer.setOpen(false);
                }
            }
        }
    }

    private void showToolPanel(CSAnimatablePanel p) {
        p.moveAnimation(PANEL_ANIMATE_DURATION, new Point2D(0, ((CSMainFrame) getMainFrame()).analysisPanel().getHeight() - (double) p.getHeight()), Ease.OUT_CUBIC.fun());
        p.setEnabled(true);
    }

    private void hideToolPanel(CSAnimatablePanel p) {
        p.moveAnimation(PANEL_ANIMATE_DURATION, new Point2D(0, ((CSMainFrame) getMainFrame()).analysisPanel().getHeight()), Ease.OUT_CUBIC.fun());
        p.setEnabled(false);
    }

    void hideToolPanels() {
        for (AnalysisPanelType analysisPanelType : analysisPanelEnabledMap.keySet()) {
            if (Boolean.TRUE.equals(analysisPanelEnabledMap.computeIfAbsent(analysisPanelType, e -> false))) {
                changePanelShowing(analysisPanelType);
            }
        }
    }

    boolean toolPanelShowing() {
        return analysisPanelEnabledMap.containsValue(true);
    }
}

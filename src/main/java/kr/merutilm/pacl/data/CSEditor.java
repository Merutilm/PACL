package kr.merutilm.pacl.data;

import kr.merutilm.base.struct.RectBounds;
import kr.merutilm.customswing.CSPanel;

import java.awt.*;
import java.io.Serial;

class CSEditor extends CSPanel {
    @Serial
    private static final long serialVersionUID = 2552518968838893332L;
    private final CSTimelinePanel timelinePanel;
    private final CSPreviewPanel previewPanel;
    private final CSAnalysisPanel analysisPanel;

    public CSTimelinePanel timelinePanel() {
        return timelinePanel;
    }

    public CSPreviewPanel previewPanel() {
        return previewPanel;
    }

    public CSAnalysisPanel analysisPanel() {
        return analysisPanel;
    }

    CSEditor(CSMainFrame master, CSFunctionPanel functionPanel) {
        super(master);

        setLayout(null);
        setBackground(new Color(50, 50, 50));

        analysisPanel = new CSAnalysisPanel(master);
        previewPanel = new CSPreviewPanel(master);
        timelinePanel = new CSTimelinePanel(master, this, functionPanel);

        previewPanel.setTimeline(timelinePanel);
        analysisPanel.setTimeline(timelinePanel);

        add(timelinePanel);
        add(previewPanel);
        add(analysisPanel);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        RectBounds timelinePanelRectBounds = new RectBounds(0, 0, w, h / 2);
        RectBounds previewRectBounds = new RectBounds(0, timelinePanelRectBounds.endY(), h * 8 / 9, h);
        RectBounds editorAnalysisPanelRectBounds = new RectBounds(previewRectBounds.endX(), timelinePanelRectBounds.endY(), w, h);

        analysisPanel.setBounds(editorAnalysisPanelRectBounds.convertToRectangle());
        previewPanel.setBounds(previewRectBounds.convertToRectangle());
        timelinePanel.setBounds(timelinePanelRectBounds.convertToRectangle());

    }
}

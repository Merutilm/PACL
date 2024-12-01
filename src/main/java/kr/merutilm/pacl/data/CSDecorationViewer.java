package kr.merutilm.pacl.data;

import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.struct.Range;
import kr.merutilm.base.struct.RectBounds;
import kr.merutilm.customswing.*;
import kr.merutilm.pacl.al.event.events.Event;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;
import kr.merutilm.pacl.al.event.selectable.MaskingType;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.List;
import java.util.*;

final class CSDecorationViewer extends CSAnimatablePanel implements CSDrawableParent<CSDecorationViewer.Painter> {
    @Serial
    private static final long serialVersionUID = -5732006863683636952L;
    private boolean visibleOnly = false;
    private boolean open = false;
    private final Painter painter;
    private final CSToolbarPanel toolbar;

    CSDecorationViewer(CSMainFrame master, CSAnalysisPanel analysisPanel, CSTimelinePanel timelinePanel) {
        super(master);

        setBackground(new Color(12, 12, 12));
        setEnabled(false);




        painter = new Painter(master, analysisPanel, timelinePanel, this);

        toolbar = new CSToolbarPanel(master);
        toolbar.addSwitchToolbar("Visible Only", 0, this::setVisibleOnly, visibleOnly);

        add(painter);
        add(toolbar);

    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        int amount = toolbar.getButtons().size();
        for (CSButton button : toolbar.getButtons()) {
            int bw = getWidth() / amount;
            button.setSize(bw, button.getHeight());
        }


        painter.setBounds(0, 0, w, h - CSButton.BUTTON_HEIGHT);
        toolbar.setBounds(0, h - CSButton.BUTTON_HEIGHT, w, CSButton.BUTTON_HEIGHT);
    }

    public boolean isOpened() {
        return open;
    }

    public void setOpen(boolean b) {
        open = b;
    }

    public boolean isVisibleOnly() {
        return visibleOnly;
    }

    public Painter getPainter() {
        return painter;
    }

    public void setVisibleOnly(boolean visibleOnly) {
        this.visibleOnly = visibleOnly;
        painter.update();
    }

    static final class Painter extends CSCoordinatePanel implements CSDrawable {
        @Serial
        private static final long serialVersionUID = -1282087390667960969L;
        private final CSTimelinePanel timelinePanel;
        private final CSDecorationViewer viewer;
        private transient List<VFXDecoration> depthSortedDecorations = new ArrayList<>();
        private boolean loading = false;
        private final List<List<AddDecoration>> depthEventList = new ArrayList<>();

        private static final int CELL_W = 30;
        private static final int CELL_H = 10;

        Painter(CSMainFrame master, CSAnalysisPanel analysisPanel, CSTimelinePanel timelinePanel, CSDecorationViewer viewer) {
            super(master, new Range(0.3, 1.5), new Range(1, 1), new Range(1, 1));


            this.timelinePanel = timelinePanel;
            this.viewer = viewer;
            setBackground(new Color(12, 12, 12));
            setEnabled(false);
            setCoordinate(new Point2D(192, 108));
            setMovingBoundaries(new RectBounds(0, 0, 1000000, 1000000));

            setMovable(true);
            setCanAxisZoom(false);
            setCanZoom(true);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Point2D c = toCoordinate(e.getX(), e.getY());
                    Event event = getEvent(c);
                    if (event != null) {
                        CSSwitchLocationVirtualButton virtualButton = timelinePanel.getPainter().getEventButton(event);
                        if (!virtualButton.getRealButton().isSelecting()) {
                            virtualButton.select();
                            if (master.isControlPressed()) {
                                timelinePanel.getPainter().moveTimeline(virtualButton.getAttribute().getLocationAttribute());
                            }
                            analysisPanel.openEditor();
                        }
                    }
                }
            });

            update();
        }

        public void update() {
            if (!viewer.isEnabled()) {
                return;
            }

            if (!loading) {
                loading = true;

                timelinePanel.tryRefreshFrameAnalyzerPanel();
                FrameAnalyzer analyzer = ((CSMainFrame) getMainFrame()).getAnalyzer();
                this.depthSortedDecorations = ((CSMainFrame) getMainFrame()).getEditor().level().generateListedDecoration()
                        .stream()
                        .filter(e -> e.event() instanceof AddDecoration)
                        .map(e -> analyzer.getDecorationState(analyzer.getFrame(timelinePanel.getPainter().getCoordinate().x()), e))
                        .filter(e -> !viewer.isVisibleOnly() || e.isVisible())
                        .sorted(Comparator.comparing(e -> e.isVisible() ? 0 : 1))
                        .sorted(Comparator.comparing(e -> -e.depth()))
                        .toList();

                loading = false;
            }
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            paintDepthLayer(g2);
        }

        @Override
        protected void setCoordinate(double cx, double cy) {
            super.setCoordinate(cx, cy);
            repaint();
        }

        @Override
        public void setZoom(double zoom) {
            super.setZoom(zoom);
            repaint();
        }

        private void paintDepthLayer(Graphics2D g) {

            int min = -30;

            g.setColor(new Color(150, 150, 150, 20));
            fillRect(g, min, -1000000, 0, 1000000);

            g.setColor(new Color(225, 225, 225, 250));
            drawString(g, "Depth", min, -CELL_H, CELL_H);

            g.setColor(new Color(225, 225, 225, 250));
            drawString(g, "Decoration Tags", 0, -CELL_H, CELL_H);

            g.setStroke(new BasicStroke(resize(1)));
            drawLine(g, min, 0, 1000000, 0);

            g.setColor(new Color(25, 25, 25));
            g.fillRect(0, 0, getWidth(), 2);

            Map<Short, Integer> depthAmountMap = new HashMap<>();
            depthEventList.clear();
            int i = -1;

            for (VFXDecoration decoration : depthSortedDecorations) {
                short depth = decoration.depth();
                int amount = depthAmountMap.computeIfAbsent(depth, e -> 0);
                depthAmountMap.put(depth, amount + 1);

                int x = CELL_W * amount;
                int y = (depthAmountMap.size() - 1) * CELL_H;

                if (amount == 0) {
                    i++;
                    depthEventList.add(new ArrayList<>());

                    g.setColor(new Color(150, 150, 150, 250));
                    drawString(g, Short.toString(depth), min, y, CELL_H);
                    g.setColor(new Color(50, 50, 50, 150));
                    drawLine(g, min, y, 1000000, y);
                }
                depthEventList.get(i).add(decoration.target());

                Color color = getButtonColor(decoration);
                g.setColor(color);
                fillRoundRect(g, x + 1.0, y + 1.0, x + CELL_W - 1.0, y + CELL_H - 1.0, 3);
                g.setColor(color.brighter().brighter());
                fillRoundRect(g, x + 1.0, y + 1.0, x + 4.0, y + CELL_H - 1.0, 3);
                g.setColor(color.brighter().brighter().brighter().brighter());
                String tagStr = decoration.target().tag().toString();
                drawString(g, tagStr.isBlank() ? "[ no tag ]" : tagStr, x + 2.0, y, CELL_H);

            }
        }

        private Color getButtonColor(VFXDecoration decoration) {
            if (Boolean.FALSE.equals(decoration.target().visible())) { //DISABLED
                return new Color(100, 20, 20, 255);
            }
            if (!decoration.isVisible()) { //INVISIBLE
                return new Color(50, 50, 50, 255);
            }
            if (decoration.maskingType() == MaskingType.MASK) { //MASKING
                return new Color(100, 20, 100, 255);
            }
            if (decoration.maskingType() == MaskingType.VISIBLE_INSIDE || decoration.maskingType() == MaskingType.VISIBLE_OUTSIDE) { //MASKING TARGET
                return new Color(100, 100, 20, 255);
            }

            return new Color(20, 100, 100, 255);
        }

        private AddDecoration getEvent(Point2D coordinate) {
            int x = (int) coordinate.x();
            int y = (int) coordinate.y();

            if (y < 0 || x < 0) {
                return null;
            }
            int i = y / CELL_H;
            int j = x / CELL_W;

            if (depthEventList.size() <= i) {
                return null;
            }
            if (depthEventList.get(i).size() <= j) {
                return null;
            }

            return depthEventList.get(i).get(j);
        }

    }
}




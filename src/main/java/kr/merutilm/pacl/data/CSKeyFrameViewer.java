package kr.merutilm.pacl.data;

import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.base.util.StringUtils;
import kr.merutilm.customswing.*;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;

import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class CSKeyFrameViewer extends CSAnimatablePanel implements CSDrawableParent<CSKeyFrameViewer.Painter> {
    @Serial
    private static final long serialVersionUID = -7582183151706411394L;
    private final Painter painter;
    private static final Image START_VALUE = Assets.getAsset(new ImageFile("icons/startValue.png"));
    private static final int MIN_INTERVAL = 10;
    private boolean showVisible = false;
    private boolean showRelativeTo = false;
    private boolean showPosition = false;
    private boolean showRotation = false;
    private boolean showScale = false;
    private boolean showOpacity = false;
    private boolean showDepth = false;
    private boolean showParallax = false;
    private boolean open = false;
    private final CSToolbarPanel toolbar;

    public CSKeyFrameViewer(CSMainFrame master, CSTimelinePanel timelinePanel) {
        super(master);


        this.painter = new Painter(master, this, timelinePanel);

        setBackground(new Color(12, 12, 12));


        toolbar = new CSToolbarPanel(master);
        toolbar.addSwitchToolbar("Parallax", 0, this::setShowParallax, showParallax);
        toolbar.addSwitchToolbar("Depth", 0, this::setShowDepth, showDepth);
        toolbar.addSwitchToolbar("Opacity", 0, this::setShowOpacity, showOpacity);
        toolbar.addSwitchToolbar("Scale", 0, this::setShowScale, showScale);
        toolbar.addSwitchToolbar("Rotation", 0, this::setShowRotation, showRotation);
        toolbar.addSwitchToolbar("Position", 0, this::setShowPosition, showPosition);
        toolbar.addSwitchToolbar("RelativeTo", 0, this::setShowRelativeTo, showRelativeTo);
        toolbar.addSwitchToolbar("Visible", 0, this::setShowVisible, showVisible);

        add(painter);
        add(toolbar);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        int amount = toolbar.getButtons().size();

        for (int i = 0; i < toolbar.getButtons().size(); i++) {
            int bw = getWidth() / amount;
            CSButton button = toolbar.getButtons().get(i);
            if (i >= toolbar.getButtons().size() - 2) {
                bw = (getWidth() - bw * (amount - 2)) / 2;
            }

            button.setSize(bw, button.getHeight());
        }

        painter.setBounds(0, 0, w, h - CSButton.BUTTON_HEIGHT);
        toolbar.setBounds(0, h - CSButton.BUTTON_HEIGHT, w, CSButton.BUTTON_HEIGHT);

    }

    public boolean isOpened() {
        return open && (showVisible || showRelativeTo || showPosition || showRotation || showScale || showOpacity || showDepth || showParallax);
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setShowVisible(boolean showVisible) {
        this.showVisible = showVisible;
        painter.update();
    }

    public void setShowRelativeTo(boolean showRelativeTo) {
        this.showRelativeTo = showRelativeTo;
        painter.update();
    }

    public void setShowPosition(boolean showPosition) {
        this.showPosition = showPosition;
        painter.update();
    }

    public void setShowRotation(boolean showRotation) {
        this.showRotation = showRotation;
        painter.update();
    }

    public void setShowScale(boolean showScale) {
        this.showScale = showScale;
        painter.update();
    }

    public void setShowOpacity(boolean showOpacity) {
        this.showOpacity = showOpacity;
        painter.update();
    }

    public void setShowDepth(boolean showDepth) {
        this.showDepth = showDepth;
        painter.update();
    }

    public void setShowParallax(boolean showParallax) {
        this.showParallax = showParallax;
        painter.update();
    }

    @Override
    public Painter getPainter() {
        return painter;
    }

    public static final class Painter extends CSPanel implements CSDrawable {
        @Serial
        private static final long serialVersionUID = 4613721506563475808L;
        private final CSTimelinePanel timelinePanel;
        private final transient CSKeyFrameViewer keyFrameViewer;
        private transient CustomLevel currentTargetDecorationLevel;
        private transient EIN.EventData currentTargetDecoration;

        Painter(CSMainFrame master, CSKeyFrameViewer keyFrameViewer, CSTimelinePanel timelinePanel) {
            super(master);
            this.timelinePanel = timelinePanel;
            this.keyFrameViewer = keyFrameViewer;
            setBackground(new Color(12, 12, 12));
        }


        public void refresh(EIN.EventData currentTargetDecoration) {
            this.currentTargetDecoration = currentTargetDecoration;
            this.currentTargetDecorationLevel = ((CSMainFrame) getMainFrame()).getEditor().level();
            update();
        }

        public void update() {
            if (((CSMainFrame) getMainFrame()).getEditor().level() != currentTargetDecorationLevel) {
                currentTargetDecoration = null;
            }
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(25, 25, 25));
            g2.fillRect(0, 0, getWidth(), 2);

            g2.setStroke(new BasicStroke(2f));
            g2.setColor(new Color(255, 255, 255, 10));
            g2.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());

            if (currentTargetDecoration != null) {
                List<Drawer> currentMappers = new ArrayList<>();

                if (keyFrameViewer.showVisible) {
                    currentMappers.add(new ColorDrawer("VIS", VFXDecoration::visible, new Color(170, 120, 80)));
                }
                if (keyFrameViewer.showRelativeTo) {
                    currentMappers.add(new ColorDrawer("RLT", VFXDecoration::relativeTo, new Color(220, 150, 100)));
                }

                if (keyFrameViewer.showPosition) {
                    currentMappers.add(new ColorPointDrawer("PS", VFXDecoration::positionOffset, new Color(220, 140, 140)));
                    currentMappers.add(new ColorPointDrawer("PV", VFXDecoration::pivotOffset, new Color(140, 220, 140)));
                    currentMappers.add(new ColorPointDrawer("PO", VFXDecoration::parallaxOffset, new Color(140, 140, 220)));
                }
                if (keyFrameViewer.showRotation) {
                    currentMappers.add(new ColorNumberDrawer("ROT", VFXDecoration::rotationOffset, new Color(170, 80, 170)));
                }
                if (keyFrameViewer.showScale) {
                    currentMappers.add(new ColorPointDrawer("SC", VFXDecoration::scale, new Color(140, 220, 220)));
                }
                if (keyFrameViewer.showOpacity) {
                    currentMappers.add(new ColorNumberDrawer("OPA", VFXDecoration::opacity, new Color(30, 70, 120)));
                }
                if (keyFrameViewer.showDepth) {
                    currentMappers.add(new ColorDrawer("DEP", VFXDecoration::depth, new Color(120, 30, 74)));
                }
                if (keyFrameViewer.showParallax) {
                    currentMappers.add(new ColorPointDrawer("PA", VFXDecoration::parallax, new Color(220, 220, 140)));
                }


                drawKeyFrames((Graphics2D) g, currentMappers);
            }
        }

        private KeyData getKeyData(FrameAnalyzer analyzer, ColorDrawer drawer) {
            VFXDecoration start = VFXDecoration.fromAddDecoration(currentTargetDecoration);
            Object currentValue = drawer.currentMapper().apply(analyzer.getDecorationState(analyzer.getFrame(timelinePanel.getPainter().getCoordinate().x()), currentTargetDecoration));

            return new KeyData(drawer.currentMapper().apply(start).toString(), currentValue.toString());
        }

        private KeyFrameData getKeyFrameData(int startFrame, int endFrame, FrameAnalyzer analyzer, ColorNumberDrawer drawer) {
            List<Double> keyFrames = new ArrayList<>();

            for (int frame = startFrame; frame <= endFrame; frame++) {
                double key = drawer.currentMapper().apply(analyzer.getDecorationState(frame, currentTargetDecoration));
                keyFrames.add(key);
            }

            double max = keyFrames.stream().mapToDouble(e -> e).max().orElse(Double.NaN);
            double min = keyFrames.stream().mapToDouble(e -> e).min().orElse(Double.NaN);

            if (max - min < MIN_INTERVAL) {

                double tMax = max;
                double tMin = min;

                max = (tMax + tMin + MIN_INTERVAL) / 2;
                min = (tMax + tMin - MIN_INTERVAL) / 2;
            }
            Polygon p = new Polygon();

            int prevY;
            for (int i = 0; i < keyFrames.size(); i++) {
                int x = (int) (AdvancedMath.getRatio(0, keyFrames.size() - 1, i) * getWidth());
                prevY = 0;
                int y = generateY(min, max, keyFrames.get(i));
                if (prevY != y && i != 0 && i != keyFrames.size() - 1) {
                    p.addPoint(x, y);
                }

            }

            VFXDecoration start = VFXDecoration.fromAddDecoration(currentTargetDecoration);
            double currentValue = drawer.currentMapper().apply(analyzer.getDecorationState(analyzer.getFrame(timelinePanel.getPainter().getCoordinate().x()), currentTargetDecoration));

            return new KeyFrameData(min, max, drawer.currentMapper().apply(start), currentValue, p);
        }

        private void drawKeyFrames(Graphics2D g, List<Drawer> currentMappers) {
            if (!isEnabled()) {
                return;
            }

            timelinePanel.tryRefreshFrameAnalyzerPanel();

            FrameAnalyzer analyzer = ((CSMainFrame) getMainFrame()).getAnalyzer();

            int startFrame = analyzer.getFrame(timelinePanel.getPainter().toCoordinate(0, 0).x());
            int endFrame = analyzer.getFrame(timelinePanel.getPainter().toCoordinate(timelinePanel.getWidth(), 0).x());

            int fontSize = 10;
            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));


            int offset = 5;
            int ty = 15;
            int imageSize = 10;

            String tag = ((AddDecoration) currentTargetDecoration.event()).tag().toString();

            g.setColor(Color.WHITE);
            drawStringRight(g, "T = " + (tag.isBlank() ? "[ BLANK ]" : tag), fontSize, 0);
            drawStringRight(g, "[ Start Value ]", fontSize, getHeight() - 15);


            for (Drawer drawer : currentMappers) {

                Color drawerColor = drawer.color();
                g.setStroke(new BasicStroke(1f));
                g.setColor(drawerColor.brighter());


                if (drawer instanceof ColorDrawer c) {
                    KeyData k = getKeyData(analyzer, c);

                    drawStringRight(g, drawer.name() + " = " + k.currentValue(), fontSize, ty); //현재값
                    drawStringRight(g, drawer.name() + " = " + k.startValue(), fontSize, getHeight() - ty - 15); //초기값
                }
                if (drawer instanceof ColorNumberDrawer c) {

                    KeyFrameData k = getKeyFrameData(startFrame, endFrame, analyzer, c);

                    offset = drawKeyFrameDataAndGetOffset(g, drawerColor, k, fontSize, imageSize, offset);

                    drawStringRight(g, drawer.name() + " = " + k.currentValue(), fontSize, ty); //현재값
                    drawStringRight(g, drawer.name() + " = " + k.startValue(), fontSize, getHeight() - ty - 15); //초기값
                }

                if (drawer instanceof ColorPointDrawer c) {
                    KeyFrameData kx = getKeyFrameData(startFrame, endFrame, analyzer, c.x());
                    KeyFrameData ky = getKeyFrameData(startFrame, endFrame, analyzer, c.y());

                    offset = drawKeyFrameDataAndGetOffset(g, drawerColor, kx, fontSize, imageSize, offset);
                    offset = drawKeyFrameDataAndGetOffset(g, drawerColor, ky, fontSize, imageSize, offset);

                    drawStringRight(g, drawer.name() + " = [ " + kx.currentValue() + ", " + ky.currentValue() + " ]", fontSize, ty); //현재값
                    drawStringRight(g, drawer.name() + " = [ " + kx.startValue() + ", " + ky.startValue() + " ]", fontSize, getHeight() - ty - 15); //초기값
                }


                ty += 15;

            }
        }

        private int drawKeyFrameDataAndGetOffset(Graphics2D g, Color drawerColor, KeyFrameData k, int fontSize, int imageSize, int offset) {
            g.drawPolyline(
                    k.points().xpoints,
                    k.points().ypoints,
                    k.points().npoints
            );


            g.setColor(drawerColor.brighter());

            String maxStr = String.format("%.1f", k.max());
            String minStr = String.format("%.1f", k.min());

            g.drawString(maxStr, offset, fontSize); //상한값
            g.drawString(minStr, offset, getHeight() - fontSize / 2); //하한값

            int currentY = generateY(k.min(), k.max(), k.currentValue());

            g.setColor(drawerColor.darker());
            g.setStroke(new BasicStroke(0.5f));
            g.drawLine(0, currentY, getWidth(), currentY); // 현재값 가로선

            int sy = generateY(k.min(), k.max(), k.startValue()) - imageSize / 2;

            if (sy >= 0 && sy + imageSize <= getHeight()) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g.drawImage(START_VALUE, offset, sy, imageSize, imageSize, null);
            } //시작값 아이콘

            return offset + StringUtils.getStringPixelLength((maxStr.length() > minStr.length() ? maxStr : minStr) + 2, fontSize);
        }


        private void drawStringRight(Graphics2D g, String s, int fontSize, int y) {
            g.drawString(s, getWidth() - fontSize * 2 - StringUtils.getStringPixelLength(s, fontSize), y + fontSize);
        }

        private int generateY(double min, double max, double value) {
            return (int) ((1 - AdvancedMath.getRatio(min, max, value)) * getHeight());
        }

        private record KeyData(
                String startValue,
                String currentValue
        ) {

        }

        private record KeyFrameData(
                double min,
                double max,
                double startValue,
                double currentValue,
                Polygon points
        ) {

        }

        private record ColorDrawer(
                String name,
                Function<VFXDecoration, Object> currentMapper,
                Color color
        ) implements Drawer {
        }

        private record ColorNumberDrawer(
                String name,
                Function<VFXDecoration, Double> currentMapper,
                Color color
        ) implements Drawer {
        }

        private record ColorPointDrawer(
                String name,
                Function<VFXDecoration, Point2D> currentMapper,
                Color color
        ) implements Drawer {
            private ColorNumberDrawer x() {
                return new ColorNumberDrawer(name, currentMapper.andThen(Point2D::x), color);
            }

            private ColorNumberDrawer y() {
                return new ColorNumberDrawer(name, currentMapper.andThen(Point2D::y), color);
            }
        }

        private interface Drawer {
            String name();

            Color color();
        }
    }
}

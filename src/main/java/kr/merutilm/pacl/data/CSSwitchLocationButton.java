package kr.merutilm.pacl.data;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.base.util.TaskManager;
import kr.merutilm.customswing.CSButton;
import kr.merutilm.pacl.al.event.events.AngleOffsetEvent;
import kr.merutilm.pacl.al.event.events.DurationEvent;
import kr.merutilm.pacl.al.event.events.EaseEvent;
import kr.merutilm.pacl.al.event.events.Event;
import kr.merutilm.pacl.al.event.events.action.*;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;
import kr.merutilm.pacl.al.event.struct.EID;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 타임라인에 실제로 표시될 버튼
 */
public class CSSwitchLocationButton {
    /**
     * 버튼이 들어갈 타임라인
     */
    private final CSTimelinePanel.Painter painter;
    /**
     * 버튼 속성
     */
    private ButtonAttribute buttonAttribute;
    /**
     * 버튼 기본 색상
     */
    private final HexColor baseColor;
    /**
     * 버튼 바 시작 색상
     */
    private HexColor barStartColor;

    /**
     * 버튼 바 끝 색상
     */
    private HexColor barEndColor;
    /**
     * 버튼 합성 색상
     */
    private HexColor mergerColor = HexColor.WHITE;
    /**
     * 버튼의 이름
     */
    private String name;
    /**
     * 현재 선택 중인지 여부
     */
    private boolean select;

    /**
     * 마킹 중인지 여부
     */
    private boolean marked;
    /**
     * 버튼 선택 시 명령
     */
    private final List<Consumer<CSSwitchLocationButton>> selectActions = new ArrayList<>();
    /**
     * 버튼 선택 해제 시 명령
     */
    private final List<Consumer<CSSwitchLocationButton>> deselectActions = new ArrayList<>();

    private static final Image ICON_ANGLE_OFFSET = Assets.getAsset(new ImageFile("icons/angleOffsetIcon.png"));
    private static final Image ICON_DURATION = Assets.getAsset(new ImageFile("icons/durationIcon.png"));
    /**
     * 이벤트 편집기
     */
    private EventEditor editor;

    CSSwitchLocationButton(CSTimelinePanel.Painter painter, ButtonAttribute buttonAttribute, HexColor baseColor) {
        this.painter = painter;
        this.buttonAttribute = buttonAttribute;
        this.baseColor = baseColor;
        refreshBounds();
    }

    public EventEditor getEditor() {
        return editor;
    }

    public void setEditor(EventEditor editor) {
        this.editor = editor;
    }

    /**
     * 버튼 속성 설정
     */
    void setButtonAttribute(ButtonAttribute buttonAttribute) {
        this.buttonAttribute = buttonAttribute;
    }

    /**
     * 기본 색상에 합성할 추가 색상
     */
    void setMergerColor(HexColor color) {
        this.mergerColor = color;
    }

    /**
     * 경계 재설정
     */
    private Rectangle refreshBounds() {
        ButtonAttribute.Location location = buttonAttribute.getLocationAttribute();
        return painter.convert(
                location.startX(),
                location.yLineNumber() * CSButton.BUTTON_HEIGHT,
                location.endX(),
                (location.yLineNumber() + 1) * CSButton.BUTTON_HEIGHT);
    }

    /**
     * 선택 시 실행할 명령
     */
    private void runSelectAction() {
        if (!isSelecting()) {
            select = true;
            selectActions.forEach(f -> f.accept(this));
        }
    }

    /**
     * 선택 해제 시 실행할 명령
     */
    private void runDeselectAction() {
        if (isSelecting()) {
            select = false;
            deselectActions.forEach(f -> f.accept(this));
        }
    }

    void deselect() {
        runDeselectAction();
    }

    void select() {
        runSelectAction();
    }

    void setMarked(boolean marked) {
        this.marked = marked;
    }

    /**
     * 현재 해당 버튼이 선택되어 있으면 true / 아니면 false
     */
    boolean isSelecting() {
        return select;
    }

    boolean isMarked() {
        return marked;
    }

    /**
     * 선택 시 명령 추가
     */
    public void addSelectAction(Consumer<CSSwitchLocationButton> action) {
        selectActions.add(action);
    }

    /**
     * 선택 해제 시 명령 추가
     */
    public void addDeselectAction(Consumer<CSSwitchLocationButton> action) {
        deselectActions.add(action);
    }

    private void decorateButton() {
        EID eid = buttonAttribute.getEventInfo().eventID();
        Event event = buttonAttribute.getEventInfo().event();

        this.barStartColor = HexColor.fromAWT(CSTemplates.getEventBaseColor(event).toAWT().brighter());
        this.barEndColor = null;

        switch (event) {
            case SetFilter setFilter -> {
                if (Boolean.TRUE.equals(setFilter.enabled())) {
                    if (Boolean.TRUE.equals(setFilter.disableOthers())) {
                        this.name = "Filter ON : " + setFilter.filter().toString();
                        this.barStartColor = HexColor.DARK_RED;
                    } else {
                        this.name = "Filter ON : " + setFilter.filter().toString();
                        this.barStartColor = HexColor.DARK_GREEN;
                    }
                } else {
                    if (Boolean.TRUE.equals(setFilter.disableOthers())) {
                        this.name = "Filter OFF";
                        this.barStartColor = HexColor.DARK_RED;
                    } else {
                        this.name = "Filter OFF : " + setFilter.filter().toString();
                        this.barStartColor = HexColor.DARK_GREEN;
                    }
                }
            }
            case Flash flash -> {
                this.name = "Flash : " + flash.startOpacity().intValue() + "% -> " + flash.endOpacity().intValue() + "%";
                this.barStartColor = flash.startColor();
                this.barEndColor = flash.endColor();
            }
            case HallOfMirrors hallOfMirrors -> {
                this.name = "Hall Of Mirrors";
                this.barStartColor = baseColor.blend(HexColor.ColorBlendMode.NORMAL, Boolean.TRUE.equals(hallOfMirrors.enabled()) ? HexColor.DARK_GREEN : HexColor.DARK_RED, 0.5);
            }
            case Bloom bloom -> {
                if (Boolean.TRUE.equals(bloom.enabled())) {
                    this.name = "Bloom ON : " + bloom.threshold().intValue() + ", " + bloom.intensity().intValue();
                } else {
                    this.name = "Bloom OFF";
                }
                this.barStartColor = bloom.color();
            }
            case MoveTrack moveTrack ->
                    this.name = "Move : " + moveTrack.startTile().floor(eid.floor(), ((CSMainFrame) painter.getMainFrame()).getEditor().level().getLength()) + " ~ " + moveTrack.endTile().floor(eid.floor(),((CSMainFrame) painter.getMainFrame()).getEditor().level().getLength());
            case MoveDecorations moveDecorations -> this.name = "Move : " + moveDecorations.tag();
            case AddDecoration addDecoration ->
                    this.name = "Add : " + addDecoration.image() + " | " + addDecoration.tag().toString();

            default -> this.name = event.eventType();
        }
        if (barStartColor != null && barEndColor == null) {
            barEndColor = barStartColor;
        }

        this.name = name.replace("->", "→");
    }

    /**
     * 스위치를 새로고침합니다.
     */
    void refresh(Graphics2D g) {

        Rectangle r = refreshBounds();
        decorateButton();

        HexColor color = baseColor.blend(HexColor.ColorBlendMode.MULTIPLY, mergerColor);

        if (!buttonAttribute.getEventInfo().event().isActive()) {
            color = color.blend(HexColor.ColorBlendMode.NORMAL, HexColor.get(50, 50, 50, 200));
        }

        if (isMarked()) {
            color = color.blend(HexColor.ColorBlendMode.MULTIPLY, HexColor.get(150, 180, 210));
        }


        Color finalColor = color.toAWT();

        // 버튼 위치와 크기 정의하기
        int x = r.x;
        int y = r.y + 1;
        int w = r.width;
        int h = r.height - 2;
        double arc = 6;

        // 바 두께 정의하기
        int barWidth = h / 6;

        // 버튼 그리기
        g.setColor(finalColor.darker());
        g.fillRoundRect(x, y, w, h, painter.resize(arc), painter.resize(arc));
        if (painter.getZoom() < 2) {
            g.fillRect(x + barWidth / 2, y, barWidth / 2, h);
        }
        // 버튼 이름 위치 정의하기
        int strX = r.x + painter.resize(8);
        int strY = r.y + r.height / 2 + Math.min(3, painter.resize(3));


        // 줌이 일정 값 이하일 때, 이벤트에 따라 버튼 디자인하기
        if (painter.getZoom() <= CSTimelinePanel.Painter.DESIGN_BUTTON_ZOOM_LIMIT) {

            Event event = buttonAttribute.getEventInfo().event();
            int iconSize = painter.resize(7);
            float opacity = 0.2f;

            if (event instanceof EaseEvent e) {


                //CONSTANT!!

                double stroke = 1;
                double accuracy = 200;
                int division = 5;

                //________________________________

                FunctionEase ease = e.ease() == null ? Ease.LINEAR.fun() : e.ease().fun();

                g.setStroke(new BasicStroke(painter.resize(stroke)));
                g.setColor(finalColor);

                int gStartX = x + barWidth / 2;
                int gEndX = x + w;
                int gStartY = y + (division - 1) * h / division;
                int gEndY = y + h / division;

                int m = gEndY - gStartY;

                Point prevLocation = new Point(gStartX, gStartY);

                Polygon outP = new Polygon();
                Polygon inP = new Polygon();

                outP.addPoint(prevLocation.x, prevLocation.y);
                inP.addPoint(prevLocation.x, prevLocation.y + painter.resize(stroke));

                for (int i = gStartX; i < gEndX; i += Math.max(1, painter.resize(100 / accuracy))) {

                    int gCurY = (int) (gStartY + m * ease.apply((double) (i - gStartX) / (gEndX - gStartX)));

                    if (prevLocation.y != gCurY) {
                        outP.addPoint(i, gCurY);
                        inP.addPoint(i, gCurY + painter.resize(stroke));

                        prevLocation = new Point(i, gCurY);
                    }

                }
                outP.addPoint(gEndX, gEndY);
                outP.addPoint(gEndX, y + h);
                outP.addPoint(gStartX, y + h);

                inP.addPoint(gEndX, gEndY + painter.resize(stroke));
                inP.addPoint(gEndX, y + h);
                inP.addPoint(gStartX, y + h);

                //가감속 그래프 그리기

                g.setColor(finalColor);
                g.fillPolygon(outP);
                g.setColor(finalColor.darker().darker());
                g.fillPolygon(inP);


                //가감속 시작,끝 값 기준 선 긋기

                g.setColor(new Color(255, 255, 255, 35));
                g.fillRect(gStartX, gStartY + 1, gEndX - gStartX, 1);
                g.fillRect(gStartX, gEndY, gEndX - gStartX, 1);

                //가감속 이름 아래에 쓰기

                g.setColor(new Color(0, 0, 0, 150));
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.min(8, painter.resize(8))));
                g.drawString(e.ease().toString(), strX, y + h - painter.resize(1));

            }
            if (event instanceof AngleOffsetEvent) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g.drawImage(ICON_ANGLE_OFFSET, x + w - iconSize, y + h - iconSize, iconSize, iconSize, painter);
            }
            if (event instanceof DurationEvent) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g.drawImage(ICON_DURATION, x + w - iconSize, y, iconSize, iconSize, painter);
            }
        }
        if (painter.getZoom() < 2) {

            // 바 그리기
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

            // 바 색상 정하기
            Color finalBarStartColor = barStartColor.blend(HexColor.ColorBlendMode.MULTIPLY, mergerColor).toAWT();
            Color finalBarEndColor = barEndColor.blend(HexColor.ColorBlendMode.MULTIPLY, mergerColor).toAWT();


            Paint prevPaint = g.getPaint();

            // 바 그리기
            g.setPaint(new GradientPaint(x, y, finalBarStartColor, x, y + h, finalBarEndColor));
            g.fillRoundRect(x, y, barWidth, h, painter.resize(arc), painter.resize(arc));
            g.setPaint(prevPaint);

            //버튼 이름 그리기
            g.setColor(finalColor.brighter());
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.min(12, painter.resize(12))));
            g.drawString(name, strX, strY);
        }
    }

    /**
     * 버튼 애니메이션
     */
    void colorAnimation(int sr, int sg, int sb, int er, int eg, int eb, long duration) throws InterruptedException {
        TaskManager.animate(duration, e -> {
            int cr = (int) AdvancedMath.ratioDivide(er, sr, e);
            int cg = (int) AdvancedMath.ratioDivide(eg, sg, e);
            int cb = (int) AdvancedMath.ratioDivide(eb, sb, e);
            HexColor current = HexColor.get(cr, cg, cb);
            setMergerColor(current);
        }, Ease.INOUT_SINE.fun());
    }
}

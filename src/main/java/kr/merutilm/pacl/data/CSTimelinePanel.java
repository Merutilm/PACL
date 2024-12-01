package kr.merutilm.pacl.data;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.base.io.BitMapImage;
import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.*;
import kr.merutilm.base.util.*;
import kr.merutilm.customswing.*;
import kr.merutilm.pacl.al.editor.vfx.t2manimtile.VFXGeneratorAnimateTile;
import kr.merutilm.pacl.al.editor.vfx.t2manimtile.attribute.AttributeAnimation;
import kr.merutilm.pacl.al.event.events.*;
import kr.merutilm.pacl.al.event.events.Event;
import kr.merutilm.pacl.al.event.events.action.*;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;
import kr.merutilm.pacl.al.event.events.decoration.AddObject;
import kr.merutilm.pacl.al.event.events.decoration.AddText;
import kr.merutilm.pacl.al.event.events.decoration.Decorations;
import kr.merutilm.pacl.al.event.selectable.Filter;
import kr.merutilm.pacl.al.event.struct.EID;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;

import static kr.merutilm.pacl.data.CSAnalysisPanel.PANEL_ANIMATE_DURATION;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


/**
 * 타임라인
 */
public final class CSTimelinePanel extends CSPanel implements CSDrawableParent<CSTimelinePanel.Painter> {

    @Serial
    private static final long serialVersionUID = 6930675062682539204L;
    private final Painter painter;
    private boolean eventListing = false;
    private boolean frameAnalyzerRefreshed = false;
    /**
     * 이벤트 설치 등 속성 변경 시 호출됩니다
     */
    private final List<Runnable> timelineFixedActions = new ArrayList<>();
    /**
     * 타임라인 이동 시 호출됩니다
     */
    private final List<Runnable> timelineMovedActions = new ArrayList<>();
    /**
     * 레벨 로드 시 호출됩니다
     */
    private final List<Runnable> timelineLevelLoadedActions = new ArrayList<>();
    /**
     * 버튼 선택 시 호출됩니다
     */
    private final List<Runnable> timelineEventSelectedActions = new ArrayList<>();
    private static final int BUTTON_LIST_WIDTH = 150;
    private final CSToolbarPanel toolbar;

    CSTimelinePanel(CSMainFrame master, CSEditor csEditor, CSFunctionPanel functionPanel) {
        super(master);


        // 타임라인 이벤트 설정 ------------------------------------------

        addTimelineFixedAction(() -> {

            CSAnalysisPanel analysisPanel = csEditor.analysisPanel();
            CSPreviewPanel previewPanel = csEditor.previewPanel();
            frameAnalyzerRefreshed = false;

            if ((previewPanel.isOpened() ||
                (analysisPanel.getKeyFrameViewer() != null && analysisPanel.getKeyFrameViewer().isOpened()) ||
                (analysisPanel.getDecorationViewer() != null && analysisPanel.getDecorationViewer().isOpened())
                ) && analysisPanel.isGenerated()) {
                    analysisPanel.getDecorationViewer().getPainter().update();
                    if (analysisPanel.getCurrentlyEditingEvent() instanceof AddDecoration) {
                        analysisPanel.getKeyFrameViewer().getPainter().refresh(getPainter().getCurrentlyEditingEventButton().getAttribute().getEventInfo());
                    } else {
                        analysisPanel.getKeyFrameViewer().getPainter().update();
                    }
                }
            

            previewPanel.reload();
        });

        addTimelineLevelLoadedAction(() -> {

            CSAnalysisPanel analysisPanel = csEditor.analysisPanel();
            CSPreviewPanel previewPanel = csEditor.previewPanel();
            previewPanel.reloadAll();

            frameAnalyzerRefreshed = false;

            if (((analysisPanel.getKeyFrameViewer() != null && analysisPanel.getKeyFrameViewer().isOpened()) ||
                    (analysisPanel.getDecorationViewer() != null && analysisPanel.getDecorationViewer().isOpened())) 
                    && analysisPanel.isGenerated()) {
                    analysisPanel.getKeyFrameViewer().getPainter().update();
                    analysisPanel.getDecorationViewer().getPainter().update();
            }
        });

        AtomicInteger prevFrame = new AtomicInteger(-1);

        addTimelineMovedAction(() -> {
            int currFrame = ((CSMainFrame) getMainFrame()).getAnalyzer().getFrame(getPainter().getCoordinate().x());

            CSAnalysisPanel analysisPanel = csEditor.analysisPanel();
            CSPreviewPanel previewPanel = csEditor.previewPanel();

            if (currFrame != prevFrame.get() && previewPanel.isOpened()) {
                previewPanel.render();
            }
            prevFrame.set(currFrame);

            if (analysisPanel.isGenerated()) {
                analysisPanel.getDecorationViewer().getPainter().update();
                analysisPanel.getKeyFrameViewer().getPainter().update();
            }
        });

        addTimelineEventSelectedAction(() -> {
            CSAnalysisPanel analysisPanel = csEditor.analysisPanel();
            if (analysisPanel.getKeyFrameViewer() != null && analysisPanel.getKeyFrameViewer().isOpened()) {
                csEditor.analysisPanel().renderPanel(CSAnalysisPanel.AnalysisPanelType.KEYFRAME);
            }
        });

        // 타임라인 이벤트 설정 후 인스턴스 생성 ------------------------------------------

        CSAnalysisPanel analysisPanel = csEditor.analysisPanel();
        CSPreviewPanel previewPanel = csEditor.previewPanel();

        this.painter = new Painter(master, this, analysisPanel, functionPanel);
        painter.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                getPainter().refreshDestinationFloorToCenter();
            }
        });

        painter.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                getPainter().refreshDestinationFloorToCenter();
            }
        });


        CSToggleButtonList createButtonList = new CSToggleButtonList(master, new Rectangle(0, 0, BUTTON_LIST_WIDTH, CSToggleButtonList.DEFAULT_LIST_HEIGHT),
                new CSEventToggleButton(analysisPanel, painter, new AddDecoration.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new AddText.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new AddObject.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new AnimateTrack.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new AutoPlayTiles.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new Bloom.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new Bookmark.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new CheckPoint.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new ColorTrack.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new CustomBackground.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new EditorComment.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new Flash.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new HallOfMirrors.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new Hide.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new Hold.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new MoveCamera.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new MoveDecorations.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new MoveTrack.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new MultiPlanet.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new Pause.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new PlaySound.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new PositionTrack.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new RecolorTrack.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new RepeatEvents.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new ScaleMargin.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new ScalePlanets.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new ScaleRadius.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new ScreenScroll.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new ScreenTile.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new SetConditionalEvents.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new SetFilter.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new SetHitsound.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new SetPlanetRotation.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new SetSpeed.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new SetText.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new ShakeScreen.Builder().build()),
                new CSEventToggleButton(analysisPanel, painter, new Twirl.Builder().build())
        );


        toolbar = new CSToolbarPanel(master);
        toolbar.addToggleToolbar("New Event", 90, e -> {

            CSDialog dialog = new CSDialog(master, createButtonList.getWidth(), createButtonList.getHeight());

            createButtonList.setVisible(true);
            dialog.add(createButtonList);
            dialog.setLocation(master.getX() + CSTimelinePanel.this.getX() + master.getWidth() - BUTTON_LIST_WIDTH - 7, master.getY() + CSTimelinePanel.this.getY() + toolbar.getY() - createButtonList.getHeight() + 60);
            dialog.setup();

        });
        toolbar.addToggleToolbar("Reload Decoration Files", 120,
                e -> {

                    CSConfirmDialog dialog = new CSConfirmDialog(getMainFrame(), "Reload Decoration Files", 150,
                            """
                                    <html><body style='text-align:center;'><body>Are you sure you want to reload decoration files?
                                    This is useful when the decoration file name has changed.</font></body></html>
                                    """
                    );
                    dialog.addRedOption("Cancel", a -> {
                    });
                    dialog.addGreenOption("Reload", a -> {
                        if (painter.isLoading()) {
                            painter.sendWaitMessage();
                            return;
                        }
                        previewPanel.reloadAll();
                        painter.sendMessage("All of Decorations have been Reloaded.");
                    });

                    dialog.setup();


                }
        );

        toolbar.addToggleToolbar("Set Resolution", 100,
                e -> new CSSingleDialog<>(getMainFrame(),
                        "Set Resolution",
                        "Width",
                        previewPanel.getXRes(),
                        Integer::parseInt,
                        previewPanel::setXRes
                )
        );

        toolbar.addSwitchToolbar("Pre-Render Mode", 100,
                previewPanel::setPreRenderMode,
                previewPanel.isPreRenderMode()
        );

        toolbar.addSwitchToolbar("Forced Rendering Mode", 120,
                (Consumer<CSSwitchButton>) e -> {
                    if (e.isOn()) {
                        CSConfirmDialog dialog = new CSConfirmDialog(getMainFrame(), "Forced Rendering Mode", 150,
                                "<html><body style='text-align:center;'><body>Are you sure you want to change to Forced Render Mode? " +
                                "It can be <font color=#ff5555>extremely slow</font> because it forces rendering at a set resolution within <font color=#ff5555>just one frame.</font> " +
                                "The resolution of the current preview screen is <font color=#55ff55>" + previewPanel.getXRes() + "</font>.</body></html>"
                        );
                        dialog.addGreenOption("Do Not Change", a -> {
                        });
                        dialog.addRedOption("Change Anyway", a -> previewPanel.setForceRenderMode(true));
                        dialog.addDisposeAction(() -> e.setOn(previewPanel.isForceRenderMode()));
                        dialog.setup();

                    } else {
                        previewPanel.setForceRenderMode(false);
                    }
                },
                previewPanel.isForceRenderMode()
        );

        toolbar.addSwitchToolbar("Preview Color Filters", 120,
                previewPanel::setShowColorFilter,
                previewPanel.isColorFilterRendering());
        toolbar.addSwitchToolbar("Preview Foreground", 120,
                previewPanel::setShowForeground,
                previewPanel.isForegroundRendering());
        toolbar.addSwitchToolbar("Preview Video Background", 120,
                previewPanel::setShowVideoBackground,
                previewPanel.isVideoBackgroundRendering());
        toolbar.addSwitchToolbar("Preview Background", 120,
                previewPanel::setShowBackground,
                previewPanel.isBackgroundRendering());
        toolbar.addSwitchToolbar("Preview Decoration", 120,
                previewPanel::setShowDecoration,
                previewPanel.isDecorationRendering());
        toolbar.addSwitchToolbar("Preview Tile", 90,
                previewPanel::setShowTile,
                previewPanel.isTileRendering());

        //Event shortcut requires only shift key
        master.addKeyListener(painter::bookmark, KeyEvent.VK_B, false, true);
        master.addKeyListener(painter::setSpeed, KeyEvent.VK_S, false, true);
        master.addKeyListener(painter::twirl, KeyEvent.VK_T, false, true);
        master.addKeyListener(painter::moveDecorations, KeyEvent.VK_M, false, true);
        master.addKeyListener(painter::setFilter, KeyEvent.VK_F, false, true);

        //VFXGenerator requires only alt key
        master.addKeyListener(painter::vfxGenAddRandomFilter, KeyEvent.VK_R, false, false, true);
        master.addKeyListener(painter::vfxGenAdvancedAnimateTrack, KeyEvent.VK_T, false, false, true);

        //Otherwise
        master.addKeyListener(painter::selectTask, KeyEvent.VK_T, true);
        master.addKeyListener(painter::undo, KeyEvent.VK_Z, true);
        master.addKeyListener(painter::redo, KeyEvent.VK_Z, true, true);
        master.addKeyListener(painter::save, KeyEvent.VK_S, true);
        master.addKeyListener(painter::getEID, KeyEvent.VK_E, true);
        master.addKeyListener(painter::copy, KeyEvent.VK_C, true);
        master.addKeyListener(painter::paste, KeyEvent.VK_V, true);
        master.addKeyListener(painter::pasteFitToCenterLine, KeyEvent.VK_V, true, true);
        master.addKeyListener(painter::grouping, KeyEvent.VK_G, true);
        master.addKeyListener(painter::ungrouping, KeyEvent.VK_G, true, true);
        master.addKeyListener(painter::destroy, KeyEvent.VK_DELETE);
        master.addKeyListener(painter::flipX, KeyEvent.VK_L, true);
        master.addKeyListener(painter::flipY, KeyEvent.VK_L, true, true);
        master.addKeyListener(painter::deselect, KeyEvent.VK_ESCAPE);
        master.addKeyListener(painter::moveFloor, KeyEvent.VK_F, true);
        master.addChainKeyListener(painter::prevFloor, KeyEvent.VK_LEFT);
        master.addChainKeyListener(painter::nextFloor, KeyEvent.VK_RIGHT);
        master.addKeyListener(painter::moveToStart, KeyEvent.VK_LEFT, true);
        master.addKeyListener(painter::moveToEnd, KeyEvent.VK_RIGHT, true);
        master.addKeyListener(painter::prevBookmark, KeyEvent.VK_LEFT, true, true);
        master.addKeyListener(painter::nextBookmark, KeyEvent.VK_RIGHT, true, true);

        master.addKeyListener(painter::nextMarkedEvent, KeyEvent.VK_UP, false);
        master.addKeyListener(painter::prevMarkedEvent, KeyEvent.VK_DOWN, false);
        master.addKeyListener(painter::nextMarkedEvent, KeyEvent.VK_UP, true);
        master.addKeyListener(painter::prevMarkedEvent, KeyEvent.VK_DOWN, true);
        master.addKeyListener(painter::nextMarkedEvent, KeyEvent.VK_UP, false, true);
        master.addKeyListener(painter::prevMarkedEvent, KeyEvent.VK_DOWN, false, true);
        master.addKeyListener(painter::nextMarkedEvent, KeyEvent.VK_UP, true, true);
        master.addKeyListener(painter::prevMarkedEvent, KeyEvent.VK_DOWN, true, true);

        master.addKeyListener(painter::mark, KeyEvent.VK_M, true);
        master.addKeyListener(painter::unmark, KeyEvent.VK_N, true);
        master.addKeyListener(painter::markingAllMoveDecorations, KeyEvent.VK_M, true, true);
        master.addKeyListener(painter::play, KeyEvent.VK_SPACE);


        master.addKeyListener(() -> analysisPanel.changePanelShowing(CSAnalysisPanel.AnalysisPanelType.KEYFRAME), KeyEvent.VK_F1);
        master.addKeyListener(() -> analysisPanel.changePanelShowing(CSAnalysisPanel.AnalysisPanelType.DECORATION), KeyEvent.VK_F2);
        master.addKeyListener(() -> analysisPanel.changePanelShowing(CSAnalysisPanel.AnalysisPanelType.SETTINGS), KeyEvent.VK_F12);

        add(painter);
        add(toolbar);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        toolbar.setBounds(0, h - CSButton.BUTTON_HEIGHT, w, CSButton.BUTTON_HEIGHT);
        painter.setBounds(0, 0, w, h - CSButton.BUTTON_HEIGHT);
        painter.resetAllEditors();
    }

    public boolean isEventListing() {
        return eventListing;
    }

    private void addTimelineFixedAction(Runnable action) {
        timelineFixedActions.add(action);
    }

    private void addTimelineMovedAction(Runnable action) {
        timelineMovedActions.add(action);
    }

    private void addTimelineLevelLoadedAction(Runnable action) {
        timelineLevelLoadedActions.add(action);
    }

    private void addTimelineEventSelectedAction(Runnable action) {
        timelineEventSelectedActions.add(action);
    }

    public void tryRefreshFrameAnalyzerPanel() {
        if (!frameAnalyzerRefreshed) {
            eventListing = true;
            ((CSMainFrame) getMainFrame()).getAnalyzer().reload(((CSMainFrame) getMainFrame()).getEditor().level());
            eventListing = false;
            frameAnalyzerRefreshed = true;
        }
    }

    public void runTimelineFixedAction() {
        ((CSMainFrame) getMainFrame()).setFixed(true);
        for (Runnable timelineFixedAction : timelineFixedActions) {
            timelineFixedAction.run();
        }
    }

    public void runTimelineMovedAction() {
        for (Runnable timelineMovedAction : timelineMovedActions) {
            timelineMovedAction.run();
        }
    }

    public void runTimelineLevelLoadedAction() {
        ((CSMainFrame) getMainFrame()).setFixed(false);
        for (Runnable timelineLevelLoadedAction : timelineLevelLoadedActions) {
            timelineLevelLoadedAction.run();
        }
    }

    public void runTimelineEventSelectedAction() {
        for (Runnable timelineEventSelectedAction : timelineEventSelectedActions) {
            timelineEventSelectedAction.run();
        }
    }

    public Painter getPainter() {
        return painter;
    }

    public static final class Painter extends CSCoordinatePanel implements CSDrawable {
        @Serial
        private static final long serialVersionUID = -8451898286613646086L;
        /**
         * 세로선 (해당 타일 번호까지 진행 시각) 리스트
         */
        private final List<Double> xLine = new ArrayList<>();
        /**
         * 패널 위 선택된 영역 (해당 변수는 '패널' 좌표이므로, 사용 시 타임라인 좌표로 변환 필요)
         */
        private transient RectBounds selectedArea = new RectBounds(0, 0, 0, 0);

        /**
         * 기본 레벨 로드 메시지
         */
        private static final String DEFAULT_LOAD_MESSAGE = "Level Loaded";
        /**
         * 레벨 로드 시 전송할 메시지
         */
        private String loadMessage = DEFAULT_LOAD_MESSAGE;

        /**
         * DND
         */
        private final transient CSFileDropper<CSTimelinePanel.Painter> dropper;
        /**
         * 최소 버튼 너비
         */
        static final int MIN_BUTTON_WIDTH = 40;
        /**
         * 모든 가상 버튼
         */
        private final List<CSSwitchLocationVirtualButton> allVirtualButtons = new ArrayList<>();
        /**
         * 연결된 가상 버튼. 버튼 활성화 시 다른 버튼이 비활성화됩니다.
         */
        private final transient CSLinkedSwitchLocationButton linkedEventButtons = new CSLinkedSwitchLocationButton();

        /**
         * 그룹화된 버튼. 해당 버튼 중 하나를 클릭시, 다른 버튼도 다중 선택 됩니다.
         */
        private final transient Set<Set<CSSwitchLocationVirtualButton>> groupedButtons = new HashSet<>();
        /**
         * 버튼 그리기 도구, 프레임당 갱신됩니다
         */
        private final transient CSVisualFunction<CSSwitchLocationVirtualButton> buttonPainter = new CSVisualFunction<>(allVirtualButtons);
        /**
         * 분석 패널
         */
        private final CSAnalysisPanel analysisPanel;
        /**
         * 기능 패널
         */
        private final CSFunctionPanel functionPanel;
        /**
         * 타임라인
         */
        private final CSTimelinePanel timelinePanel;
        /**
         * 메시지를 보내는 스레드. 메시지를 보내다가 한번 더 메시지를 호출할때 interrupt 됩니다
         */
        private transient Thread messageSenderThread = null;
        /**
         * 백업 스레드
         */
        private transient Thread backupThread = null;
        /**
         * 메시지를 보내기 위한 패널
         */
        private final CSAnimatablePanel messagePanel = new CSAnimatablePanel(getMainFrame());
        /**
         * 초기 버튼 생성시 SetSpeed 가 너무 많아 빈번한 버튼 위치 재설정 현상 방지
         */
        private boolean preventAnalysis = true;

        /**
         * 모든 버튼이 로딩이 완료되었는지 확인
         */
        private boolean allButtonsLoaded = false;

        /**
         * 타이밍 관련 가상 버튼 생성/수정/제거 시 true,
         * refresh 이후 false 로 변경됩니다.
         */
        boolean timingModified = false;
        /**
         * 레인 (가로줄) 최소 개수
         */
        private static final int MINIMUM_LAYERS = 29;

        /**
         * 복사된 Events 정보
         */
        private transient List<EIN.ABSEventData> copiedEvents = null;
        /**
         * 버튼의 양 끝 지점을 저장.<p>
         * 홀수 인덱스 : 끝 좌표, 짝수 인덱스 : 시작 좌표, 오름차순 정렬<p>
         * 버튼 겹침을 검사할때 사용합니다
         */
        private final List<List<Double>> eventTimelineHorizontalBounds = new ArrayList<>();
        /**
         * Event 비교기를 사용하여 버튼을 찾습니다
         */
        final Map<EventComparator, CSSwitchLocationVirtualButton> eventComparatorButtonMap = new HashMap<>();
        /**
         * 버튼 위치를 이용하여 버튼을 찾습니다
         */
        private final Map<ButtonAttribute.Location, CSSwitchLocationVirtualButton> locationToButtonMap = new HashMap<>();
        /**
         * 수정 기록
         */
        private final List<ModifiedLog> modifiedLogs = new ArrayList<>();
        /**
         * 현재 작업(최신 상태인 경우 리스트의 마지막 인덱스), modifiedLogs 인덱스로 사용함.
         */
        private int currentTaskNumber = 0;
        /**
         * 로그에 없던 완전히 다른 파일을 로드했는지 확인합니다.
         */
        private boolean loadedAnotherFile = true;
        /**
         * 마킹된 버튼
         */
        @Nonnull
        private transient List<CSSwitchLocationVirtualButton> markedButtons = new ArrayList<>();

        /**
         * 현재 메시지
         */
        private String currentMessage = null;
        /**
         * 버튼을 디자인할 최소 줌
         */
        static final double DESIGN_BUTTON_ZOOM_LIMIT = 0.5;
        private boolean dropUI = false;
        private boolean playing = false;
        static final Color BACKGROUND = new Color(20, 20, 20);


        public Painter(CSMainFrame master, CSTimelinePanel timelinePanel, CSAnalysisPanel analysisPanel, CSFunctionPanel functionPanel) {
            super(master, new Range(0.3, 5), new Range(0.75, 2), new Range(0.88, 2));

            this.functionPanel = functionPanel;
            this.analysisPanel = analysisPanel;
            this.timelinePanel = timelinePanel;
            this.dropper = new CSFileDropper<>(this, t -> t.dropUI(false),
                    new CSFileDropper.DropActions<>(FileIO.EXTENSION_STR, t -> t.dropUI(true), (t, file) -> {

                        getMainFrame().disposeAll();

                        Runnable r = () -> {
                            t.dropUI(false);
                            t.loadNewLevel(CustomLevel.load(((CSMainFrame) getMainFrame()).getEditor(), file.getAbsolutePath()), DEFAULT_LOAD_MESSAGE);
                        };

                        if (((CSMainFrame) getMainFrame()).targetLevelFixed()) {
                            ((CSMainFrame) getMainFrame()).saveConfirmDialog("Load new level", r, () -> t.dropUI(false));
                        } else {
                            r.run();
                        }

                    }),
                    new CSFileDropper.DropActions<>("png", t -> {
                    }, (t, file) -> createEventButton(new AddDecoration.Builder().setDecorationImage(new ImageFile(file.getName())).build())),
                    new CSFileDropper.DropActions<>("mp4", t -> {
                    }, (t, file) -> vfxGenVideoDecoration(file.getAbsolutePath()))
            );


            setMovable(true);
            setCanAxisZoom(true);
            setCanZoom(true);
            setCoordinate(new Point2D(0, 100));
            setMovingBoundaries(new RectBounds(0, 0, Integer.MAX_VALUE, MINIMUM_LAYERS * CSButton.BUTTON_HEIGHT));

            setIntervalXMultiplier(0.5);
            setBackground(BACKGROUND);
            update();

            final Point start = new Point();
            AtomicBoolean listenerAdded = new AtomicBoolean(false);
            AtomicBoolean error = new AtomicBoolean(false);
            AtomicReference<EIN.EventData> tempEventData = new AtomicReference<>();
            TaskManager.runTask(() -> {
                for (long i = 0; i < Long.MAX_VALUE; i++) { //무한 반복
                    try {
                        Process type = ((CSMainFrame) getMainFrame()).getEditor().getProcessType();
                        if (preventAnalysis && (type == Process.FINISHED || type == Process.LOAD)) {
                            refreshLines();
                            refreshButtons();

                            if (loadedAnotherFile) {
                                createModifiedLog("Initialize");
                            } else {
                                loadedAnotherFile = true;
                            }

                            allButtonsLoaded = true;
                            preventAnalysis = false;
                            timingModified = false;
                            if (type == Process.LOAD) {
                                sendMessage(loadMessage);
                            }
                            if (backupThread == null) {
                                backupThread = TaskManager.runTask(() -> {
                                    functionPanel.disableAll();
                                    if (master.getEditor().generateOutputFolder() != null && FileIO.backup(master.getEditor())) {
                                        sendMessage("Backup Saved");
                                    }
                                    functionPanel.enableAll();
                                }, 180000, Integer.MAX_VALUE, 180000); //3분마다 백업 저장
                            }
                            if (!listenerAdded.get()) {
                                listenerAdded.set(true);
                                AtomicBoolean modifiedByDragging = new AtomicBoolean(false);
                                addMouseListener(new MouseAdapter() {

                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        if (SwingUtilities.isLeftMouseButton(e)) {

                                            Point2D coordinate = toCoordinate(e.getX(), e.getY());
                                            List<CSSwitchLocationVirtualButton> buttons = getButtonsInSelectedArea(coordinate, coordinate);

                                            if (buttons.isEmpty()) {
                                                deselect();
                                                return;
                                            }

                                            CSSwitchLocationVirtualButton virtualButton = buttons.get(0);
                                            CSSwitchLocationButton button = virtualButton.getRealButton();

                                            List<CSSwitchLocationVirtualButton> buttonSet = checkGrouping(virtualButton);

                                            if (!buttonSet.isEmpty()) {
                                                if (button.isSelecting()) {
                                                    if (getMainFrame().isShiftPressed()) { //다중선택 상태인지 확인
                                                        linkedEventButtons.multiDeselect(buttonSet);
                                                    } else {
                                                        linkedEventButtons.deselectAll();
                                                    }
                                                } else {
                                                    if (!getMainFrame().isShiftPressed()) { //다중선택 해제 상태인지 확인
                                                        linkedEventButtons.deselectAll();
                                                    }
                                                    linkedEventButtons.multiSelect(buttonSet);
                                                }
                                                return;
                                            }

                                            if (button.isSelecting()) {
                                                if (getMainFrame().isShiftPressed()) {
                                                    linkedEventButtons.multiDeselect(virtualButton);
                                                } else {
                                                    button.deselect();
                                                }
                                            } else {
                                                if (getMainFrame().isShiftPressed()) {
                                                    linkedEventButtons.multiSelect(virtualButton);
                                                } else {
                                                    button.select();
                                                }
                                            }
                                            List<CSSwitchLocationVirtualButton> currentEnabledButtons = linkedEventButtons.getSelectedButtons();
                                            if (currentEnabledButtons.size() == 1) {
                                                analysisPanel.openEditor();
                                            } else {
                                                analysisPanel.closeEditor();
                                            }
                                            timelinePanel.runTimelineEventSelectedAction();

                                        }
                                        if (SwingUtilities.isMiddleMouseButton(e)) {
                                            List<CSSwitchLocationVirtualButton> virtualButton = linkedEventButtons.getSelectedButtons();
                                            if (virtualButton.size() == 1) {
                                                moveTimeline(virtualButton.get(0).getAttribute().getLocationAttribute());
                                            }
                                        }
                                    }

                                    @Override
                                    public void mousePressed(MouseEvent e) {

                                        if (getMouseMovementType() == MouseMovementType.LEFT_CLICK_SHIFT_MODE) {
                                            start.setLocation(e.getX(), e.getY());
                                            selectedArea = new RectBounds(start.x, start.y, start.x, start.y);
                                        }
                                        if (getMouseMovementType() == MouseMovementType.LEFT_CLICK_DRAG_MODE) {
                                            selectedArea = new RectBounds(0, 0, 0, 0);
                                        }
                                        if (getMouseMovementType() == MouseMovementType.RIGHT_CLICK_SHIFT_MODE || getMouseMovementType() == MouseMovementType.RIGHT_CLICK_CTRL_MODE) {
                                            start.setLocation(e.getX(), e.getY());
                                            List<CSSwitchLocationVirtualButton> selectedButtons = linkedEventButtons.getSelectedButtons();
                                            if (selectedButtons.size() == 1) {
                                                tempEventData.set(selectedButtons.get(0).getAttribute().getEventInfo());
                                            }
                                        }
                                    }

                                    @Override
                                    public void mouseReleased(MouseEvent e) {
                                        if (modifiedByDragging.get()) {
                                            createModifiedLog("Modified By Dragging");
                                            modifiedByDragging.set(false);
                                        }
                                        if (getMouseMovementType() == MouseMovementType.LEFT_CLICK_SHIFT_MODE) {
                                            Point2D start = toCoordinate(selectedArea.startX(), selectedArea.startY());
                                            Point2D end = toCoordinate(selectedArea.endX(), selectedArea.endY());
                                            if (start.x() == end.x() && start.y() == end.y()) { //click Action
                                                return;
                                            }
                                            List<CSSwitchLocationVirtualButton> buttons = getButtonsInSelectedArea(start, end);
                                            buttons.addAll(buttons.stream()
                                                    .map(this::checkGrouping)
                                                    .flatMap(List::stream)
                                                    .collect(Collectors.toSet())
                                            );
                                            linkedEventButtons.multiSelect(buttons);
                                            timelinePanel.runTimelineEventSelectedAction();
                                            analysisPanel.closeEditor();
                                        }
                                        selectedArea = new RectBounds(0, 0, 0, 0);
                                    }

                                    @Nonnull
                                    private List<CSSwitchLocationVirtualButton> checkGrouping(CSSwitchLocationVirtualButton button) {
                                        Set<CSSwitchLocationVirtualButton> largestSet = new HashSet<>();
                                        for (Set<CSSwitchLocationVirtualButton> buttonSet : groupedButtons) {
                                            if (buttonSet.contains(button) && largestSet.size() < buttonSet.size()) {
                                                largestSet = buttonSet;
                                            }
                                        }
                                        return new ArrayList<>(largestSet);
                                    }
                                });
                                addMouseMotionListener(new MouseMotionAdapter() {

                                    @Override
                                    public void mouseDragged(MouseEvent e) {
                                        if (getMouseMovementType() == MouseMovementType.LEFT_CLICK_SHIFT_MODE) {
                                            selectedArea = new RectBounds(start.x, start.y, e.getX(), e.getY());
                                        }

                                        //이벤트 늘이기와 줄이기
                                        if (getMouseMovementType() == MouseMovementType.RIGHT_CLICK_SHIFT_MODE || getMouseMovementType() == MouseMovementType.RIGHT_CLICK_CTRL_MODE) {
                                            modifiedByDragging.set(true);
                                            List<CSSwitchLocationVirtualButton> selectedButtons = linkedEventButtons.getSelectedButtons();
                                            if (selectedButtons.size() == 1) {
                                                CSSwitchLocationVirtualButton button = selectedButtons.get(0);


                                                EID eventID = button.getAttribute().getEventInfo().eventID();
                                                Event event = button.getAttribute().getEventInfo().event();
                                                EventBuilder eventBuilder = event.edit();

                                                if (!Objects.equals(eventID, tempEventData.get().eventID())) {
                                                    //대부분의 경우는 일어날 수 없는 경우이지만 발생할경우 치명적인 작용을 방지
                                                    return;
                                                }

                                                double tlx = toCoordinate(e.getX(), e.getY()).x() - toCoordinate(start.x, start.y).x();

                                                switch (getMouseMovementType()) {
                                                    case RIGHT_CLICK_SHIFT_MODE -> {
                                                        if (eventBuilder instanceof DoubleDurationEventBuilder eb) {
                                                            DoubleDurationEvent startEvent = (DoubleDurationEvent) tempEventData.get().event();
                                                            double duration = startEvent.duration();
                                                            eb.setDuration(Math.max(0, duration + tlx / 180));
                                                        }
                                                        if (eventBuilder instanceof IntDurationEventBuilder eb) {
                                                            IntDurationEvent startEvent = (IntDurationEvent) tempEventData.get().event();
                                                            int duration = startEvent.duration();

                                                            eb.setDuration((int) Math.max(0, duration + tlx / 180));
                                                        }
                                                    }
                                                    case RIGHT_CLICK_CTRL_MODE -> {
                                                        if (eventBuilder instanceof AngleOffsetEventBuilder eb) {
                                                            AngleOffsetEvent startEvent = (AngleOffsetEvent) tempEventData.get().event();
                                                            double angleOffset = startEvent.angleOffset();
                                                            eb.setAngleOffset(angleOffset + tlx);
                                                        }
                                                    }
                                                    default -> {
                                                        //noop
                                                    }
                                                }

                                                analysisPanel.closeEditor();

                                                int prevY = button.getAttribute().getLocationAttribute().yLineNumber();

                                                button.refreshIfChangedRemotely(eventID, eventBuilder.build());

                                                int curY = button.getAttribute().getLocationAttribute().yLineNumber();

                                                if (prevY != curY) {
                                                    moveTimeline(button.getAttribute().getLocationAttribute(), Axis.Y_AXIS);
                                                }

                                                timelinePanel.runTimelineFixedAction();
                                            }
                                        }
                                    }

                                });
                            }

                            functionPanel.enableAll();
                            dropper.setEnabled(true);
                            timelinePanel.runTimelineLevelLoadedAction();
                        }

                        if (type == Process.ERROR && error.get()) {
                            functionPanel.enableAll();
                            dropper.setEnabled(true);
                            error.set(true);
                        }
                        if (type != Process.ERROR) {
                            error.set(false);
                        }
                        if (allButtonsLoaded) {
                            update();
                        }
                        Thread.sleep(CSMainFrame.FRAME_REFRESH_MILLIS);
                    } catch (InterruptedException e) {
                        ConsoleUtils.logError(e);
                        Thread.currentThread().interrupt();
                    }
                }
            });

            messagePanel.setLocation(0, 0);
            messagePanel.getNameLabel().setForeground(Color.WHITE);
            messagePanel.getNameLabel().setHorizontalAlignment(SwingConstants.LEFT);
            messagePanel.getNameLabel().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
            messagePanel.setBackground(new Color(255, 255, 255, 0));
            messagePanel.setForeground(new Color(255, 255, 255, 0));
            messagePanel.setBorder(null);
            messagePanel.setSize(1000, 40);

            add(messagePanel);
        }

        boolean isLoading() {
            return !allButtonsLoaded || ((CSMainFrame) getMainFrame()).isSaving();
        }

        /**
         * 선택된 Event 복제
         */
        private void copy() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }
            List<CSSwitchLocationVirtualButton> currentEnabledButtons = linkedEventButtons.getSelectedButtons();

            if (!currentEnabledButtons.isEmpty()) {
                copiedEvents = currentEnabledButtons.stream()
                        .sorted(Comparator.comparing(e -> e.getAttribute().getEventInfo().eventID().address()))
                        .sorted(Comparator.comparing(e -> e.getAttribute().getEventInfo().eventID().floor()))
                        .map(v -> v.getAttribute().abstractEventInfo())
                        .toList();
                sendMessage("Event Copied");
            }
        }

        /**
         * 복제된 Event 붙여넣기
         */
        private void paste() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            if (copiedEvents != null) {
                List<EIN.ABSEventData> toPaste = new ArrayList<>();
                int floor = getFloorOnCenter();
                int min = copiedEvents.stream().mapToInt(EIN.ABSEventData::floor).min().orElseThrow(NullPointerException::new);
                int offset = floor - min;
                for (EIN.ABSEventData copiedEvent : copiedEvents) {
                    toPaste.add(new EIN.ABSEventData(copiedEvent.floor() + offset, copiedEvent.event()));
                }

                if (createEventButton(toPaste)) {
                    sendMessage("Event Pasted");
                }
                timelinePanel.runTimelineFixedAction();
                createModifiedLog("Clone Event");
            }

        }

        /**
         * 복제된 Event 를 가운데 선에 맞춰 붙여넣기
         */
        private void pasteFitToCenterLine() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            if (copiedEvents != null) {

                int floor = getFloorOnCenter();
                int min = copiedEvents.stream().mapToInt(EIN.ABSEventData::floor).min().orElseThrow(NullPointerException::new);
                int offset = floor - min;

                List<EIN.ABSEventData> toPaste = copiedEvents.stream().map(e -> {
                    EventBuilder builder = e.event().edit();
                    if (builder instanceof AngleOffsetEventBuilder b) {
                        b.setAngleOffset(((AngleOffsetEvent) e.event()).angleOffset() + getCenterAngleOffset());
                    }
                    return new EIN.ABSEventData(e.floor() + offset, builder.build());
                }).toList();

                if (createEventButton(toPaste)) {
                    sendMessage("Event Pasted");
                }
            }
            timelinePanel.runTimelineFixedAction();
            createModifiedLog("Clone Event (Fit to Center Line)");
        }

        /**
         * 버튼을 그룹화합니다.
         */
        private void grouping() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            Set<CSSwitchLocationVirtualButton> buttons = new HashSet<>(linkedEventButtons.getSelectedButtons());
            if (buttons.size() < 2) {
                return;
            }
            if (!groupedButtons.contains(buttons)) {
                groupedButtons.add(buttons);
                sendMessage(buttons.size() + " Events have been grouped.");
            }
        }

        /**
         * 버튼 그룹을 해제합니다.
         */
        private void ungrouping() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }
            List<CSSwitchLocationVirtualButton> currentEnabledButtons = linkedEventButtons.getSelectedButtons();

            if (currentEnabledButtons.size() < 2) {
                return;
            }
            CSSwitchLocationVirtualButton target = currentEnabledButtons.get(0);


            groupedButtons.remove(
                    groupedButtons.stream()
                            .filter(e -> e.contains(target)) //해당 버튼이 속한 그룹
                            .max(Comparator.comparing(Set::size)) //그 가운데 가장 큰 그룹
                            .orElseGet(HashSet::new)
            ); //제거

            sendMessage("Selected button group has been removed.");
        }

        /**
         * 현재 선택된 이벤트의 이벤트의 회전값, 좌표값을 좌우반전합니다.
         */
        private void flipX() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }
            List<CSSwitchLocationVirtualButton> currentEnabledButtons = linkedEventButtons.getSelectedButtons();

            if (!currentEnabledButtons.isEmpty()) {
                analysisPanel.closeEditor();
                flip(Axis.X_AXIS);
                sendMessage("Selected Events have been inverted on the x-axis.");
                analysisPanel.openEditor();
            }
        }

        /**
         * 현재 선택된 이벤트의 이벤트의 회전값, 좌표값을 상하반전합니다. <p>
         * 예외 : 색수차도 50 기준 반전됩니다.
         */
        private void flipY() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }
            List<CSSwitchLocationVirtualButton> currentEnabledButtons = linkedEventButtons.getSelectedButtons();

            if (!currentEnabledButtons.isEmpty()) {
                analysisPanel.closeEditor();
                flip(Axis.Y_AXIS);
                sendMessage("Selected Events have been inverted on the y-axis.");
                analysisPanel.openEditor();
            }
        }

        private void flip(Axis axis) {

            //현재 편집중인 이벤트가 없어야 정상적으로 변경할수있습니다
            assert analysisPanel.getCurrentlyEditingEvent() == null;

            List<CSSwitchLocationVirtualButton> currentEnabledButtons = linkedEventButtons.getSelectedButtons();

            for (CSSwitchLocationVirtualButton enabledButton : currentEnabledButtons) {

                EID id = enabledButton.getAttribute().getEventInfo().eventID();
                final Event event = enabledButton.getAttribute().getEventInfo().event();
                eventComparatorButtonMap.remove(new EventComparator(event));
                final Event finalEvent;

                switch (event) {
                    case MoveCamera a -> {
                        MoveCamera.Builder builder = a.edit();
                        if (a.position() != null) {
                            Point2D.Builder position = a.position().edit();
                            if (axis == Axis.X_AXIS) {
                                builder.setPosition(position.invertX().build());
                            }
                            if (axis == Axis.Y_AXIS) {
                                builder.setPosition(position.invertY().build());
                            }
                        }
                        if (a.rotation() != null) {
                            builder.setRotation(a.rotation() * -1);
                        }
                        finalEvent = builder.build();
                    }
                    case PositionTrack a -> {
                        PositionTrack.Builder builder = a.edit();
                        if (a.positionOffset() != null) {
                            Point2D.Builder position = a.positionOffset().edit();
                            if (axis == Axis.X_AXIS) {
                                builder.setPositionOffset(position.invertX().build());
                            }
                            if (axis == Axis.Y_AXIS) {
                                builder.setPositionOffset(position.invertY().build());
                            }
                        }
                        if (a.rotation() != null) {
                            builder.setRotation(a.rotation() * -1);
                        }
                        finalEvent = builder.build();
                    }
                    case MoveTrack a -> {
                        MoveTrack.Builder builder = a.edit();
                        if (a.positionOffset() != null) {
                            Point2D.Builder position = a.positionOffset().edit();
                            if (axis == Axis.X_AXIS) {
                                builder.setPositionOffset(position.invertX().build());
                            }
                            if (axis == Axis.Y_AXIS) {
                                builder.setPositionOffset(position.invertY().build());
                            }
                        }
                        if (a.rotationOffset() != null) {
                            builder.setRotationOffset(a.rotationOffset() * -1);
                        }
                        finalEvent = builder.build();
                    }
                    case MoveDecorations a -> {
                        MoveDecorations.Builder builder = a.edit();
                        if (a.positionOffset() != null) {
                            Point2D.Builder position = a.positionOffset().edit();
                            if (axis == Axis.X_AXIS) {
                                builder.setPositionOffset(position.invertX().build());
                            }
                            if (axis == Axis.Y_AXIS) {
                                builder.setPositionOffset(position.invertY().build());
                            }
                        }
                        if (a.pivotOffset() != null) {
                            Point2D.Builder pivot = a.pivotOffset().edit();
                            if (axis == Axis.X_AXIS) {
                                builder.setPivotOffset(pivot.invertX().build());
                            }
                            if (axis == Axis.Y_AXIS) {
                                builder.setPivotOffset(pivot.invertY().build());
                            }
                        }
                        if (a.parallaxOffset() != null) {
                            Point2D.Builder position = a.parallaxOffset().edit();
                            if (axis == Axis.X_AXIS) {
                                builder.setParallaxOffset(position.invertX().build());
                            }
                            if (axis == Axis.Y_AXIS) {
                                builder.setParallaxOffset(position.invertY().build());
                            }
                        }
                        if (a.rotationOffset() != null) {
                            builder.setRotationOffset(a.rotationOffset() * -1);
                        }
                        finalEvent = builder.build();
                    }
                    case SetFilter a -> {
                        SetFilter.Builder builder = a.edit();
                        if (a.filter() == Filter.ABERRATION) {
                            builder.setIntensity(100 - a.intensity());
                        }
                        finalEvent = builder.build();
                    }
                    case AddDecoration d -> {
                        AddDecoration.Builder builder = d.edit();
                        if (d.position() != null) {
                            Point2D.Builder position = d.position().edit();
                            if (axis == Axis.X_AXIS) {
                                builder.setPosition(position.invertX().build());
                            }
                            if (axis == Axis.Y_AXIS) {
                                builder.setPosition(position.invertY().build());
                            }
                        }
                        if (d.pivotOffset() != null) {
                            Point2D.Builder pivot = d.pivotOffset().edit();
                            if (axis == Axis.X_AXIS) {
                                builder.setPivotOffset(pivot.invertX().build());
                            }
                            if (axis == Axis.Y_AXIS) {
                                builder.setPivotOffset(pivot.invertY().build());
                            }
                        }
                        if (d.parallaxOffset() != null) {
                            Point2D.Builder parallaxOffset = d.parallaxOffset().edit();
                            if (axis == Axis.X_AXIS) {
                                builder.setParallaxOffset(parallaxOffset.invertX().build());
                            }
                            if (axis == Axis.Y_AXIS) {
                                builder.setParallaxOffset(parallaxOffset.invertY().build());
                            }
                        }
                        if (d.rotation() != null) {
                            builder.setRotation(d.rotation() * -1);
                        }
                        finalEvent = builder.build();
                    }
                    case AddText d -> {
                        AddText.Builder builder = d.edit();
                        if (d.position() != null) {
                            Point2D.Builder position = d.position().edit();
                            if (axis == Axis.X_AXIS) {
                                builder.setPosition(position.invertX().build());
                            }
                            if (axis == Axis.Y_AXIS) {
                                builder.setPosition(position.invertY().build());
                            }
                        }
                        if (d.pivotOffset() != null) {
                            Point2D.Builder pivot = d.pivotOffset().edit();
                            if (axis == Axis.X_AXIS) {
                                builder.setPivotOffset(pivot.invertX().build());
                            }
                            if (axis == Axis.Y_AXIS) {
                                builder.setPivotOffset(pivot.invertY().build());
                            }
                        }
                        if (d.parallaxOffset() != null) {
                            Point2D.Builder parallaxOffset = d.parallaxOffset().edit();
                            if (axis == Axis.X_AXIS) {
                                builder.setParallaxOffset(parallaxOffset.invertX().build());
                            }
                            if (axis == Axis.Y_AXIS) {
                                builder.setParallaxOffset(parallaxOffset.invertY().build());
                            }
                        }
                        if (d.rotation() != null) {
                            builder.setRotation(d.rotation() * -1);
                        }
                        finalEvent = builder.build();
                    }
                    default -> finalEvent = event;
                }
                enabledButton.refreshIfChangedRemotely(id, finalEvent);
            }
            timelinePanel.runTimelineFixedAction();
            createModifiedLog("Flip Event");
        }

        private enum Axis {
            X_AXIS, Y_AXIS
        }

        private void selectTask() {
            AtomicInteger taskNumber = new AtomicInteger(currentTaskNumber);
            CSMultiDialog dialog = new CSMultiDialog(getMainFrame(), "Modified Logs", 400, 200, () -> setTask(taskNumber.get(), "Selected Task has been Loaded."));
            ModifiedLog firstValue = modifiedLogs.get(this.currentTaskNumber);
            dialog.getInput().createSelectInput("Select Task",
                    firstValue,
                    firstValue,
                    modifiedLogs.toArray(ModifiedLog[]::new),
                    s -> taskNumber.set(modifiedLogs.indexOf(s)));
            dialog.setup();
        }

        private void undo() {
            setTask(currentTaskNumber - 1, "Undo");
        }

        private void redo() {
            setTask(currentTaskNumber + 1, "Redo");
        }

        private void save(){
            CSMainFrame mainFrame = ((CSMainFrame) getMainFrame());
            if(isLoading()){
                sendWaitMessage();
                return;
            }
            mainFrame.saveLevel(false, true);
        }

        /**
         * 버튼이 단일 선택된 경우, 해당 버튼의 이벤트 아이디를 얻습니다.
         */
        private void getEID() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            List<CSSwitchLocationVirtualButton> currentEnabledButtons = linkedEventButtons.getSelectedButtons();

            if (currentEnabledButtons.size() == 1) {
                sendMessage("Event ID : " + currentEnabledButtons.get(0).getAttribute().getEventInfo().eventID());
            }
        }

        /**
         * 선택된 Event 제거
         */
        private void destroy() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            List<CSSwitchLocationVirtualButton> currentEnabledButtons = linkedEventButtons.getSelectedButtons();

            if (!currentEnabledButtons.isEmpty()) {
                removeEventButton(currentEnabledButtons);
                timelinePanel.runTimelineFixedAction();
                createModifiedLog("Event Removal");
                sendMessage("Event Deleted");
            }
        }

        /**
         * 모두 선택 해제
         */
        private void deselect() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            List<CSSwitchLocationVirtualButton> currentEnabledButtons = linkedEventButtons.getSelectedButtons();

            if (!currentEnabledButtons.isEmpty()) {
                linkedEventButtons.deselectAll();
            }
        }


        /**
         * 타임라인 이동
         */
        public void moveFloor() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            new CSSingleDialog<>(getMainFrame(), "Move Floor", "Floor", getFloorOnCenter(), Integer::parseInt, e ->
                    moveTimelineX(getBoundOfX(e, 0), 0, Ease.OUT_CUBIC.fun()));
        }


        private int destinationFloor = 0;

        private void refreshDestinationFloorToCenter() {
            setDestinationFloor(getFloorOnCenter());
        }

        private void setDestinationFloor(int floor) {
            destinationFloor = floor;
        }

        /**
         * 이전 타일로 이동
         */
        public void prevFloor() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }
            if (isPlaying()) {
                stopMove();
                refreshDestinationFloorToCenter();
                setDestinationFloor(Math.min(destinationFloor + 1, ((CSMainFrame) getMainFrame()).getEditor().level().getLength()));
            }
            setDestinationFloor(Math.max(0, destinationFloor - 1));
            moveTimelineX(getBoundOfX(destinationFloor, 0), 100, Ease.OUT_CUBIC.fun());
        }

        /**
         * 다음 타일로 이동
         */
        public void nextFloor() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }
            if (isPlaying()) {
                stopMove();
                refreshDestinationFloorToCenter();

            }

            setDestinationFloor(Math.min(destinationFloor + 1, ((CSMainFrame) getMainFrame()).getEditor().level().getLength()));
            moveTimelineX(getBoundOfX(destinationFloor, 0), 100, Ease.OUT_CUBIC.fun());
        }

        /**
         * 시작 위치로 이동
         */
        public void moveToStart() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }
            setDestinationFloor(0);
            moveTimelineX(getBoundOfX(0, 0), PANEL_ANIMATE_DURATION, Ease.OUT_CUBIC.fun());
        }

        /**
         * 끝 위치로 이동
         */
        public void moveToEnd() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            CustomLevel level = ((CSMainFrame) getMainFrame()).getEditor().level();
            setDestinationFloor(level.getLength());
            moveTimelineX(getBoundOfX(level.getLength(), 0), PANEL_ANIMATE_DURATION, Ease.OUT_CUBIC.fun());
        }

        /**
         * 이전 북마크
         */
        public void prevBookmark() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            List<CSSwitchLocationVirtualButton> currentEnabledButtons = linkedEventButtons.getSelectedButtons();

            int currentFloor = currentEnabledButtons.size() != 1
                    ? getFloorOnCenter()
                    : currentEnabledButtons.get(0).getAttribute().getEventInfo().eventID().floor();

            CustomLevel level = ((CSMainFrame) getMainFrame()).getEditor().level();
            for (int i = currentFloor - 1; i >= 0; i--) {
                if (checkBookmarkAndMove(level, i)) return;
            }
            moveToStart();
        }

        /**
         * 다음 북마크
         */
        public void nextBookmark() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            List<CSSwitchLocationVirtualButton> currentEnabledButtons = linkedEventButtons.getSelectedButtons();

            int currentFloor = currentEnabledButtons.size() != 1
                    ? getFloorOnCenter()
                    : currentEnabledButtons.get(0).getAttribute().getEventInfo().eventID().floor();

            CustomLevel level = ((CSMainFrame) getMainFrame()).getEditor().level();
            for (int i = currentFloor + 1; i <= level.getLength(); i++) {
                if (checkBookmarkAndMove(level, i)) return;
            }
            moveToEnd();
        }

        /**
         * SetSpeed 설치
         */
        public void setSpeed() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            CustomLevel level = ((CSMainFrame) getMainFrame()).getEditor().level();
            SetSpeed.Builder builder = new SetSpeed.Builder();

            double bpm = level.getSettings().bpm();
            double multiplier = 2;

            builder.setBpm(bpm)
                    .setMultiplier(multiplier);


            CSMultiDialog dialog = new CSMultiDialog(getMainFrame(), "Set Speed", () -> {
                createEventButton(builder.build());
                analysisPanel.openEditor();
            });
            dialog.getInput().createTextInput("BPM", bpm, 100.0, Double::parseDouble, builder::setBpm);
            dialog.getInput().createTextInput("Multiplier", multiplier, multiplier, Double::parseDouble, builder::setMultiplier);
            dialog.setup();
        }

        /**
         * Twirl 설치
         */
        public void twirl() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            createEventButton(new Twirl.Builder().build());
            analysisPanel.openEditor();
        }

        /**
         * SetFilter 설치
         */
        public void setFilter() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            createEventButton(new SetFilter.Builder().build());
            analysisPanel.openEditor();
        }

        /**
         * MoveDecorations 설치
         */
        public void moveDecorations() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            new CSSingleDialog<>(getMainFrame(), "MoveDecorations", "Tag", "sampleTag", s -> s,
                    s -> {
                        createEventButton(new MoveDecorations.Builder().setTag(Tag.of(s.split(" "))).build());
                        analysisPanel.openEditor();
                    });
        }

        /**
         * 북마크 설치
         */
        public void bookmark() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            createEventButton(new Bookmark.Builder().build());
            analysisPanel.openEditor();
        }


        private boolean checkBookmarkAndMove(CustomLevel level, int floor) {
            Bookmark bookmark = level.getActivatedAction(floor, Bookmark.class);
            if (bookmark != null) {
                CSSwitchLocationVirtualButton button = getEventButton(bookmark);
                if (button == null) {
                    sendWaitMessage();
                    return false;
                }
                ButtonAttribute.Location location = button.getAttribute().getLocationAttribute();
                button.select();
                moveTimeline(location.startX(), location.yLineNumber(), Ease.OUT_CUBIC.fun());
                setDestinationFloor(getFloor(location.startX()));
                return true;
            }
            return false;
        }

        public void play() {
            double endX = getXLine(((CSMainFrame) getMainFrame()).getEditor().level().getLength()) + 100000;
            double startX = getCoordinate().x();
            if (startX > endX) {
                return;
            }

            if (isPlaying()) {
                stopMove();
                currWheelCoordinate.set(getCoordinate());
                refreshDestinationFloorToCenter();
                setMovable(true);
                return;
            }

            setMovable(false);

            double angleInterval = endX - startX;
            double bpm = ((CSMainFrame) getMainFrame()).getEditor().level().getSettings().bpm() * ((CSMainFrame) getMainFrame()).getEditor().level().getSettings().pitch() / 100;
            long duration = (long) (1000 * angleInterval / (3 * bpm));


            move(new Point2D(endX, Double.NaN), duration, e -> e);
            playing = true;
        }


        @Override
        protected void stopMove() {
            super.stopMove();
            playing = false;
        }

        private boolean isPlaying() {
            return playing && isCoordinateMoving();
        }

        private transient List<CSSwitchLocationVirtualButton> prevMarkedButtons = new ArrayList<>();

        private int markedEventsIndex = 0;

        public void mark() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            mark(linkedEventButtons.getSelectedButtons());
        }

        private void mark(@Nonnull List<CSSwitchLocationVirtualButton> buttons) {

            if (buttons.size() > 1) {

                for (CSSwitchLocationVirtualButton markedButton : markedButtons) {
                    markedButton.getRealButton().setMarked(false);
                }
                for (CSSwitchLocationVirtualButton markedButton : buttons) {
                    markedButton.getRealButton().setMarked(true);
                }

                markedButtons = buttons;
                sendMessage(buttons.size() + " Events have been marked");
            } else {
                sendMessage("Cannot mark only one event");
            }
        }

        public void unmark() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            for (CSSwitchLocationVirtualButton markedButton : markedButtons) {
                markedButton.getRealButton().setMarked(false);
            }
            sendMessage(markedButtons.size() + " Events have been unmarked");
            markedButtons = new ArrayList<>();
        }


        public void nextMarkedEvent() {
            if (isLoading()) {
                sendWaitMessage();
                return;
            }
            if (markedButtons.size() < 2) {
                return;
            }

            if (prevMarkedButtons != markedButtons) {
                prevMarkedButtons = markedButtons;
                markedEventsIndex = 0;
            }

            if (markedEventsIndex == markedButtons.size() - 1) {
                markedEventsIndex = -1;
            }

            markedEventsIndex++;

            if (getMainFrame().isShiftPressed()) {
                markedEventsIndex = markedButtons.size() - 1;
            }

            CSSwitchLocationVirtualButton button = markedButtons.get(markedEventsIndex);

            if (getMainFrame().isControlPressed()) {
                moveTimeline(button.getAttribute().getLocationAttribute());
            }

            button.select();
            analysisPanel.openEditor();
        }

        public void prevMarkedEvent() {
            if (isLoading()) {
                sendWaitMessage();
                return;
            }
            if (markedButtons.size() < 2) {
                return;
            }

            if (prevMarkedButtons != markedButtons) {
                prevMarkedButtons = markedButtons;
                markedEventsIndex = 0;
            }
            if (markedEventsIndex == 0) {
                markedEventsIndex = markedButtons.size();
            }

            markedEventsIndex--;

            if (getMainFrame().isShiftPressed()) {
                markedEventsIndex = 0;
            }

            CSSwitchLocationVirtualButton button = markedButtons.get(markedEventsIndex);

            if (getMainFrame().isControlPressed()) {
                moveTimeline(button.getAttribute().getLocationAttribute());
            }

            button.select();
            analysisPanel.openEditor();
        }

        public void markingAllMoveDecorations() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            List<CSSwitchLocationVirtualButton> currentEnabledButtons = linkedEventButtons.getSelectedButtons();

            if (currentEnabledButtons.size() == 1 && currentEnabledButtons.get(0).getAttribute().getEventInfo().event() instanceof AddDecoration a) {
                List<CSSwitchLocationVirtualButton> target = new ArrayList<>();

                target.add(getEventButton(a));

                target.addAll(((CSMainFrame) getMainFrame()).getAnalyzer().getMoveDecorations(a.tag())
                        .stream()
                        .map(EIN.EventData::event)
                        .map(this::getEventButton)
                        .filter(Objects::nonNull) //이벤트 반복, 조건부 이벤트 등이 적용된 허상의 이벤트를 필터링
                        .toList()
                );

                mark(target);
            }
        }

        /**
         * 랜덤 필터 추가
         */
        public void vfxGenAddRandomFilter() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            String executeName = "Executed : RandomFilters";
            new CSSingleDialog<>(getMainFrame(), "Random Filters", "Amount", 5, Integer::parseInt, e -> {
                createEventButton(Filter.random(e, 0).stream().map(v -> new EIN.ABSEventData(getFloorOnCenter(), v)).toList());
                sendMessage(executeName);
                timelinePanel.runTimelineFixedAction();
                createModifiedLog("Random Filters");
            });
        }


        /**
         * 향상된 트랙 애니메이션 추가
         */
        public void vfxGenAdvancedAnimateTrack() {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            String executeName = "Executed : AdvancedAnimateTrack";

            dropper.setEnabled(false);

            AtomicInteger startTile = new AtomicInteger(getFloorOnCenter());
            AtomicInteger endTile = new AtomicInteger(getFloorOnCenter());
            AtomicReference<Double> normalScale = new AtomicReference<>(100d);
            AtomicReference<Double> normalOpacity = new AtomicReference<>(100d);
            AtomicReference<Double> onTileOpacity = new AtomicReference<>(50d);
            AtomicReference<Double> onTileOpacityDuration = new AtomicReference<>(0.5);
            AtomicReference<Double> percentage = new AtomicReference<>(100d);

            AtomicBoolean aheadEnabled = new AtomicBoolean(true);
            AtomicReference<Double> aheadAnimationStartBeats = new AtomicReference<>(8d);
            AtomicReference<Double> aheadDuration = new AtomicReference<>(4d);
            AtomicReference<Point2D> aheadPosition = new AtomicReference<>(Point2D.ORIGIN);
            AtomicReference<Double> aheadRotation = new AtomicReference<>(0d);
            AtomicReference<Double> aheadScale = new AtomicReference<>(100d);
            AtomicReference<Ease> aheadEase = new AtomicReference<>(Ease.LINEAR);
            AtomicReference<Double> aheadRandomDurationP = new AtomicReference<>(0d);
            AtomicReference<Double> aheadRandomRadius = new AtomicReference<>(0d);
            AtomicReference<Double> aheadRandomRotation = new AtomicReference<>(0d);
            AtomicReference<Double> aheadRandomScaleP = new AtomicReference<>(0d);

            AtomicBoolean behindEnabled = new AtomicBoolean(true);
            AtomicReference<Double> behindAnimationStartBeats = new AtomicReference<>(0d);
            AtomicReference<Double> behindDuration = new AtomicReference<>(4d);
            AtomicReference<Point2D> behindPosition = new AtomicReference<>(Point2D.ORIGIN);
            AtomicReference<Double> behindRotation = new AtomicReference<>(0d);
            AtomicReference<Double> behindScale = new AtomicReference<>(100d);
            AtomicReference<Ease> behindEase = new AtomicReference<>(Ease.LINEAR);
            AtomicReference<Double> behindRandomDurationP = new AtomicReference<>(0d);
            AtomicReference<Double> behindRandomRadius = new AtomicReference<>(0d);
            AtomicReference<Double> behindRandomRotation = new AtomicReference<>(0d);
            AtomicReference<Double> behindRandomScaleP = new AtomicReference<>(0d);

            AtomicBoolean init = new AtomicBoolean(false);

            CSMultiDialog dialog = new CSMultiDialog(getMainFrame(), "Add Advanced Animate Track", 450, 300, () -> {
                VFXGeneratorAnimateTile gen = new VFXGeneratorAnimateTile(((CSMainFrame) getMainFrame()).getEditor().level());
                List<EIN.ABSEventData> events = gen.getAdvancedTileAnimation(startTile.get(), endTile.get(),
                        aheadEnabled.get() ? new AttributeAnimation(aheadAnimationStartBeats.get(), aheadDuration.get(), aheadPosition.get(), aheadRotation.get(), aheadScale.get(), aheadEase.get(), aheadRandomDurationP.get(),
                                aheadRandomRadius.get(), aheadRandomRotation.get(), aheadRandomScaleP.get()) : null,
                        normalScale.get(), normalOpacity.get(), onTileOpacity.get(), onTileOpacityDuration.get(), percentage.get(),
                        behindEnabled.get() ? new AttributeAnimation(behindAnimationStartBeats.get(), behindDuration.get(), behindPosition.get(), behindRotation.get(), behindScale.get(), behindEase.get(), behindRandomDurationP.get(),
                                behindRandomRadius.get(), behindRandomRotation.get(), behindRandomScaleP.get()) : null, null);
                createEventButton(events);
                sendMessage(executeName);
                timelinePanel.runTimelineFixedAction();
                createModifiedLog("Advanced Animate Track");
            });
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    dropper.setEnabled(true);
                }
            });
            dialog.getInput().createTextInput("Start Tile", startTile.get(), startTile.get(), Integer::parseInt, startTile::set);
            dialog.getInput().createTextInput("End Tile", endTile.get(), endTile.get(), Integer::parseInt, endTile::set);
            dialog.getInput().createTextInput("Default Scale", normalScale.get(), normalScale.get(), Double::parseDouble, normalScale::set);
            dialog.getInput().createTextInput("Default Opacity", normalOpacity.get(), normalOpacity.get(), Double::parseDouble, normalOpacity::set);
            dialog.getInput().createTextInput("Opacity After Hit", onTileOpacity.get(), onTileOpacity.get(), Double::parseDouble, onTileOpacity::set);
            dialog.getInput().createTextInput("Opacity Change Duration", onTileOpacityDuration.get(), onTileOpacityDuration.get(), Double::parseDouble, onTileOpacityDuration::set);
            dialog.getInput().createTextInput("Application Percentage", percentage.get(), percentage.get(), Double::parseDouble, percentage::set);
            dialog.getInput().createTitle("Ahead Animation");

            dialog.getInput().createBoolInput("Enabled", true, true, e -> {
                if (init.get()) {
                    for (int i = 10; i <= 19; i++) {
                        dialog.getInput().getComponents()[i].setEnabled(e);
                    }
                }
                aheadEnabled.set(e);
            });


            dialog.getInput().createTextInput("Beats Ahead", aheadAnimationStartBeats.get(), aheadAnimationStartBeats.get(), Double::parseDouble, aheadAnimationStartBeats::set);
            dialog.getInput().createTextInput("Animate Duration", aheadDuration.get(), aheadDuration.get(), Double::parseDouble, aheadDuration::set);
            dialog.getInput().createTemplateInput("Start Position", aheadPosition.get(), aheadPosition.get(), Point2D.class, aheadPosition::set, CSTemplates.getNoneMatchTemplatesProvider());
            dialog.getInput().createTextInput("Start Rotation", aheadRotation.get(), aheadRotation.get(), Double::parseDouble, aheadRotation::set);
            dialog.getInput().createTextInput("Start Scale", aheadScale.get(), aheadScale.get(), Double::parseDouble, aheadScale::set);
            dialog.getInput().createSelectInput("Ahead Ease", aheadEase.get(), aheadEase.get(), Ease.values(), aheadEase::set);
            dialog.getInput().createTextInput("Random Duration Percentage", aheadRandomDurationP.get(), aheadRandomDurationP.get(), Double::parseDouble, aheadRandomDurationP::set);
            dialog.getInput().createTextInput("Random Radius", aheadRandomRadius.get(), aheadRandomRadius.get(), Double::parseDouble, aheadRandomRadius::set);
            dialog.getInput().createTextInput("Random Rotation", aheadRandomRotation.get(), aheadRandomRotation.get(), Double::parseDouble, aheadRandomRotation::set);
            dialog.getInput().createTextInput("Random Scale Percentage", aheadRandomScaleP.get(), aheadRandomScaleP.get(), Double::parseDouble, aheadRandomScaleP::set);


            dialog.getInput().createTitle("Behind Animation");

            dialog.getInput().createBoolInput("Enabled", true, true, e -> {
                if (init.get()) {
                    for (int i = 22; i <= 31; i++) {
                        dialog.getInput().getComponents()[i].setEnabled(e);
                    }
                }

                behindEnabled.set(e);
            });

            dialog.getInput().createTextInput("Beats Behind", behindAnimationStartBeats.get(), behindAnimationStartBeats.get(), Double::parseDouble, behindAnimationStartBeats::set);
            dialog.getInput().createTextInput("Animate Duration", behindDuration.get(), behindDuration.get(), Double::parseDouble, behindDuration::set);
            dialog.getInput().createTemplateInput("End Position", behindPosition.get(), behindPosition.get(), Point2D.class, behindPosition::set, CSTemplates.getNoneMatchTemplatesProvider());
            dialog.getInput().createTextInput("End Rotation", behindRotation.get(), behindRotation.get(), Double::parseDouble, behindRotation::set);
            dialog.getInput().createTextInput("End Scale", behindScale.get(), behindScale.get(), Double::parseDouble, behindScale::set);
            dialog.getInput().createSelectInput("Behind Ease", behindEase.get(), behindEase.get(), Ease.values(), behindEase::set);
            dialog.getInput().createTextInput("Random Duration Percentage", behindRandomDurationP.get(), behindRandomDurationP.get(), Double::parseDouble, behindRandomDurationP::set);
            dialog.getInput().createTextInput("Random Radius", behindRandomRadius.get(), behindRandomRadius.get(), Double::parseDouble, behindRandomRadius::set);
            dialog.getInput().createTextInput("Random Rotation", behindRandomRotation.get(), behindRandomRotation.get(), Double::parseDouble, behindRandomRotation::set);
            dialog.getInput().createTextInput("Random Scale Percentage", behindRandomScaleP.get(), behindRandomScaleP.get(), Double::parseDouble, behindRandomScaleP::set);

            dialog.setup();
            init.set(true);

        }

        /**
         * 영상 장식 추가
         */
        public void vfxGenVideoDecoration(String path) {

            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            final AtomicReference<String> parentFolder = new AtomicReference<>(((CSMainFrame) getMainFrame()).getEditor().generateOutputFolder());
            if (parentFolder.get() == null) {
                sendMessage("Load the level first.");
                return;
            }
            int count = 0;
            String firstSTR = "nevVDC";

            while (new File(parentFolder.get(), firstSTR + count).exists()) {
                count++;
            }
            firstSTR += count;

            dropper.setEnabled(false);

            String executeName = "Executed : Video Decorations";
            AtomicReference<String> filePath = new AtomicReference<>(path);
            AtomicInteger floor = new AtomicInteger(getFloorOnCenter());
            AtomicReference<Double> fps = new AtomicReference<>(30d);
            AtomicReference<String> decorationFolderName = new AtomicReference<>(firstSTR);
            AtomicReference<Point2D> position = new AtomicReference<>(Point2D.ORIGIN);
            AtomicReference<Double> rotation = new AtomicReference<>(0d);
            AtomicReference<Short> depth = new AtomicReference<>((short) 0);
            AtomicReference<Point2D> parallax = new AtomicReference<>(Point2D.ORIGIN);
            AtomicReference<String> decorationTag = new AtomicReference<>(firstSTR);
            AtomicReference<Double> angleOffset = new AtomicReference<>(0d);
            AtomicBoolean alpha = new AtomicBoolean(true);


            CSMultiDialog dialog = new CSMultiDialog(getMainFrame(), "Add Video Decoration", () -> {

                VideoFrame videoFrame = new VideoFrame(filePath.get());
                videoFrame.readAllFrames(fps.get());
                BufferedImage[] allFrames = videoFrame.getAllFrames();

                createEventButton(floor.get(),
                        new AddDecoration.Builder()
                                .setOpacity(0d)
                                .setPosition(position.get())
                                .setRotation(rotation.get())
                                .setParallax(parallax.get())
                                .setTag(Tag.of(decorationTag.get()))
                                .build(), false, false, false
                );
                CustomLevel level = ((CSMainFrame) getMainFrame()).getEditor().level();
                parentFolder.set(parentFolder.get() + "/" + decorationFolderName.get());
                File dest = new File(parentFolder.get());
                if (!dest.exists() && !dest.mkdir()) {
                    throw new IllegalStateException("File not found");
                }


                for (int i = 0; i < allFrames.length; i++) {
                    try {

                        BufferedImage frame = allFrames[i];
                        BitMapImage bitMapImage = new BitMapImage(frame);
                        String decorationName = Objects.toString(i);
                        if (alpha.get()) {
                            bitMapImage.getBitMap().getCanvas();
                            bitMapImage.blackToAlpha();
                        }
                        ImageIO.write(bitMapImage.getImage(), "png", new File(parentFolder.get(), decorationName + ".png"));
                        createEventButton(floor.get(),
                                new MoveDecorations.Builder()
                                        .setImage(new ImageFile(decorationFolderName.get() + "/" + decorationName + ".png"))
                                        .setOpacity(100d)
                                        .setAngleOffset(angleOffset.get() + level.getAngleOffsetByTimeSec(floor.get(), i / fps.get()))
                                        .setTag(Tag.of(decorationTag.get()))
                                        .build(), false, false, false
                        );
                    } catch (IOException e) {
                        Logger.getGlobal().log(Level.WARNING, e, () -> Arrays.toString(e.getStackTrace()));
                    }
                }

                timelinePanel.runTimelineFixedAction();
                createModifiedLog("Video Frame Decoration");
                sendMessage(executeName);

            });
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    dropper.setEnabled(true);
                }
            });
            dialog.getInput().createTextInput("Floor", floor.get(), floor.get(), Integer::parseInt, floor::set);
            dialog.getInput().createTextInput("File Path", filePath.get(), filePath.get(), v -> v, filePath::set);
            dialog.getInput().createTextInput("FPS", fps.get(), fps.get(), Double::parseDouble, fps::set);
            dialog.getInput().createTextInput("Image Prefix", decorationFolderName.get(), decorationFolderName.get(), v -> v, decorationFolderName::set);
            dialog.getInput().createTemplateInput("Position", position.get(), position.get(), Point2D.class, position::set, CSTemplates.getNoneMatchTemplatesProvider());
            dialog.getInput().createTextInput("Rotation", rotation.get(), rotation.get(), Double::parseDouble, rotation::set);
            dialog.getInput().createTextInput("Depth", depth.get(), depth.get(), Short::parseShort, depth::set);
            dialog.getInput().createTemplateInput("Parallax", parallax.get(), parallax.get(), Point2D.class, parallax::set, CSTemplates.getNoneMatchTemplatesProvider());
            dialog.getInput().createTextInput("Decoration Tag", decorationTag.get(), decorationTag.get(), v -> v, decorationTag::set);
            dialog.getInput().createTextInput("Angle Offset", angleOffset.get(), angleOffset.get(), Double::parseDouble, angleOffset::set);
            dialog.getInput().createBoolInput("Convert To Alpha", alpha.get(), alpha.get(), alpha::set);
            dialog.setup();

        }

        public void resetAllEditors() {
            analysisPanel.closeEditor();
            List.copyOf(allVirtualButtons).forEach(CSSwitchLocationVirtualButton::resetEditor);
        }

        /**
         * 대기 메시지 보내기
         */
        public void sendWaitMessage() {
            sendMessage("Please wait a moment...");
        }

        /**
         * 메시지 보내기
         */
        public void sendMessage(String message) {

            this.currentMessage = message;

            if (messageSenderThread != null) {
                messageSenderThread.interrupt();
                messageSenderThread = null;
            }
            messageSenderThread = TaskManager.runTask(() -> {
                try {
                    messagePanel.stopAnimation();
                    messagePanel.setLocation(25, -100);
                    messagePanel.moveAnimation(600, new Point2D(25, 0), Ease.OUT_BACK.fun());
                    Thread.sleep(1000);
                    messagePanel.moveAnimation(600, new Point2D(25, -100), Ease.IN_CUBIC.fun());
                    Thread.sleep(600);
                    this.currentMessage = null;
                    messageSenderThread = null;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        public void dropUI(boolean b) {
            this.dropUI = b;
        }

        @Override
        public void update() {
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            BitMapImage.highGraphics(g2);
            g2.setStroke(new BasicStroke(0.5f));

            paintLine(g2);
            paintBounds(g2);
            buttonPainter.refresh(button -> button.render(g2));
            paintCenterBar(g2);
            paintNumber(g2);
            paintSelectedArea(g2);

            if (dropUI) {
                if (!dropUITimerStart) {
                    dropUITimer = System.currentTimeMillis();
                    dropUITimerStart = true;
                }
                paintDropUI(g2);
            } else {
                if (dropUITimerStart) {
                    dropUITimer = System.currentTimeMillis();
                    dropUITimerStart = false;
                }
                paintDropUI(g2);
            }

            paintInfo(g2);
            Point loc = messagePanel.getLocation();
            if (currentMessage != null) {
                g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
                g2.drawString(currentMessage, loc.x, loc.y + 30);
            }
        }

        private void paintInfo(Graphics2D g) {
            int fontSize = 10;
            int lineNumber = 1;
            int sy = 15;
            int offset = 20;


            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
            List<CSSwitchLocationVirtualButton> currentEnabledButtons = linkedEventButtons.getSelectedButtons();
            g.setColor(Color.WHITE);

            if (currentEnabledButtons.size() == 1) {

                CSSwitchLocationVirtualButton button = currentEnabledButtons.get(0);

                Event event = button.getAttribute().getEventInfo().event();
                EID id = button.getAttribute().getEventInfo().eventID();

                if (button.getRealButton().isMarked()) {
                    paintInfo(g, new Color(150, 250, 150), "Marked ", "#" + (markedButtons.indexOf(button) + 1), fontSize, lineNumber);
                    lineNumber++;
                }

                if (event instanceof MoveDecorations e) {
                    paintInfo(g, new Color(150, 250, 250), "Tag : ", e.tag().toString().isBlank() ? "NONE" : e.tag(), fontSize, lineNumber);
                    lineNumber++;
                }

                if (event instanceof AddDecoration e) {
                    paintInfo(g, new Color(150, 250, 250), "Tag : ", e.tag().toString().isBlank() ? "NONE" : e.tag(), fontSize, lineNumber);
                    lineNumber++;
                }

                if (event instanceof EaseEvent e) {
                    paintInfo(g, new Color(100, 150, 250), "Ease : ", e.ease(), fontSize, lineNumber);
                    lineNumber++;
                }

                if (event instanceof AngleOffsetEvent e) {
                    paintInfo(g, new Color(250, 150, 250), "Angle Offset : ", e.angleOffset(), fontSize, lineNumber);
                    lineNumber++;
                }

                if (event instanceof DurationEvent<?> e) {
                    paintInfo(g, new Color(250, 150, 250), "Duration : ", e.duration(), fontSize, lineNumber);
                    lineNumber++;
                }

                if (event instanceof RelativeTileEvent e) {
                    paintInfo(g, new Color(250, 250, 150), "Range : ", e.startTile().floor(id.floor(), ((CSMainFrame) getMainFrame()).getEditor().level().getLength()) + " ~ " + e.endTile().floor(id.floor(), ((CSMainFrame) getMainFrame()).getEditor().level().getLength()), fontSize, lineNumber);
                    lineNumber++;
                }

                paintInfo(g, new Color(150, 150, 150), "Floor : ", id.floor(), fontSize, lineNumber);


            } else if (currentEnabledButtons.size() >= 2) {
                String s = "Selected : " + currentEnabledButtons.size();
                g.drawString(s, getWidth() - offset - StringUtils.getStringPixelLength(s, fontSize), getHeight() - lineNumber * sy);
            }
        }

        private void paintInfo(Graphics2D g, Color color, Object o1, Object o2, int fontSize, int lineNumber) {

            int sy = 15;
            int offset = 20;

            String s1 = o1.toString();
            String s2 = o2.toString();

            int len = StringUtils.getStringPixelLength(s2, fontSize);


            g.setColor(color);
            g.drawString(s2, getWidth() - offset - len, getHeight() - lineNumber * sy);

            g.setColor(Color.WHITE);
            g.drawString(s1, getWidth() - offset - len - StringUtils.getStringPixelLength(s1, fontSize), getHeight() - lineNumber * sy);

        }

        private long dropUITimer = 0;
        private boolean dropUITimerStart = false;

        private void paintDropUI(Graphics2D g) {
            long current = (System.currentTimeMillis() - dropUITimer);
            int opacity = 150;
            long millis = 300;

            g.setColor(new Color(0, 36, 0, (int) Math.max(0, Math.min(opacity, dropUITimerStart ? current * opacity / millis : opacity - current * opacity / millis))));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(0, 90, 0, (int) Math.max(0, Math.min(opacity, dropUITimerStart ? current * opacity / millis : opacity - current * opacity / millis))));
            int size = 50;

            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, size));
            g.drawString("Drop file here to load", getWidth() / 2 - 250, getHeight() / 2 + 20);
        }

        /**
         * 가로/세로선 그리기
         */
        private void paintLine(Graphics2D g) {

            int currentLayerSize = Math.max(MINIMUM_LAYERS, eventTimelineHorizontalBounds.size());

            for (int i = 0; i < xLine.size(); i++) {
                if (i == 0) {
                    g.setStroke(new BasicStroke(1f));
                } else {
                    g.setStroke(new BasicStroke(0.5f));
                }

                int x = toLocationX(xLine.get(i));

                if (x >= getWidth()) {
                    break;
                }


                if (0 < x && x < getWidth()) {
                    if (i == 0) {
                        g.setColor(new Color(40, 55, 40, 255));
                    } else {
                        g.setColor(new Color(30, 45, 30, 255));
                    }
                    g.drawLine(x, 0, x, getHeight());
                }
            }


            for (int i = 0; i <= currentLayerSize; i++) {

                if (i == 0) {
                    g.setStroke(new BasicStroke(1f));
                } else {
                    g.setStroke(new BasicStroke(0.5f));
                }

                int y = toLocationY((double)i * CSButton.BUTTON_HEIGHT);

                if (y >= getHeight()) {
                    break;
                }

                if (0 < y && y < getWidth()) {

                    if (i == 0) {
                        g.setColor(new Color(90, 165, 90, 120));
                    } else {
                        g.setColor(new Color(25, 30, 25, 255));
                    }
                    g.drawLine(0, y, getWidth(), y);
                }
            }

        }

        /**
         * 중앙 바 그리기
         */
        private void paintCenterBar(Graphics2D g) {
            if (getMainFrame().isShiftPressed()) {
                g.setColor(new Color(120, 195, 120, 255));
            } else {
                g.setColor(new Color(45, 81, 45, 200));
            }
            g.setStroke(new BasicStroke(2f));
            g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
        }

        /**
         * 숫자 그리기
         */
        private void paintNumber(Graphics2D g) {
            int prevTileNumberPanelPosition = -1000;
            int currentLayerSize = Math.max(MINIMUM_LAYERS, eventTimelineHorizontalBounds.size());
            boolean canWriteTileNumber = false;
            int fs = Math.max(13, resize(13));
            g.setFont(new Font(Font.SERIF, Font.BOLD, fs));
            for (int i = 0; i < xLine.size(); i++) {
                if (i == 0) {
                    g.setStroke(new BasicStroke(1f));
                } else {
                    g.setStroke(new BasicStroke(0.5f));
                }


                int x = toLocationX(xLine.get(i));

                if (prevTileNumberPanelPosition + 4 * fs < x) {
                    prevTileNumberPanelPosition = x;
                    canWriteTileNumber = true;
                }

                if (0 < x && x < getWidth() && canWriteTileNumber) { //Tile number
                    g.setColor(new Color(90, 165, 90));
                    g.drawString(String.valueOf(i), x + Math.max(10, resize(10)), Math.max(toLocationY(-30 * getIntervalYMultiplier()), 8 + fs));

                }
                canWriteTileNumber = false;
            }

            int prevLayerNumberPanelPosition = toLocationY(-30);
            boolean canWriteLayerNumber = false;

            g.setFont(new Font(Font.SERIF, Font.BOLD, Math.max(10, resize(10))));
            for (int i = 0; i <= currentLayerSize; i++) {

                int y = toLocationY((double)i * CSButton.BUTTON_HEIGHT);

                if (prevLayerNumberPanelPosition + 10 < y) {
                    prevLayerNumberPanelPosition = y;
                    canWriteLayerNumber = true;
                }

                if (0 < y && y < getWidth() && canWriteLayerNumber && i < currentLayerSize) { //Layer Number
                    g.setColor(new Color(45, 50, 45));
                    g.drawString(String.valueOf(i + 1), Math.max(toLocationX(-100 * getIntervalXMultiplier()), 10), toLocationY((double)i * CSButton.BUTTON_HEIGHT) + resize(17 / getIntervalYMultiplier()) + 2);
                }
                canWriteLayerNumber = false;
            }
        }

        /**
         * 경계 그리기
         */
        private void paintBounds(Graphics2D g) {
            int currentLayerSize = Math.max(MINIMUM_LAYERS, eventTimelineHorizontalBounds.size());
            Paint p = g.getPaint();
            g.setPaint(new GradientPaint(toLocationX(-600 * getIntervalXMultiplier()), 0, new Color(10, 10, 10), toLocationX(-100 * getIntervalXMultiplier()), 0, new Color(7, 7, 7, 60)));
            g.fillRect(0, 0, toLocationX(0), getHeight());
            g.setPaint(new GradientPaint(0, toLocationY((double)currentLayerSize * CSButton.BUTTON_HEIGHT + 600), new Color(10, 10, 10), 0, toLocationY((double)currentLayerSize * CSButton.BUTTON_HEIGHT + 100), new Color(7, 7, 7, 60)));
            g.fillRect(0, toLocationY((double)currentLayerSize * CSButton.BUTTON_HEIGHT), getWidth(), getHeight());
            g.setPaint(new GradientPaint(0, toLocationY(-300), new Color(10, 10, 10), 0, toLocationY(-50), new Color(7, 7, 7, 90)));
            g.fillRect(0, 0, getWidth(), toLocationY(-50 * getIntervalYMultiplier()) - 10);
            g.setPaint(new GradientPaint(toLocationX(-2000 * getIntervalXMultiplier()), 0, Color.BLACK, toLocationX(-600 * getIntervalXMultiplier()), 0, new Color(0, 0, 0, 0)));
            g.fillRect(0, 0, toLocationX(0), getHeight());
            g.setPaint(p);
        }

        private int toLocationX(double x) {
            return toLocation(x, 0).x;
        }

        private int toLocationY(double y) {
            return toLocation(0, y).y;
        }


        /**
         * 현재 선택된 영역 그리기
         */
        private void paintSelectedArea(Graphics2D g) {
            int startX = Math.min(selectedArea.startX(), selectedArea.endX());
            int startY = Math.min(selectedArea.startY(), selectedArea.endY());
            g.setColor(new Color(150, 190, 255, 30));
            g.fillRect(startX, startY, Math.abs(selectedArea.sizeX()), Math.abs(selectedArea.sizeY()));
        }

        /**
         * 다중 선택 시 사용합니다.
         *
         * @return 선택된 영역 내의 모든 button
         */
        private List<CSSwitchLocationVirtualButton> getButtonsInSelectedArea(Point2D start, Point2D end) {
            return getButtonsInSelectedArea(start.x(), start.y(), end.x(), end.y());
        }

        /**
         * 다중 선택 시 사용합니다.
         *
         * @return 선택된 영역 내의 모든 button
         */
        private List<CSSwitchLocationVirtualButton> getButtonsInSelectedArea(double sx, double sy, double ex, double ey) {
            List<CSSwitchLocationVirtualButton> result = new ArrayList<>();

            int y1 = getYLineNumber(sy);
            int y2 = getYLineNumber(ey);

            int startYLine = Math.min(y1, y2);
            int endYLine = Math.max(y1, y2);
            double areaStartX = Math.min(sx, ex);
            double areaEndX = Math.max(sx, ex);

            checkBounds(MINIMUM_LAYERS);

            for (int y = startYLine; y <= endYLine; y++) {
                List<Double> layerTimelines = eventTimelineHorizontalBounds.get(y);
                int index = ArrayFunction.searchIndex(layerTimelines, areaStartX);
                final int startIndex;
                if (index % 2 == 0) { //next : start
                    startIndex = index;
                } else { //next : end
                    startIndex = index - 1;
                }
                index = ArrayFunction.searchIndex(layerTimelines, areaEndX);
                final int endIndex;
                if (index % 2 == 0) { //next : start
                    endIndex = index - 1;
                } else { //next : end
                    endIndex = index;
                }
                //startIndex : even
                //endIndex : odd
                for (int i = startIndex; i + 1 <= endIndex; i += 2) {
                    double startX = layerTimelines.get(i);
                    double endX = layerTimelines.get(i + 1);
                    CSSwitchLocationVirtualButton virtualButton = locationToButtonMap.get(new ButtonAttribute.Location(startX, endX, y));
                    result.add(virtualButton);
                }
            }
            return result;
        }

        /**
         * 해당 위치로 타임라인을 이동합니다.
         *
         * @param x      x좌표
         * @param millis 이동 기간
         * @param ease   가감속
         */
        private void moveTimelineX(double x, long millis, FunctionEase ease) {
            move(new Point2D(x, Double.NaN), millis, ease);
            currWheelCoordinate.set(new Point2D(x, getCoordinate().y()));
        }

        /**
         * 해당 위치로 타임라인을 이동합니다.
         *
         * @param locationAttribute 가상 버튼의 위치 속성
         */
        void moveTimeline(ButtonAttribute.Location locationAttribute) {
            moveTimeline(locationAttribute, null);
        }


        /**
         * 해당 위치로 타임라인을 이동합니다.
         *
         * @param locationAttribute 가상 버튼의 위치 속성
         */
        private void moveTimeline(ButtonAttribute.Location locationAttribute, Axis axis) {
            double x = locationAttribute.startX();
            double y = locationAttribute.yLineNumber();
            if (axis == Axis.X_AXIS) {
                moveTimeline(x, getCoordinate().y(), Ease.OUT_CUBIC.fun());
            }
            if (axis == Axis.Y_AXIS) {
                moveTimeline(getCoordinate().x(), y, Ease.OUT_CUBIC.fun());
            }
            if (axis == null) {
                moveTimeline(x, y, Ease.OUT_CUBIC.fun());
            }
        }

        /**
         * 해당 위치로 타임라인을 이동합니다.
         *
         * @param x           x좌표
         * @param yLineNumber 레인 번호
         * @param ease        가감속
         */
        private void moveTimeline(double x, double yLineNumber, FunctionEase ease) {
            move(new Point2D(x, yLineNumber * CSButton.BUTTON_HEIGHT), PANEL_ANIMATE_DURATION, ease);
            currWheelCoordinate.set(new Point2D(x, yLineNumber * CSButton.BUTTON_HEIGHT));
        }

        /**
         * @return 타임라인 가운데에 있는 타일 번호
         */
        public int getFloorOnCenter() {
            return getFloor(getCoordinate().x());
        }

        /**
         * @return 해당 값에 있는 타일
         */
        public int getFloor(double t) {
            return ((CSMainFrame) getMainFrame()).getEditor().level().getFloor(t);
        }

        public double getCenterAngleOffset() {
            double x = getCoordinate().x();
            double floorX = getXLine(getFloorOnCenter());
            return x - floorX;
        }

        public double getXLine(int floor) {
            return xLine.get(floor);
        }

        /**
         * AngleData 가 수정되었을때, 세로선을 다시 긋습니다.
         */
        private void refreshLines() {
            CustomLevel customLevel = ((CSMainFrame) getMainFrame()).getEditor().level();
            xLine.clear();
            for (int i = 0; i <= customLevel.getLength(); i++) { //마지막 타일 이후 180도로 설정된 가상 타일은 없는 것으로 간주.
                double traveled = AdvancedMath.fixDouble(customLevel.getTraveledAngle(i));
                xLine.add(traveled);
            }
        }

        /**
         * 모든 이벤트의 버튼을 생성하고 새로고침합니다.
         */
        private void refreshButtons() {

            CustomLevel customLevel = ((CSMainFrame) getMainFrame()).getEditor().level();

            for (int i = 0; i <= customLevel.getLength(); i++) {
                for (int j = 0; j < customLevel.getFloorActions(i).size(); j++) {
                    EID id = new EID(i, j);
                    Actions actions = customLevel.getAction(id);
                    createIfAbsent(id, actions);
                }
                for (int j = 0; j < customLevel.getFloorDecorations(i).size(); j++) {
                    EID id = new EID(i, j);
                    Decorations decorations = customLevel.getDecoration(id);
                    createIfAbsent(id, decorations);
                }
            }

            buttonPainter.refreshImmediately();
        }

        /**
         * 현재 설정을 제거하고 새로운 레벨을 불러옵니다.
         */
        public void loadNewLevel(@Nullable CustomLevel level, String loadMessage) {

            dropper.setEnabled(false);
            functionPanel.disableAll();
            analysisPanel.hideToolPanels();
            this.loadMessage = loadMessage;

            if (level == null) { //취소한 경우
                dropper.setEnabled(true);
                functionPanel.enableAll();
                return;
            }

            if (loadedAnotherFile) {
                modifiedLogs.clear();
                currentTaskNumber = 0;
            }
            getMainFrame().disposeAll(); //모든 창 지우기
            timelinePanel.frameAnalyzerRefreshed = false; //프레임 분석기 새로 고침 준비
            linkedEventButtons.deselectAll(); //링크된 이벤트 모두 선택 해제
            analysisPanel.closeEditor(); //현재 편집중인 이벤트 닫기

            groupedButtons.clear(); //그룹화 제거
            eventComparatorButtonMap.clear(); //비교기 삭제

            ((CSMainFrame) getMainFrame()).getEditor().setCustomLevel(level); //새로운 레벨 설정
            eventTimelineHorizontalBounds.clear(); //기존 타임라인 지우기
            allVirtualButtons.clear(); //기존 데이터 지우기
            markedButtons = new ArrayList<>(); //마크 지우기

            if (backupThread != null) {
                backupThread.interrupt(); //백업 스레드 중지
                backupThread = null; //백업 스레드 초기화
            }

            preventAnalysis = true; // 새 레벨의 SetSpeed를 불러오기 위한 분석 방지
            allButtonsLoaded = false;
            timingModified = false; // 타이밍을 기본값으로 변경
            buttonPainter.refreshImmediately(); //즉시 새로고침
        }

        /**
         * 타이밍에 연관된 Event 의 설정이 변경될경우 모든 버튼의 위치를 재설정합니다.
         */
        public void reloadLevelIfChanged() {
            if (!preventAnalysis && timingModified) {
                timingModified = false;
                refreshLines();
                refreshButtons();
                timelinePanel.runTimelineFixedAction();

            }
        }

        /**
         * 각도 오프셋과 타일 번호를 이용하여 버튼의 위치 오프셋을 정의합니다.
         */
        private double getBoundOfX(int floor, double angleOffset) {
            CustomLevel customLevel = ((CSMainFrame) getMainFrame()).getEditor().level();
            return xLine.get(floor) + angleOffset * customLevel.getSettings().bpm() / customLevel.getBPM(floor);
        }

        public CSSwitchLocationVirtualButton getCurrentlyEditingEventButton() {
            return getEventButton(analysisPanel.getCurrentlyEditingEvent());
        }

        /**
         * 해당 이벤트의 버튼을 얻습니다.
         */
        public CSSwitchLocationVirtualButton getEventButton(Event event) {
            return eventComparatorButtonMap.get(new EventComparator(event));
        }

        /**
         * 가상 버튼이 존재할 경우 해당 버튼을 수정하고
         * 존재하지 않을경우 가상 버튼을 새로 만들어냅니다.
         */
        void createIfAbsent(EID eventID, @Nullable Event event) {
            if (event == null) {
                return;
            }

            double angleOffset = 0;
            if (event instanceof AngleOffsetEvent e) {
                angleOffset = Objects.requireNonNullElse(e.angleOffset(), 0.0);
            }

            double duration = 0;
            if (event instanceof DurationEvent<?> e) {
                duration = Objects.requireNonNullElse(e.duration(), 0.0).doubleValue();
            }

            if (((CSMainFrame) getMainFrame()).getEditor().level().needAnalyse()) {
                timingModified = true;
            }

            reloadLevelIfChanged();
            EventComparator comparator = new EventComparator(event);

            if (eventComparatorButtonMap.containsKey(comparator)) {
                CSSwitchLocationVirtualButton locationButton = eventComparatorButtonMap.get(comparator);
                ButtonAttribute prevAttribute = locationButton.getAttribute();

                removeBounds(prevAttribute.getLocationAttribute());
                locationToButtonMap.remove(prevAttribute.getLocationAttribute());

                locationButton.setAttribute(generateButtonLocation(eventID, event, angleOffset, duration));
                locationToButtonMap.put(locationButton.getAttribute().getLocationAttribute(), locationButton);

            } else {
                create(eventID, event, angleOffset, duration);
            }
        }

        /**
         * 버튼이 제거되거나 수정되어 기존 속성이 쓸모 없어졌을때, 해당 함수를 호출하여 경계를 지웁니다.
         */
        private void removeBounds(ButtonAttribute.Location attribute) {
            eventTimelineHorizontalBounds.get(attribute.yLineNumber()).remove(attribute.startX());
            eventTimelineHorizontalBounds.get(attribute.yLineNumber()).remove(attribute.endX());
            while (eventTimelineHorizontalBounds.get(eventTimelineHorizontalBounds.size() - 1).isEmpty() && MINIMUM_LAYERS < eventTimelineHorizontalBounds.size()) {
                eventTimelineHorizontalBounds.remove(eventTimelineHorizontalBounds.size() - 1);
                setMovingBoundaries(new RectBounds(0, 0, Integer.MAX_VALUE, eventTimelineHorizontalBounds.size() * CSButton.BUTTON_HEIGHT));
                move(getCoordinate(), 100, Ease.OUT_CUBIC.fun());
            }
        }

        private void checkBounds(int yLineNumber) {
            while (eventTimelineHorizontalBounds.size() <= Math.max(MINIMUM_LAYERS, yLineNumber)) {
                eventTimelineHorizontalBounds.add(new ArrayList<>());
            }
            setMovingBoundaries(new RectBounds(0, 0, Integer.MAX_VALUE, eventTimelineHorizontalBounds.size() * CSButton.BUTTON_HEIGHT));
        }


        /**
         * 이중 리스트를 사용하여, 시작 좌표와 끝 좌표만을 가지고 겹침 없이 최대한 빽빽하게 놓기
         */
        private ButtonAttribute generateButtonLocation(EID eventID, Event event, double startAngleOffset, @Nullable Double duration) {
            double endAngleOffset = startAngleOffset + Optional.ofNullable(duration).orElse(0.0) * 180;

            int floor = Math.max(0, eventID.floor());

            double startX = getBoundOfX(floor, startAngleOffset);
            double endX = getBoundOfX(floor, endAngleOffset);

            if (startX < -20) {
                startX = -20;
            }

            if (endX - startX < MIN_BUTTON_WIDTH) {
                endX = startX + MIN_BUTTON_WIDTH;
            }


            int yLineNumber;
            boolean added = false;

            for (yLineNumber = 0; yLineNumber < Integer.MAX_VALUE; yLineNumber++) {
                if (added) {
                    break;
                }
                checkBounds(yLineNumber);
                List<Double> layerTimelines = eventTimelineHorizontalBounds.get(yLineNumber);
                if (layerTimelines.isEmpty()) {
                    layerTimelines.add(startX);
                    layerTimelines.add(endX);
                    added = true;
                } else if (layerTimelines.get(0) > endX) {
                    layerTimelines.add(0, endX);
                    layerTimelines.add(0, startX);
                    added = true;
                } else {
                    int index = ArrayFunction.searchIndex(layerTimelines, startX);
                    //발견된 인덱스가 end 인덱스(홀수 인덱스)일 경우, start, end 사이에 start 가 들어가므로 겹침 확정. 다음 레인로 이동
                    if (index % 2 == 0) {
                        if (index == layerTimelines.size()) { //리스트의 크기는 항상 짝수이므로 마지막 항목에 대한 처리 역시 여기서 실행
                            layerTimelines.add(startX);
                            layerTimelines.add(endX);
                            added = true;
                        } else if (endX < layerTimelines.get(index)) { //발견된 인덱스는 start 인덱스, endX 가 해당 항목보다 작은지 검사
                            layerTimelines.add(index, endX);
                            layerTimelines.add(index, startX);
                            added = true;
                        }
                    }
                }
            }
            yLineNumber--;
            return new ButtonAttribute(this, new ButtonAttribute.Location(startX, endX, yLineNumber), new EIN.EventData(eventID, event), duration);

            // timeline
            //---------------------------------------------------------------
            // Layer 1     |                  | <- save end Position X
            //---------------------------------------------------------------
            // Layer 2                |                 |
            //---------------------------------------------------------------
            // Layer 3            |                                |
            // --------------------------------------------------------------
        }


        /**
         * {@link Event Event}를 하나 만드려면 해당 함수를 호출
         */
        public void createEventButton(Event event) {
            int floor = getFloorOnCenter();
            if (createEventButton(floor, event, true, getMainFrame().isControlPressed(), true) != null) {
                sendMessage("Event Created");
            }
        }

        /**
         * {@link Event Event}을 여러개 만드려면 해당 함수를 호출
         *
         * @return 생성 성공 시 : true
         */
        public boolean createEventButton(List<EIN.ABSEventData> events) {

            linkedEventButtons.deselectAll();
            List<CSSwitchLocationVirtualButton> createdButtons = new ArrayList<>();
            AtomicBoolean r = new AtomicBoolean(false);
            events.forEach(a -> {
                int finalFloor = a.floor();
                if (finalFloor <= ((CSMainFrame) getMainFrame()).getEditor().level().getLength()) {
                    CSSwitchLocationVirtualButton virtualButton = createEventButton(finalFloor, a.event(), false, false, false);
                    if (virtualButton != null) {
                        createdButtons.add(virtualButton);
                        r.set(true);
                    }
                }
            });
            linkedEventButtons.multiSelect(createdButtons);
            timelinePanel.runTimelineFixedAction();
            analysisPanel.openEditor();

            return r.get();
        }

        /**
         * {@link CSSwitchLocationVirtualButton Event}를 만드려면 해당 함수를 호출
         *
         * @return null : 중복 불가 이벤트 설치 시도 시 반환
         */
        @Nullable
        public CSSwitchLocationVirtualButton createEventButton(int floor, Event event, boolean select, boolean move, boolean refresh) {
            PACL editor = ((CSMainFrame) getMainFrame()).getEditor();
            if (eventComparatorButtonMap.containsKey(new EventComparator(event))) {
                event = Event.copyOf(event);
            }

            int address = event instanceof Actions ? editor.level().getFloorActions(floor).size() : editor.level().getFloorDecorations(floor).size();
            EID eventID = new EID(floor, address);

            if (!event.canDuplicated() && editor.level().containsEvent(floor, event.getClass())) {
                sendMessage("Cannot Duplicate : " + event.eventType());
                return null;
            }

            editor.level().createEvent(floor, event);
            createIfAbsent(eventID, event);

            CSSwitchLocationVirtualButton button = getEventButton(event);
            if (select) {
                button.select();
            }

            if (move) {
                ButtonAttribute attribute = button.getAttribute();
                moveTimeline(attribute.getLocationAttribute());
            }
            if (refresh) {
                timelinePanel.runTimelineFixedAction();
                createModifiedLog("Event Creation");
            }
            buttonPainter.refreshImmediately();
            return button;
        }

        /**
         * {@link Event Event}를 지우려면 해당 함수를 호출
         */
        public void removeEventButton(List<CSSwitchLocationVirtualButton> buttons) {
            buttons.forEach(button -> {
                EID eventID = button.getAttribute().getEventInfo().eventID();
                Event event = button.getAttribute().getEventInfo().event();
                deselect();
                removeBounds(button.getAttribute().getLocationAttribute());
                allVirtualButtons.remove(button);
                locationToButtonMap.remove(button.getAttribute().getLocationAttribute());
                if (event instanceof Actions) {
                    ((CSMainFrame) getMainFrame()).getEditor().level().destroyAction(eventID);
                } else {
                    ((CSMainFrame) getMainFrame()).getEditor().level().destroyDecoration(eventID);
                }
                if (((CSMainFrame) getMainFrame()).getEditor().level().needAnalyse()) {
                    timingModified = true;
                }
            });
            buttonPainter.refreshImmediately();
            reloadLevelIfChanged();
        }

        /**
         * 이벤트의 정보에 따라 가상 버튼을 적절한 위치에 생성
         */
        private void create(EID eventID, Event event, double startAngleOffset, @Nullable Double duration) {
            ButtonAttribute attribute = generateButtonLocation(eventID, event, startAngleOffset, duration);
            CSSwitchLocationVirtualButton eventButton = new CSSwitchLocationVirtualButton(analysisPanel, timelinePanel, attribute);

            allVirtualButtons.add(eventButton);
            eventComparatorButtonMap.put(new EventComparator(event), eventButton);
            locationToButtonMap.put(eventButton.getAttribute().getLocationAttribute(), eventButton);
        }

        /**
         * 타임라인 상의 y축 위치를 레인 위치로 변환합니다.
         */
        private int getYLineNumber(double yOffset) {
            return Math.max(0, Math.min((int) (yOffset / CSButton.BUTTON_HEIGHT), eventTimelineHorizontalBounds.size() - 1)); //버림
        }

        public CSLinkedSwitchLocationButton getLinkedButtons() {
            return linkedEventButtons;
        }


        @Override
        protected void setCoordinate(double x, double y) {
            super.setCoordinate(x, y);
            if (!preventAnalysis) {
                timelinePanel.runTimelineMovedAction();
            }
        }

        void createModifiedLog(String log) {
            if (currentTaskNumber + 1 < modifiedLogs.size()) {
                modifiedLogs.subList(currentTaskNumber + 1, modifiedLogs.size()).clear();
            }
            modifiedLogs.add(new ModifiedLog(new CustomLevel(((CSMainFrame) getMainFrame()).getEditor().level()), log));
            currentTaskNumber = modifiedLogs.size() - 1;
        }

        private void setTask(int taskNumber, String loadMessage) {
            if (isLoading()) {
                sendWaitMessage();
                return;
            }

            if (currentTaskNumber == taskNumber || taskNumber < 0 || taskNumber >= modifiedLogs.size()) {
                return;
            }

            currentTaskNumber = taskNumber;
            loadedAnotherFile = false;
            loadNewLevel(new CustomLevel(modifiedLogs.get(taskNumber).level), loadMessage);
        }

        private record ModifiedLog(
                CustomLevel level,
                String log
        ) {
            @Override
            public String toString() {
                return log;
            }
        }
    }
}



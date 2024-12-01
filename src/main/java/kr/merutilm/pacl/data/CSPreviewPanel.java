package kr.merutilm.pacl.data;

import kr.merutilm.base.exception.IllegalRenderStateException;
import kr.merutilm.base.io.BitMap;
import kr.merutilm.base.io.BitMapImage;
import kr.merutilm.base.parallel.RenderState;
import kr.merutilm.base.parallel.ShaderDispatcher;
import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.*;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.base.util.ConsoleUtils;
import kr.merutilm.base.util.TaskManager;
import kr.merutilm.customswing.CSCoordinatePanel;
import kr.merutilm.pacl.al.event.events.action.CustomBackground;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;
import kr.merutilm.pacl.al.event.selectable.*;
import kr.merutilm.pacl.inheritedshaders.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;

import static kr.merutilm.pacl.data.FrameAnalyzer.FRAME_RATE;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serial;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public final class CSPreviewPanel extends CSCoordinatePanel {
    @Serial
    private static final long serialVersionUID = 6373322983971074113L;
    private static final int TILE_TEXTURE_SCALE = 200;
    private static final double TILE_WIDTH = 150;
    private static final double MAX_TILE_SIZE = 2000;
    private static final int IMAGE_REFRESH_INTERVAL = 500;
    private int xRes = 640;
    private boolean showBackground = false;
    private boolean showVideoBackground = false;
    private boolean showDecoration = false;
    private boolean showTile = false;
    private boolean showForeground = false;
    private boolean showColorFilter = false;
    private boolean isRendering = false;
    private boolean forceRenderMode = false;
    private boolean preRenderMode = false;
    private transient List<EIN.EventData> listedDecorations;
    private static final Map<AngleImageKey, BufferedImage> ANGLE_IMAGE_MAP = new ConcurrentHashMap<>();
    private static final Map<String, BufferedImage> FILE_PATH_IMAGE_MAP = new ConcurrentHashMap<>();
    private final Set<String> requiredImages = new HashSet<>();
    private transient BufferedImage currentImage = BitMapImage.emptyImage(1, 1);
    private double zoomMultiplier = 1;
    private final transient RenderState renderState = new RenderState();
    private CSTimelinePanel timelinePanel;


    CSPreviewPanel(CSMainFrame master) {
        super(master, new Range(0.01, 1000), new Range(1, 1), new Range(1, 1));

        setZoom(1);
        setMovable(false);
        setCanAxisZoom(false);
        setBackground(new Color(0, 0, 0));
        setCanZoom(false);
        setMovingBoundaries(new RectBounds(-1000000, -1000000, 1000000, 1000000));

        Range range = new Range(0.1, 10);
        AtomicReference<Double> zoomMultiplier = new AtomicReference<>(1d);
        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() > 0 && zoomMultiplier.get() < range.max()) {// zoom out
                    double current = zoomMultiplier.get();
                    double end = Math.min(zoomMultiplier.get() * 1.111, range.max());
                    zoomMultiplier.set(end);

                    setZoomMultiplier(current, end);
                }
                if (e.getWheelRotation() < 0 && zoomMultiplier.get() > range.min()) {// zoom out
                    double current = zoomMultiplier.get();
                    double end = Math.max(zoomMultiplier.get() * 0.9, range.min());
                    zoomMultiplier.set(end);

                    setZoomMultiplier(current, end);
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isMiddleMouseButton(e)) {
                    double current = zoomMultiplier.get();
                    double end = 1;
                    zoomMultiplier.set(end);

                    setZoomMultiplier(current, end);
                }
            }
        });


    }

    public boolean isOpened() {
        return showTile || showVideoBackground || showDecoration || showBackground || showForeground || showColorFilter;
    }

    public void setTimeline(CSTimelinePanel timelinePanel) {
        this.timelinePanel = timelinePanel;
    }

    void reloadAll() {
        FILE_PATH_IMAGE_MAP.clear();
        reload();
    }

    void reload() {
        listedDecorations = ((CSMainFrame) getMainFrame()).getEditor().level().generateListedDecoration();
        if (isOpened()) {
            render();
        }
    }

    private transient Thread zoomThread;

    public void setZoomMultiplier(double start, double end) {

        if (zoomThread != null) {
            zoomThread.interrupt();
            zoomThread = null;
        }
        zoomThread = TaskManager.runTask(() -> {
            try {
                TaskManager.animate(400, e -> {
                    this.zoomMultiplier = AdvancedMath.ratioDivide(start, end, e);
                    render();
                }, Ease.OUT_CUBIC.fun());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

    }

    public void interruptRender() {
        renderState.createBreakpoint();
    }

    public int getCenterFrame() {
        if (timelinePanel == null) {
            throw new IllegalStateException("Timeline not generated");
        }
        return ((CSMainFrame) getMainFrame()).getAnalyzer().getFrame(timelinePanel.getPainter().getCoordinate().x());
    }

    @Override
    public void setZoom(double zoom) {
        super.setZoom(zoom * zoomMultiplier);
    }

    public void render() {
        if (timelinePanel == null || timelinePanel.isEventListing()) {
            return;
        }
        timelinePanel.tryRefreshFrameAnalyzerPanel();
        renderFrame(((CSMainFrame) getMainFrame()).getAnalyzer().getFrame(timelinePanel.getPainter().getCoordinate().x()));
    }

    public void renderFrame(int frame) {

        if (isRendering) {
            return;
        }

        isRendering = true;
        interruptRender();
        if (!forceRenderMode) {
            clearImage();
        }

        requiredImages.clear();

        int renderID = this.renderState.getId();

        CustomLevel level = ((CSMainFrame) getMainFrame()).getEditor().level();
        Settings settings = level.getSettings();
        FrameAnalyzer analyzer = ((CSMainFrame) getMainFrame()).getAnalyzer();

        final List<EIN.EventData> listedDecorations = List.copyOf(this.listedDecorations);
        final boolean showTile = this.showTile;
        final boolean showDecoration = this.showDecoration;
        final boolean showBackground = this.showBackground;
        final boolean showVideoBackground = this.showVideoBackground;
        final boolean showForeground = this.showForeground;
        final boolean showColorFilter = this.showColorFilter;
        final boolean preRenderMode = this.preRenderMode;
        double zoomMultiplier = this.zoomMultiplier;
        double initZoom = settings.zoom();
        boolean loopVideo = settings.loopVideo();
        int vidOffset = settings.videoOffset();

        int firstTileFrame = analyzer.getFrame(level.getTraveledAngle(1));


        List<Point2D> defaultTile = new ArrayList<>();
        List<Integer> absIntIncludedAngleList = new ArrayList<>();
        List<Double> angleExceptMidspinList = new ArrayList<>();
        List<Tile> frameTiles = new ArrayList<>();
        File videoFile = level.getSettings().bgVideo().getFile(((CSMainFrame) getMainFrame()).getEditor().generateOutputFolder());

        try {
            for (int floor = 0; floor <= level.getLength(); floor++) {
                if (showDecoration) {
                    defaultTile.add(analyzer.getDefaultTilePosition(floor));
                }
                if (showTile || showDecoration) {
                    frameTiles.add(analyzer.getTileState(frame, floor));
                }
                if (showTile) {
                    absIntIncludedAngleList.add((int) level.getAbsoluteIncludedAngle(floor));
                    angleExceptMidspinList.add(level.getAngleExceptMidSpin(floor));
                }
                renderState.tryBreak(renderID);
            }


            final Map<Filter, Double> currentFilter;
            List<VFXDecoration> currentDecorations = new ArrayList<>();
            if (showDecoration) {

                analyzer.computeAllMoveDecorations(listedDecorations);

                for (EIN.EventData listedDecoration : listedDecorations) {
                    if (listedDecoration.event() instanceof AddDecoration) {
                        VFXDecoration deco = analyzer.getDecorationState(frame, listedDecoration);
                        if (deco.isVisible()) {
                            requiredImages.add(deco.image().name());
                            currentDecorations.add(deco);
                        }
                    }

                    renderState.tryBreak(renderID);
                }

            }

            if (showBackground) {
                requiredImages.add(analyzer.getBackgroundState(frame).image().name());
            }


            if (showColorFilter) {
                currentFilter = Arrays.stream(Filter.values())
                        .collect(Collectors.toMap(
                                        e -> e,
                                        e -> analyzer.getFilterState(frame, e)
                                )
                        );
                renderState.tryBreak(renderID);

            } else {
                currentFilter = null;
            }

            VFXCamera currentCamera = analyzer.getCameraState(frame);
            VFXFlash currentFlash = analyzer.getFlashState(frame);
            VFXBloom currentBloom = analyzer.getBloomState(frame);
            CustomBackground currentBackground = analyzer.getBackgroundState(frame);

            renderState.tryBreak(renderID);

            Runnable r = () -> {
                try {
                    compute(
                            frame, renderID, zoomMultiplier, initZoom, firstTileFrame, loopVideo, vidOffset,
                            videoFile, absIntIncludedAngleList,
                            angleExceptMidspinList, defaultTile, frameTiles, currentCamera, currentFlash,
                            currentBackground, currentDecorations, currentFilter, currentBloom,
                            showTile, showDecoration, showBackground, showVideoBackground,
                            showForeground, showColorFilter, preRenderMode
                    );

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IllegalRenderStateException ignored) {
                    //noop
                }

            };

            if (forceRenderMode) {
                r.run();
            } else {
                TaskManager.runTask(r);
            }
        } catch (IllegalRenderStateException ignored) {
            //noop
        }
        isRendering = false;
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = ((Graphics2D) g);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(currentImage, 0, 0, getWidth(), getHeight(), null);

    }

    private void clearImage() {
        currentImage = BitMapImage.emptyImage(1, 1);
        repaint();
    }

    private void compute(int frame, int renderID, double zoomMultiplier, double initZoom, int firstTileFrame, boolean loopVideo, int vidOffset,
                         File videoFile, List<Integer> absIntIncludedAngleList, List<Double> angleExceptMidspinList,
                         List<Point2D> defaultTiles, List<Tile> currentTiles, VFXCamera camera, VFXFlash currentFlash, CustomBackground currentBackground,
                         List<VFXDecoration> currentDecorations, Map<Filter, Double> currentFilters, VFXBloom currentBloom,
                         boolean showTile, boolean showDecoration, boolean showBackground, boolean showVideoBackground,
                         boolean showForeground, boolean showColorFilter, boolean preRenderMode) throws InterruptedException, IllegalRenderStateException {

        renderState.tryBreak(renderID);

        int xRes = this.xRes;
        int yRes = (int) (xRes * getScreenRatio());

        AtomicBoolean tileRenderStarted = new AtomicBoolean(false);
        AtomicBoolean foregroundRenderStarted = new AtomicBoolean(false);
        AtomicBoolean colorFilterRenderStarted = new AtomicBoolean(false);
        AtomicBoolean wholeRendered = new AtomicBoolean(false);

        HexColor[] canvas = Collections.nCopies(xRes * yRes, currentBackground.backgroundColor()).toArray(HexColor[]::new);
        setCamera(camera, xRes);

        BackAndFrontDecorations analyzedDecorations;

        if (showDecoration) {
            List<ImageConvertedVFXDecoration> icdList = currentDecorations.stream()
                    .sorted(Comparator.comparing(e -> -e.depth()))
                    .map(t -> new ImageConvertedVFXDecoration(getImage(t.image()), t,
                            currentDecorations.stream()
                                    .filter(e -> e.maskingType() == MaskingType.MASK)
                                    .filter(e -> !e.useMaskingDepth() || e.maskingDepthRange().inRange(t.depth()))
                                    .map(e -> new ImageConvertedVFXDecoration(getImage(e.image()), e.edit()
                                            .setTarget(e.target()
                                                    .edit()
                                                    .setTile(Point2D.ORIGIN)
                                                    .setBlendMode(BlendMode.NONE)
                                                    .build()
                                            )
                                            .setColor(HexColor.WHITE)
                                            .setOpacity(100d)
                                            .build(), Collections.emptyList()
                                    ))
                                    .toList()
                    ))
                    .filter(e -> isShowing(initZoom, e, camera, zoomMultiplier, defaultTiles, xRes))
                    .toList();

            List<ImageConvertedVFXDecoration> back = new ArrayList<>();
            List<ImageConvertedVFXDecoration> front = new ArrayList<>();

            for (ImageConvertedVFXDecoration icd : icdList) {
                if (icd.decoration.depth() >= 0) {
                    back.add(icd);
                } else {
                    front.add(icd);
                }
            }

            analyzedDecorations = new BackAndFrontDecorations(back, front);
        } else {
            analyzedDecorations = null;
        }


        renderState.tryBreak(renderID);

        Thread t = null;
        if (!forceRenderMode) {
            t = new Thread(() -> {
                try {
                    Thread.sleep(100);
                    for (int i = 0; i < Integer.MAX_VALUE; i++) {

                        if (forceRenderMode || wholeRendered.get()) {
                            return;
                        }

                        HexColor[] copied = Arrays.copyOf(canvas, canvas.length);

                        if (preRenderMode && showTile && !tileRenderStarted.get()) {
                            renderTile(copied, renderID, absIntIncludedAngleList, angleExceptMidspinList, currentTiles, xRes);
                        }

                        if (preRenderMode && showForeground && !foregroundRenderStarted.get()) {
                            renderForeground(copied, renderID, currentFlash);
                        }

                        if (preRenderMode && showColorFilter && !colorFilterRenderStarted.get()) {
                            renderColorFilter(copied, renderID, currentFilters, currentBloom, xRes);
                        }

                        renderState.tryBreak(renderID);

                        if (forceRenderMode || wholeRendered.get()) {
                            return;
                        }

                        currentImage = new BitMap(xRes, yRes, copied).getImage();
                        repaint();

                        if (forceRenderMode || wholeRendered.get()) {
                            return;
                        }

                        Thread.sleep(IMAGE_REFRESH_INTERVAL);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    ConsoleUtils.logError(e);
                } catch (IllegalRenderStateException ignored) {
                    //noop
                }
            });
            t.start();
        }

        if (showVideoBackground) {
            renderVideoBackground(frame, renderID, canvas, firstTileFrame, loopVideo, vidOffset, videoFile, xRes);
        }
        if (showBackground) {
            renderBackground(canvas, renderID, initZoom, currentBackground, currentFlash, camera, zoomMultiplier, xRes);
        }
        if (showDecoration) {
            renderDecoration(canvas, renderID, initZoom, analyzedDecorations.back(), camera, zoomMultiplier, defaultTiles, xRes);
        }
        tileRenderStarted.set(true);
        if (showTile) {
            renderTile(canvas, renderID, absIntIncludedAngleList, angleExceptMidspinList, currentTiles, xRes);
        }
        if (showDecoration) {
            renderDecoration(canvas, renderID, initZoom, analyzedDecorations.front(), camera, zoomMultiplier, defaultTiles, xRes);
        }
        foregroundRenderStarted.set(true);
        if (showForeground) {
            renderForeground(canvas, renderID, currentFlash);
        }
        colorFilterRenderStarted.set(true);
        if (showColorFilter) {
            renderColorFilter(canvas, renderID, currentFilters, currentBloom, xRes);
        }
        wholeRendered.set(true);

        renderState.tryBreak(renderID);

        if (t != null) {
            t.join();
        }
        currentImage = new BitMap(xRes, yRes, canvas).getImage();
        repaint();

    }

    private void renderVideoBackground(int frame, int renderID, HexColor[] canvas, int firstTileFrame, boolean loopVideo, int vidOffset, File videoFile, int xRes) throws IllegalRenderStateException {
        if (videoFile == null) {
            return;
        }

        int yRes = (int) (xRes * getScreenRatio());
        BufferedImage img = new BitMap(xRes, yRes, canvas).getImage();

        VideoFrame videoFrame = new VideoFrame(videoFile);
        int videoFrameSize = videoFrame.getFrameLength();
        double videoFps = videoFrame.getVideoFps();

        int f = (int) ((frame + vidOffset * FRAME_RATE / 1000 - firstTileFrame) * videoFps / FRAME_RATE);

        if (loopVideo) {
            f = (f % videoFrameSize + videoFrameSize) % videoFrameSize;
        }

        renderState.tryBreak(renderID);
        if (f < videoFrameSize && f >= 0) {
            BufferedImage vidImage = BitMapImage.emptyImage(xRes, yRes);
            renderState.tryBreak(renderID);
            vidImage.createGraphics().drawImage(videoFrame.readFrame(f), 0, 0, xRes, yRes, null);
            renderState.tryBreak(renderID);
            img.createGraphics().drawImage(vidImage, 0, 0, xRes, yRes, null);
            renderState.tryBreak(renderID);
        }
        renderState.tryBreak(renderID);

        System.arraycopy(new BitMapImage(img).getBitMap().getCanvas(), 0, canvas, 0, canvas.length);
    }

    private void renderBackground(HexColor[] canvas, int renderID, double initZoom, CustomBackground currentBackground, VFXFlash currentFlashEffect, VFXCamera camera, double zoomMultiplier, int xRes) throws IllegalRenderStateException, InterruptedException {

        int yRes = (int) (xRes * getScreenRatio());

        boolean fitToScreen = currentBackground.bgDisplayMode() == BGDisplayMode.FIT_TO_SCREEN;
        boolean loop = currentBackground.loop() || currentBackground.bgDisplayMode() == BGDisplayMode.TILED;
        double scaleMultiplier = (currentBackground.scalingRatio() == null ? 1 : currentBackground.scalingRatio() / 100.0);
        BufferedImage image = getImage(currentBackground.image());


        double fitScaleX = image == null ? 0 : initZoom * 1800.0 / image.getWidth();
        double fitScaleY = image == null ? 0 : initZoom * 1800.0 * yRes / xRes / image.getHeight();


        Point2D finalScale = fitToScreen ? new Point2D(fitScaleX, fitScaleY).multiply(scaleMultiplier) : new Point2D(100, 100).multiply(scaleMultiplier);
        int tile = currentBackground.bgDisplayMode() == BGDisplayMode.TILED ? 1000001 : 1000000;

        VFXDecoration bgDecoration = new VFXDecoration(0,
                new AddDecoration.Builder()
                        .setLockScale(fitToScreen)
                        .setLockRotation(currentBackground.lockRot())
                        .setRelativeTo(RelativeTo.AddDecoration.GLOBAL)
                        .setTile(loop ? new Point2D(tile, tile) : new Point2D(1, 1))
                        .setBlendMode(BlendMode.NONE)
                        .build(),
                true,
                RelativeTo.MoveDecoration.GLOBAL,
                currentBackground.image(),
                Point2D.ORIGIN,
                Point2D.ORIGIN,
                Point2D.ORIGIN,
                0,
                loop ? finalScale.multiply(tile) : finalScale,
                currentBackground.imageColor(),
                100,
                Short.MAX_VALUE,
                Boolean.TRUE.equals(currentBackground.lockRot()) ? new Point2D(100, 100) : currentBackground.parallax(),
                null,
                false,
                null
        );

        HexColor flashColor = currentFlashEffect.backColor().opacity(currentFlashEffect.backOpacity() / 100);

        List<Point2D> defaultTile = new ArrayList<>();
        defaultTile.add(Point2D.ORIGIN); //배경의 기준 좌표는 글로벌이므로 첫 번째 원소만 사용함.

        ShaderDispatcher dispatcher = new ShaderDispatcher(renderState, renderID, new BitMap(xRes, yRes, canvas));
        dispatcher.createRenderer((x, y, xr, yr, rx, ry, i, c, t) -> {

            dispatcher.tryBreak();
            return c.blend(HexColor.ColorBlendMode.NORMAL, getColor(x, y, initZoom, new ImageConvertedVFXDecoration(image, bgDecoration, Collections.emptyList()), camera, zoomMultiplier, defaultTile, xRes))
                    .blend(HexColor.ColorBlendMode.NORMAL, flashColor);

        });
        dispatcher.dispatch();

    }

    private void renderForeground(HexColor[] canvas, int renderID, VFXFlash currentFlashEffect) throws IllegalRenderStateException {
        HexColor color = currentFlashEffect.frontColor().opacity(currentFlashEffect.frontOpacity() / 100);

        for (int i = 0; i < canvas.length; i++) {
            renderState.tryBreak(renderID);
            canvas[i] = canvas[i].blend(HexColor.ColorBlendMode.NORMAL, color);
        }
    }

    private void renderTile(HexColor[] canvas, int renderID, List<Integer> absIntIncludedAngleList, List<Double> angleExceptMidspinList, List<Tile> currentTiles, int xRes) throws IllegalRenderStateException {

        for (int floor = currentTiles.size() - 1; floor >= 0; floor--) { //뒤쪽 타일이 앞쪽 타일보다 뒤에 있으므로 뒤쪽 타일부터 설치

            renderState.tryBreak(renderID);
            drawTileOnCanvas(canvas, absIntIncludedAngleList.get(floor), angleExceptMidspinList.get(floor), currentTiles.get(floor), xRes);
        }
    }

    private void renderDecoration(HexColor[] canvas, int renderID, double initZoom, List<ImageConvertedVFXDecoration> targetDecorations, VFXCamera camera, double zoomMultiplier, List<Point2D> defaultTile, int xRes) throws InterruptedException, IllegalRenderStateException {
        int yRes = (int) (xRes * getScreenRatio());
        ShaderDispatcher dispatcher = new ShaderDispatcher(renderState, renderID, new BitMap(xRes, yRes, canvas));
        dispatcher.createRenderer(((x, y, xr, yr, rx, ry, i, c, t) -> getFinalColor(c, x, y, initZoom, targetDecorations, camera, zoomMultiplier, defaultTile, xRes)));
        dispatcher.dispatch();
    }


    public void renderColorFilter(HexColor[] canvas, int renderID, Map<Filter, Double> currentFilters, VFXBloom currentBloomEffect, int xRes) throws InterruptedException, IllegalRenderStateException {

        int yRes = (int) (xRes * getScreenRatio());
        BitMap bitMap = new BitMap(xRes, yRes, canvas);

        if (!currentFilters.get(Filter.SEPIA).isNaN()) {
            double intensity = currentFilters.get(Filter.SEPIA) / 100;
            new FilterSepia(renderState, renderID, bitMap, intensity).dispatch();
        }
        if (!currentFilters.get(Filter.GRAYSCALE).isNaN()) {
            double intensity = currentFilters.get(Filter.GRAYSCALE) / 100;
            new FilterGrayscale(renderState, renderID, bitMap, intensity).dispatch();
        }
        if (!currentFilters.get(Filter.INVERT_COLORS).isNaN()) {
            new FilterInvert(renderState, renderID, bitMap).dispatch();
        }
        if (!currentFilters.get(Filter.FISHEYE).isNaN()) {
            double intensity = currentFilters.get(Filter.FISHEYE) / 100;
            new FilterFisheye(renderState, renderID, bitMap, intensity).dispatch();
        }
        if (!currentFilters.get(Filter.FILM_GRAIN).isNaN()) {
            double intensity = currentFilters.get(Filter.FILM_GRAIN) / 100;
            new FilterFilmGrain(renderState, renderID, bitMap, intensity).dispatch();
        }
        if (currentBloomEffect.isValid()) {
            double intensity = currentBloomEffect.intensity() / 200;
            new FilterBloom(renderState, renderID, bitMap, intensity, currentBloomEffect).dispatch();
        }
        if (!currentFilters.get(Filter.ABERRATION).isNaN()) {
            double intensity = (currentFilters.get(Filter.ABERRATION) - 50) / 2500; //0 = ORIGINAL, 1 = WIDTH or HEIGHT
            new FilterAberration(renderState, renderID, bitMap, intensity).dispatch();
        }
        if (!currentFilters.get(Filter.CONTRAST).isNaN()) {
            double intensity = currentFilters.get(Filter.CONTRAST) / 100;
            new FilterContrast(renderState, renderID, bitMap, intensity).dispatch();
        }
        if (!currentFilters.get(Filter.POSTER).isNaN()) {
            double intensity = currentFilters.get(Filter.POSTER) / 100;
            new FilterPoster(renderState, renderID, bitMap, intensity).dispatch();
        }
        if (!currentFilters.get(Filter.BLUR).isNaN()) {
            double intensity = currentFilters.get(Filter.BLUR) / 128000;
            new FilterBlur(renderState, renderID, bitMap, intensity).dispatch();
        }
        if (!currentFilters.get(Filter.SHARPEN).isNaN()) {
            double value = currentFilters.get(Filter.SHARPEN) / 100;
            new FilterSharpen(renderState, renderID, bitMap, value).dispatch();
        }
        if (!currentFilters.get(Filter.GAUSSIAN_BLUR).isNaN()) {
            double intensity = currentFilters.get(Filter.GAUSSIAN_BLUR) / 128000;
            new FilterGaussianBlur(renderState, renderID, bitMap, intensity).dispatch();
        }
    }

    /**
     * 한 픽셀의 최종 색상을 반환합니다.
     *
     * @param x 픽셀 좌표 x
     * @param y 픽셀 좌표 y
     */
    private HexColor getFinalColor(HexColor firstColor, int x, int y, double initZoom, List<ImageConvertedVFXDecoration> targetDecorations, VFXCamera camera, double zoomMultiplier, List<Point2D> defaultTile, int xRes) {


        HexColor result = firstColor;
        for (ImageConvertedVFXDecoration icd : targetDecorations) {
            if (icd.decoration.maskingType() == MaskingType.MASK) {
                continue;
            }

            HexColor.ColorBlendMode blendMode = Objects.requireNonNullElse(icd.decoration.target().blendMode(), BlendMode.NONE).match();


            HexColor blend = getColor(x, y, initZoom, icd, camera, zoomMultiplier, defaultTile, xRes);
            result = result.blend(blendMode, blend, icd.decoration.opacity() / 100);
        }

        return result;
    }

    private boolean checkVisibleTile(Point2D p, Point2D s, int xRes) {
        int l = (int) (Math.hypot(TILE_TEXTURE_SCALE * s.x() / 100, TILE_TEXTURE_SCALE * s.y() / 100));
        int w = (int) (TILE_WIDTH * 200);

        // 스케일 100당 한쪽 방향으로 0.5타일, 1타일은 N픽셀. 사진 크기는 가로/세로 크기 적용 후 대각선 길이. 초기 사진 크기는 S픽셀.
        // 사진 한 개에 S 픽셀을 가야하므로 타일 이동량은 S/N 타일.
        // 스케일 / 100에 해당 수를 곱하면 끝 점을 구할수 있다
        // -> IMAGE_SIZE * SCALE / ( TILE_SIZE * 200 )

        Point2D p1 = p.edit()
                .add(-l * s.x() / w, -l * s.y() / w)
                .invertY()
                .multiply(getTileInterval(xRes))
                .build();

        Point2D p2 = p.edit()
                .add(l * s.x() / w, l * s.y() / w)
                .invertY()
                .multiply(getTileInterval(xRes))
                .build();

        return canVisible(p1.x(), p1.y(), p2.x(), p2.y(), xRes);

    }

    /**
     * 단일 타일을 캔버스 위에 그립니다
     */
    private void drawTileOnCanvas(HexColor[] canvas, int absIntIncludedAngle, double angleExceptMidspin, Tile tile, int xRes) {

        if (!checkVisibleTile(tile.positionOffset(), tile.scale(), xRes)) {
            return;
        }

        boolean inverseY = absIntIncludedAngle > 180;

        BufferedImage image = ANGLE_IMAGE_MAP.computeIfAbsent(new AngleImageKey(absIntIncludedAngle > 180 && absIntIncludedAngle != 360 ? 360 - absIntIncludedAngle : absIntIncludedAngle, tile.trackStyle()), e -> {
            InputStream is = Assets.getAssetStream(getImage(e));
            try {
                return is == null ? BitMapImage.emptyImage(1, 1) : ImageIO.read(is);
            } catch (IOException ex) {
                ConsoleUtils.logError(ex);
                return null;
            }
        });

        if (image == null || tile.opacity() <= 0 || tile.scale().x() == 0 || tile.scale().y() == 0 || tile.color().a() == 0) {
            return;
        }

        double multiplier = getTileInterval(xRes);

        double sx = tile.scale().x() / 100 * multiplier;
        double sy = tile.scale().y() / 100 * multiplier;

        double rotatedScale = AdvancedMath.hypotApproximate(sx * TILE_TEXTURE_SCALE / TILE_WIDTH, sy * TILE_TEXTURE_SCALE / TILE_WIDTH);
        Point2D coordinate = tile.positionOffset()
                .edit()
                .invertY()
                .multiply(multiplier)
                .build();

        Point location = toLocation(coordinate.x(), coordinate.y(), xRes);

        Point startPosition = new Point(
                location.x - resize(rotatedScale / 2),
                location.y - resize(rotatedScale / 2)
        );


        BitMapImage img = new BitMapImage(image);
        double mx = sx / TILE_WIDTH / getZoom();
        double my = (inverseY ? -1 : 1) * sy / TILE_WIDTH / getZoom();

        if (mx * TILE_TEXTURE_SCALE > MAX_TILE_SIZE || my * TILE_TEXTURE_SCALE > MAX_TILE_SIZE) {
            return;
        }

        img.scale(mx, my);
        img.rotate(getRotation() - (tile.rotationOffset() + angleExceptMidspin)); //반시계방향이므로 음수처리

        int yRes = (int) (xRes * getScreenRatio());
        double alphaMultiplier = tile.opacity() / 100;

        for (int x = Math.max(-startPosition.x, 0); x < img.getImage().getWidth() && x + startPosition.x < xRes; x++) {
            for (int y = Math.max(-startPosition.y, 0); y < img.getImage().getHeight() && y + startPosition.y < yRes; y++) {

                HexColor color = HexColor.fromAWT(new Color(img.getImage().getRGB(x, y))).blend(HexColor.ColorBlendMode.MULTIPLY, tile.color());

                double alpha = color.a() * alphaMultiplier * new Color(img.getImage().getRGB(x, y), true).getAlpha() / 255.0;

                double colorMultiplier = alpha > 255 ? alpha / 255 : 1;

                int fx = x + startPosition.x;
                int fy = y + startPosition.y;

                int i = getIndex(fx, fy, xRes);

                canvas[i] = canvas[i].blend(HexColor.ColorBlendMode.NORMAL, color.edit()
                        .setA((int) Math.min(255, alpha))
                        .functionExceptAlpha(e -> (int) AdvancedMath.restrict(0, 255, e * colorMultiplier))
                        .build()
                );
            }
        }
    }

    /**
     * <p>주어진 장식이 해당 패널 픽셀에 영향을 줄 때, 해당 픽셀의 색상을 얻습니다.</p>
     * <p></p>
     * See more :
     * <li>{@link CSPreviewPanel#getImagePixelLocation(double, double, double, ImageConvertedVFXDecoration, VFXCamera, double, List, int)}</li>
     * <li>{@link CSPreviewPanel#getAntiAliasPixelColor(Point2D, ImageConvertedVFXDecoration)}</li>
     *
     * @param px 패널 좌표 x
     * @param py 패널 좌표 y
     */
    private HexColor getColor(double px, double py, double initZoom,
                              ImageConvertedVFXDecoration icd, VFXCamera camera, double zoomMultiplier, List<Point2D> defaultTile, int xRes) {

        if (icd.targetImage == null) {
            return HexColor.TRANSPARENT;
        }
        double maskingAlphaOpacityRatio = 1;
        if (!icd.maskDecorations.isEmpty()) {
            maskingAlphaOpacityRatio = 0;
            for (ImageConvertedVFXDecoration maskDecoration : icd.maskDecorations) {
                maskingAlphaOpacityRatio = 1 - (1 - maskingAlphaOpacityRatio) * (1 - getColor(px, py, initZoom, maskDecoration, camera, zoomMultiplier, defaultTile, xRes).a() / 255.0);
            }
            if(icd.decoration.maskingType() == MaskingType.VISIBLE_OUTSIDE){
                maskingAlphaOpacityRatio = 1 - maskingAlphaOpacityRatio;
            }
        }

        Point2D imagePosition = getImagePixelLocation(px, py, initZoom, icd, camera, zoomMultiplier, defaultTile, xRes);

        if (!isValidPosition(icd.targetImage, imagePosition)) {
            return HexColor.TRANSPARENT;
        }

        HexColor color = getAntiAliasPixelColor(imagePosition, icd);

        return HexColor.get(
                color.r() * icd.decoration.color().r() / 255,
                color.g() * icd.decoration.color().g() / 255,
                color.b() * icd.decoration.color().b() / 255,
                (int)(color.a() * icd.decoration.color().a() * maskingAlphaOpacityRatio / 255)
        );
    }

    private boolean isValidPosition(BufferedImage targetImage, Point2D imagePosition) {
        return imagePosition.x() <= targetImage.getWidth() && imagePosition.y() <= targetImage.getHeight() && imagePosition.x() > 0 && imagePosition.y() > 0;
    }

    private boolean isShowing(double initZoom, ImageConvertedVFXDecoration icd, VFXCamera camera, double zoomMultiplier, List<Point2D> defaultTile, int xRes) {
        int yRes = (int) (xRes * getScreenRatio());
        BufferedImage image = icd.targetImage;

        if (image == null) {
            return false;
        }

        /*
         * 한 바퀴 순회 작업 개시, 다음의 경우 장식이 화면 내에 존재함
         * 1. x,y 부호가 모두 바뀌었는가? (내부에 존재)
         * 2. 부호가 항상 양수였지만, 좌표가 이미지 상에 존재하는가? (화면 끝에 걸쳐 있음)
         */

        boolean signChangedX = false;
        boolean signChangedY = false;

        boolean psx1 = false;
        boolean psx2 = false;
        boolean psy1 = false;
        boolean psy2 = false;

        for (int i = 0; i < xRes; i++) {
            Point2D loc1 = getImagePixelLocation(i, 0, initZoom, icd, camera, zoomMultiplier, defaultTile, xRes);
            Point2D loc2 = getImagePixelLocation(i, yRes, initZoom, icd, camera, zoomMultiplier, defaultTile, xRes);

            boolean sx1 = loc1.x() >= 0;
            boolean sx2 = loc2.x() >= 0;
            boolean sy1 = loc1.y() >= 0;
            boolean sy2 = loc2.y() >= 0;

            if (isValidPosition(image, loc1) || isValidPosition(image, loc2)) {
                return true;
            }
            if (i != 0) {
                if (sx1 != psx1 || sx2 != psx2) {
                    signChangedX = true;
                }
                if (sy1 != psy1 || sy2 != psy2) {
                    signChangedY = true;
                }
            }
            psx1 = sx1;
            psx2 = sx2;
            psy1 = sy1;
            psy2 = sy2;
        }

        for (int i = 0; i < yRes; i++) {
            Point2D loc1 = getImagePixelLocation(0, i, initZoom, icd, camera, zoomMultiplier, defaultTile, xRes);
            Point2D loc2 = getImagePixelLocation(xRes, i, initZoom, icd, camera, zoomMultiplier, defaultTile, xRes);
            boolean sx1 = loc1.x() >= 0;
            boolean sx2 = loc2.x() >= 0;
            boolean sy1 = loc1.y() >= 0;
            boolean sy2 = loc2.y() >= 0;

            if (isValidPosition(image, loc1) || isValidPosition(image, loc2)) {
                return true;
            }

            if (i != 0) {
                if (sx1 != psx1 || sx2 != psx2) {
                    signChangedX = true;
                }
                if (sy1 != psy1 || sy2 != psy2) {
                    signChangedY = true;
                }
            }
            psx1 = sx1;
            psx2 = sx2;
            psy1 = sy1;
            psy2 = sy2;
        }

        return signChangedX || signChangedY;
    }

    /**
     * <p>패널 위 좌표로 이미지상의 픽셀 위치를 계산합니다.</p>
     * <p>구해진 좌표가 사진의 범위를 벗어나면 null 을 반환합니다</p>
     * <p></p>
     * <h2><strong>Algorithms</strong></h2>
     * <p><img src="file:///C:/Users/nudet/Desktop/Main/Developments/Java/Projects/TMP2(Tkdydwk's Miraculous Private Project)/PACL/src/main/resources/javadoc/decorationAlg.png"></p>
     * <li>초기 픽셀 좌표(start)를 A라고 한다.</li>
     * <li>원점에서 시차, 중심점 오프셋과 회전값 등을 반영한 최종 좌표를 구한다. 이 점을 O라고 한다.</li>
     * <li>벡터 OA의 방향에서 회전값을 뺀 각도를 a라고 하면, 길이가 A이고 각도가 a인 새로운 벡터를 정의할 수 있다.</li>
     * <li>원점에서 해당 벡터를 더한다</li>
     *
     * @param px  패널 픽셀 x좌표
     * @param py  패널 픽셀 y좌표
     * @param icd 대상 장식 정보
     */
    @Nonnull
    private Point2D getImagePixelLocation(double px, double py, double initZoom, ImageConvertedVFXDecoration icd, VFXCamera camera, double zoomMultiplier, List<Point2D> defaultTile, int xRes) {

        VFXDecoration decoration = icd.decoration;
        BufferedImage image = icd.targetImage;

        if (image == null) {
            return Point2D.ORIGIN;
        }

        boolean lockRot = decoration.target().lockRotation();
        double pmx = (100 - decoration.parallax().x()) / 100;
        double pmy = (100 - decoration.parallax().y()) / 100;

        int floor = decoration.floor() == -1 ? 0 : Math.min(defaultTile.size() - 1, decoration.floor());


        double lockScaleMultiplier = Boolean.TRUE.equals(decoration.target().lockScale())
                ? camera.zoom() * zoomMultiplier / (initZoom / 100)
                : 1;

        Point2D scale = decoration.scale().multiply(lockScaleMultiplier);
        Point2D centerPosition; //이미지 중심(패널 상 위치)
        RelativeTo.MoveDecoration relativeTo = decoration.relativeTo();

        if (relativeTo == RelativeTo.MoveDecoration.CAMERA || relativeTo == RelativeTo.MoveDecoration.CAMERA_ASPECT) {

            int yRes = (int) (xRes * getScreenRatio());

            lockRot = true; //기본으로 적용되어있음

            Point2D panelPosition = decoration.positionOffset()
                    .edit()
                    .add(decoration.pivotOffset().edit()
                            .multiplyX(relativeTo == RelativeTo.MoveDecoration.CAMERA ? (double) yRes / xRes : 1)
                            .build()
                    )
                    .invertY()
                    .multiplyX((relativeTo == RelativeTo.MoveDecoration.CAMERA ? xRes : yRes) / 20.0)
                    .multiplyY(yRes / 20.0)
                    .addX(xRes / 2.0)
                    .addY(yRes / 2.0)
                    .build();

            Point2D pivotPoint = decoration.pivotOffset().edit()
                    .multiplyX(scale.x() / 100)
                    .multiplyY(scale.y() / 100)
                    .multiply(0.67) //0.67 is "pivot offset to parallax offset" constant
                    .rotate(decoration.rotationOffset())
                    .build();

            centerPosition = toCoordinate(panelPosition.x(), panelPosition.y(), xRes)
                    .edit()
                    .invertY()
                    .multiply(1 / getTileInterval(xRes))
                    .add(decoration.parallaxOffset()
                            .multiply(lockScaleMultiplier))
                    .add(pivotPoint)
                    .build();

        } else {


            Point2D tilePosition = defaultTile.get(floor);

            Point2D cameraPosition = getCoordinate()
                    .edit()
                    .invertY()
                    .multiply(1 / getTileInterval(xRes))
                    .build();

            centerPosition = new Point2D.Builder(
                    AdvancedMath.ratioDivide(cameraPosition.x(), tilePosition.x(), pmx),
                    AdvancedMath.ratioDivide(cameraPosition.y(), tilePosition.y(), pmy)
            )
                    .add(decoration.pivotOffset().edit()
                            .multiplyX(scale.x() / 100)
                            .multiplyY(scale.y() / 100)
                            .rotate(decoration.rotationOffset() + (lockRot ? camera.rotation() : 0))
                            .build()
                    ) //타일 오프셋만큼 더하기
                    .add(decoration.positionOffset().edit()
                            .multiplyX(pmx)
                            .multiplyY(pmy)
                            .build()
                    )
                    .add(decoration.parallaxOffset()
                            .multiply(lockScaleMultiplier)
                    )
                    .build(); //현재 '사진'의 최종 중심이 어디 있는지 표시합니다

        }

        Point2D coordinate = toCoordinate(px, py, xRes)
                .edit()
                .invertY()
                .multiply(1 / getTileInterval(xRes))
                .build();

        double distance = coordinate.distance(centerPosition); //사진의 중심과 시작 픽셀 간 거리

        //중심에 대한 상대 좌표
        double angle = Math.toDegrees(
                AdvancedMath.atan2Approximate(
                        coordinate.y() - centerPosition.y(),
                        coordinate.x() - centerPosition.x()
                )
        ) - decoration.rotationOffset(); //극좌표 각도 차이

        if (lockRot) {
            angle -= camera.rotation();
        }

        int width = image.getWidth();
        int height = image.getHeight();
        double rad = Math.toRadians(angle);

        Point2D imagePosition = new Point2D.Builder(distance * Math.cos(rad), distance * Math.sin(rad))
                .multiply(TILE_WIDTH) // 1칸은 150픽셀이다
                .multiplyX(100 / scale.x())
                .multiplyY(100 / scale.y()) //크기에 곱하기
                .addX(width / 2.0)
                .addY(height / 2.0) // 원점을 이미지 중심에서 왼쪽 위 끝으로 옮기기
                .build();


        return imagePosition.edit().invertY().addY(height).build();
    }

    /**
     * <p>이미지 위 좌표를 정수 범위에서 실수 범위로 확장하여 픽셀 색상을 계산합니다.</p>
     *
     * <p></p>
     * <h2><strong>Algorithms</strong></h2>
     * <p><img src="file:///C:/Users/nudet/Desktop/Main/Developments/Java/Projects/PACL/src/main/resources/javadoc/decorationTiling.png"></p>
     * <li>타일링을 반영하므로 위 사진과 같은 공식을 이용하여 최종적으로 정해진 픽셀의 색상을 추출한다.</li>
     * <li>이때, 주위 픽셀도 구해 색상 차를 보간하여 부드럽게 보이게 한다.</li>
     *
     * @param imagePosition 이미지 위치
     * @param icd           장식 정보
     * @return 픽셀 색상
     */
    private HexColor getAntiAliasPixelColor(Point2D imagePosition, ImageConvertedVFXDecoration icd) {

        BufferedImage image = icd.targetImage;
        Point2D tile = icd.decoration.target().tile();

        if (image == null) {
            return HexColor.TRANSPARENT;
        }

        int tx = Math.max(1, (int) tile.x());
        int ty = Math.max(1, (int) tile.y());


        double x1 = (imagePosition.x() * tx + 0.5 * (tx - 1)) % image.getWidth();
        double y1 = (imagePosition.y() * ty + 0.5 * (ty - 1)) % image.getHeight(); //픽셀 위치 오프셋 + 0.5, 타일링 오차 보정
        double x2 = Math.min(image.getWidth() - 1.0, x1 + 1);
        double y2 = Math.min(image.getHeight() - 1.0, y1 + 1);
        //타일링 반영 (범위 내 장식임이 위 if 문으로 확정됨.)

        double x = x1 % 1;
        double y = y1 % 1;


        HexColor c1 = HexColor.fromAWT(new Color(image.getRGB((int) x1, (int) y1), true));
        HexColor c2 = HexColor.fromAWT(new Color(image.getRGB((int) x2, (int) y1), true));
        HexColor c3 = HexColor.fromAWT(new Color(image.getRGB((int) x1, (int) y2), true));
        HexColor c4 = HexColor.fromAWT(new Color(image.getRGB((int) x2, (int) y2), true));

        HexColor cc1 = HexColor.ratioDivide(c1, c2, x);
        HexColor cc2 = HexColor.ratioDivide(c3, c4, x);

        return HexColor.ratioDivide(cc1, cc2, y);
    }

    private double getTileInterval(int xRes) {
        return 0.084 * xRes;
    }

    private ImageFile getImage(AngleImageKey key) {
        String imageName = "nev_travel_" + switch (key.style) {
            case NEON -> "n";
            default -> "s";
        };
        int angle;
        if (key.angle == 0) {
            angle = 0;
        } else {
            angle = Math.max(15, key.angle / 15 * 15);
        }

        return new ImageFile(imageName + angle + ".png");
    }

    @Nullable
    private BufferedImage getImage(ImageFile image) {

        return FILE_PATH_IMAGE_MAP.computeIfAbsent(image.name(), k -> {
            try {
                if (requiredImages.contains(k)) {
                    BufferedImage img = image.getImage(((CSMainFrame) getMainFrame()).getEditor().generateOutputFolder());
                    return img == null ? BitMapImage.emptyImage(1, 1) : img;
                } else return null;
            } catch (IOException e) {
                ConsoleUtils.logError(e);
                return null;
            }
        });
    }

    private void setCamera(VFXCamera c, int xRes) {
        move(c.position()
                .edit()
                .invertY()
                .multiply(getTileInterval(xRes))
                .build()
        );
        setRotation(c.rotation());
        setZoom(c.zoom());
    }

    private int getIndex(int x, int y, int xRes) {
        int yRes = (int) (xRes * getScreenRatio());
        return Matrix.convertLocation(x, y, xRes, yRes);
    }

    public void setShowDecoration(boolean showDecoration) {
        this.showDecoration = showDecoration;
        render();
    }

    public void setShowTile(boolean showTile) {
        this.showTile = showTile;
        render();
    }

    public void setShowBackground(boolean showBackground) {
        this.showBackground = showBackground;
        render();
    }

    public void setShowVideoBackground(boolean showVideoBackground) {
        this.showVideoBackground = showVideoBackground;
        render();
    }

    public void setShowForeground(boolean showForeground) {
        this.showForeground = showForeground;
        render();
    }

    public void setShowColorFilter(boolean showColorFilter) {
        this.showColorFilter = showColorFilter;
        render();
    }

    public void setPreRenderMode(boolean preRenderMode) {
        this.preRenderMode = preRenderMode;
        render();
    }

    /**
     * maxResolution 에 도달할 때까지 한 프레임 내로 강제로 렌더링할지 설정합니다.
     */
    public void setForceRenderMode(boolean forceRenderMode) {
        this.forceRenderMode = forceRenderMode;
        render();
    }

    /**
     * 최대 해상도를 설정합니다.
     */
    public void setXRes(int xRes) {
        this.xRes = xRes;
        render();
    }

    public boolean isDecorationRendering() {
        return showDecoration;
    }

    public boolean isTileRendering() {
        return showTile;
    }

    public boolean isBackgroundRendering() {
        return showBackground;
    }

    public boolean isVideoBackgroundRendering() {
        return showVideoBackground;
    }

    public boolean isForegroundRendering() {
        return showForeground;
    }

    public boolean isColorFilterRendering() {
        return showColorFilter;
    }

    public boolean isPreRenderMode() {
        return preRenderMode;
    }


    public int getXRes() {
        return xRes;
    }

    public boolean isForceRenderMode() {
        return forceRenderMode;
    }

    private record BackAndFrontDecorations(
            List<ImageConvertedVFXDecoration> back,
            List<ImageConvertedVFXDecoration> front
    ) {

    }

    private record ImageConvertedVFXDecoration(
            @Nullable BufferedImage targetImage,
            @Nonnull VFXDecoration decoration,
            @Nonnull List<ImageConvertedVFXDecoration> maskDecorations
    ) {

    }

    private record AngleImageKey(
            int angle,
            TrackStyle style
    ) {

    }

}


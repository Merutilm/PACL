package kr.merutilm.pacl.data;

import kr.merutilm.base.selectable.*;
import kr.merutilm.base.struct.*;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.base.util.TaskManager;
import kr.merutilm.pacl.al.event.events.AngleOffsetEvent;
import kr.merutilm.pacl.al.event.events.Event;
import kr.merutilm.pacl.al.event.events.RelativeTileEvent;
import kr.merutilm.pacl.al.event.events.TrackColorEvent;
import kr.merutilm.pacl.al.event.events.action.*;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;
import kr.merutilm.pacl.al.event.selectable.*;
import kr.merutilm.pacl.al.event.struct.EID;
import kr.merutilm.pacl.al.event.struct.RelativeTile;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public final class FrameAnalyzer {
    private CustomLevel level;
    private final List<DefaultTileState> tileState = new ArrayList<>();
    private final List<Point2D> cameraPosition = new ArrayList<>();
    public static final double FRAME_RATE = 100.0;
    private final Map<String, List<EIN.EventData>> tagMoveDecorationsMap = new ConcurrentHashMap<>();
    private final Map<Integer, List<EIN.EventData>> floorAffectsRecolorTrackMap = new ConcurrentHashMap<>();
    private final Map<Integer, List<EIN.EventData>> floorAffectsMoveTrackMap = new ConcurrentHashMap<>();
    private final Map<Filter, List<EIN.EventData>> filterListMap = new EnumMap<>(Filter.class);
    private Map<Class<? extends Event>, List<EIN.EventData>> listedAction;
    private FrameGenerator frameGenerator;
    private final AtomicBoolean refreshing = new AtomicBoolean(false);

    public FrameAnalyzer(CustomLevel level) {
        this.level = level;
        this.frameGenerator = new FrameGenerator(level);
    }

    /**
     * 이벤트를 등록합니다
     */
    private void listActions() {
        this.listedAction = level.generateListedAction();
        tagMoveDecorationsMap.clear();
        floorAffectsRecolorTrackMap.clear();
        floorAffectsMoveTrackMap.clear();
        filterListMap.clear();
    }

    /**
     * 레벨을 다시 불러옵니다
     */
    void reload(CustomLevel level) {
        TaskManager.lockThread(refreshing);
        refreshing.set(true);
        this.level = level;
        this.frameGenerator = new FrameGenerator(level);
        listActions();
        refreshTile();
        refreshing.set(false);
        TaskManager.notifyAvailableThread();
    }

    private List<EIN.EventData> sort(Collection<EIN.EventData> data) {

        return data.stream()
                .sorted(Comparator.comparing(e -> e.eventID().address()))
                .sorted(Comparator.comparing(e -> e.eventID().floor()))
                .sorted(Comparator.comparing(e -> level.getTraveledAngle(e.eventID().floor(), e.event() instanceof AngleOffsetEvent a ? a.angleOffset() : 0)))
                .toList();
    }

    /**
     * 모든 타일과 타일 기준 카메라를 새로고침합니다.
     */
    private void refreshTile() {

        tileState.clear();
        cameraPosition.clear();
        cameraPosition.add(new Point2D(0, 0));

        List<TileLocation> tilePosition = new ArrayList<>();
        List<TileColor> tileColor = new ArrayList<>();

        List<Point2D> tilePositionWithoutPositionTrack = new ArrayList<>();
        tilePositionWithoutPositionTrack.add(new Point2D(0, 0));

        List<Point2D> cameraPositionWithoutPositionTrack = new ArrayList<>();
        cameraPositionWithoutPositionTrack.add(new Point2D(0, 0));


        for (int floor = 0; floor < level.getLength(); floor++) {

            double multiplier = level.getRadius(floor + 1) / 100.0;
            double nextAngle = Math.toRadians(level.getAngleExceptMidSpin(floor + 1));

            Point2D currAnglePosition = tilePositionWithoutPositionTrack.get(floor);
            Point2D nextAnglePosition = currAnglePosition.add(Math.cos(nextAngle) * multiplier, Math.sin(nextAngle) * multiplier);

            Hold hold = floor == 0 ? null : level.getActivatedAction(floor, Hold.class);
            if (hold != null && !level.isMidSpinTile(floor) && level.getLength() > floor) {
                double angle = level.getAngleExceptMidSpin(floor + 1);
                double duration = hold.duration();
                double len = (duration + 1) * hold.distanceMultiplier() / 100;
                nextAnglePosition = nextAnglePosition.add(new PolarPoint(len, angle).coordinate());
            }
            tilePositionWithoutPositionTrack.add(nextAnglePosition); //패널상에서 Y 좌표는 음수
        }

        for (int floor = 0; floor < level.getLength(); floor++) {


            Point2D currCamPosition = cameraPositionWithoutPositionTrack.get(floor);
            Point2D nextCamPosition = tilePositionWithoutPositionTrack.get(floor + 1);

            double travel = level.getTravelAngle(floor);
            double ratio = Math.min(1, travel / 360);

            cameraPositionWithoutPositionTrack.add(Point2D.ratioDivide(currCamPosition, nextCamPosition, ratio));
        }

        Point2D offset = new Point2D(0, 0);
        double rotation = 0;
        double scale = 100;
        double opacity = 100;

        for (int floor = 0; floor <= level.getLength(); floor++) {

            Point2D currentPosition = tilePositionWithoutPositionTrack.get(floor);
            PositionTrack positionTrack = level.getActivatedAction(floor, PositionTrack.class);

            Point2D finalPosition;
            double finalRotation;
            double finalScale;
            double finalOpacity;


            if (positionTrack != null && !positionTrack.editorOnly()) {

                int targetTileFloor = positionTrack.relativeTo().floor(floor, level.getLength());

                targetTileFloor = Math.min(level.getLength(), targetTileFloor);
                targetTileFloor = Math.max(0, targetTileFloor);

                Point2D offsetAdd = positionTrack.positionOffset();


                finalPosition = tilePositionWithoutPositionTrack.get(targetTileFloor).add(offset.add(offsetAdd));
                finalRotation = positionTrack.rotation();
                finalScale = positionTrack.scale();
                finalOpacity = positionTrack.opacity();

                if (Boolean.FALSE.equals(positionTrack.justThisTile())) {
                    offset = finalPosition.add(currentPosition.invert());
                    rotation = finalRotation;
                    scale = finalScale;
                    opacity = finalOpacity;
                }

            } else {
                finalPosition = currentPosition.add(offset);
                finalRotation = rotation;
                finalScale = scale;
                finalOpacity = opacity;
            }

            tilePosition.add(new TileLocation(finalPosition, finalRotation, new Point2D(finalScale, finalScale), finalOpacity));
        }
        ColorTrack colorTrack = getColorTrackFromSettings();
        int colFloor = 0;

        for (int floor = 0; floor <= level.getLength(); floor++) {
            ColorTrack currentcColorTrack = level.getActivatedAction(floor, ColorTrack.class);
            if (currentcColorTrack != null) {
                colorTrack = currentcColorTrack;
                colFloor = floor;
            }
            tileColor.add(new TileColor(colFloor, colorTrack));
        }

        for (int floor = 0; floor <= level.getLength(); floor++) {
            tileState.add(new DefaultTileState(tilePosition.get(floor), tileColor.get(floor)));
        }


        Point2D currCamError = new Point2D(0, 0);
        Point2D nextCamError = new Point2D(0, 0);

        boolean stickToFloors = level.getSettings().stickToFloors();
        for (int floor = 0; floor < level.getLength(); floor++) {
            PositionTrack positionTrack = level.getActivatedAction(floor, PositionTrack.class);

            Point2D currCamPosition;
            Point2D nextCamPosition;
            stickToFloors = positionTrack != null && positionTrack.stickToFloors() != null && Boolean.TRUE.equals(!positionTrack.editorOnly()) ? positionTrack.stickToFloors() : stickToFloors;

            if (!stickToFloors) {
                currCamPosition = cameraPositionWithoutPositionTrack.get(floor).add(currCamError);
                nextCamPosition = tilePositionWithoutPositionTrack.get(floor + 1).add(nextCamError);
            } else {
                currCamPosition = cameraPosition.get(floor);
                nextCamPosition = tilePosition.get(floor).positionOffset();
                currCamError = currCamPosition.add(cameraPositionWithoutPositionTrack.get(floor).invert());
                nextCamError = nextCamPosition.add(tilePositionWithoutPositionTrack.get(floor + 1).invert());
            }

            double travel = level.getTravelAngle(floor);

            double ratio = Math.min(1, travel / 360);
            cameraPosition.add(level.containsEvent(floor, Hold.class) ? nextCamPosition : Point2D.ratioDivide(currCamPosition, nextCamPosition, ratio));
        }
    }

    @Nonnull
    private ColorTrack getColorTrackFromSettings() {
        Settings settings = level.getSettings();
        return new ColorTrack(
                true,
                settings.trackColorType(),
                settings.trackColor(),
                settings.secondaryTrackColor(),
                settings.trackColorAnimDuration(),
                settings.trackColorPulse(),
                settings.trackPulseLength(),
                settings.trackStyle(),
                settings.trackTexture(),
                settings.trackTextureScale(),
                settings.trackGlowIntensity()
        );
    }

    @Nonnull
    private EIN.EventData getAnimateTrackFromSettings() {
        Settings settings = level.getSettings();

        return new EIN.EventData(new EID(0, EID.TEMP),
                new AnimateTrack(
                        true,
                        settings.trackAnimation(),
                        settings.beatsAhead(),
                        settings.trackDisappearAnimation(),
                        settings.beatsBehind()
                )
        );
    }

    /**
     * 여행한 각도로 프레임 번호를 반환합니다.
     */
    public int getFrame(double traveled) {
        double sec = traveled / (3 * level.getSettings().bpm());
        return (int) Math.max(0, sec * FRAME_RATE);
    }

    /**
     * 프레임 번호로 여행한 각도를 반환합니다.
     */
    public double getTraveledAngle(int frame) {
        double sec = frame / FRAME_RATE;
        return sec * (3 * level.getSettings().bpm());
    }

    /**
     * 플레이어 기준 카메라 좌표를 반환합니다.
     */
    private Point2D defaultCamPosition(double traveled) {
        TaskManager.lockThread(refreshing);
        int currentFloor = level.getFloor(traveled);

        Point2D prev;
        Point2D next;
        double ratio;
        int tiles = level.getLength();
        if (currentFloor == tiles) {

            prev = cameraPosition.get(tiles);
            next = tileState.get(tiles).location().positionOffset();

            double min = level.getTraveledAngle(tiles);
            double max = level.getTraveledAngle(tiles) + 360;

            ratio = Math.min(1, AdvancedMath.getRatio(min, max, traveled));

        } else {

            prev = cameraPosition.get(currentFloor);
            next = cameraPosition.get(currentFloor + 1);
            ratio = AdvancedMath.getRatio(level.getTraveledAngle(currentFloor), level.getTraveledAngle(currentFloor + 1), traveled);
        }

        return Point2D.ratioDivide(prev, next, ratio);
    }

    /**
     * 매우 많은 장식의 State를 한 번에 얻고자 할때, 해당 기능을 사용하여 빠르게 처리할 수 있습니다.
     */
    public void computeAllMoveDecorations(List<EIN.EventData> listedDecorations) {

        if (!tagMoveDecorationsMap.isEmpty()) {
            return;
        }

        List<EIN.EventData> listedAction = this.listedAction.getOrDefault(MoveDecorations.class, Collections.emptyList());
        List<EIN.EventData> listedDecoration = listedDecorations.stream().filter(e -> e.event() instanceof AddDecoration).toList();

        Map<String, Set<EIN.EventData>> tagData = new HashMap<>();
        for (EIN.EventData ed : listedAction) {
            MoveDecorations event = (MoveDecorations) ed.event();
            event.tag().tags().forEach(e -> tagData.computeIfAbsent(e, k -> new HashSet<>()).add(ed));
        }
        for (EIN.EventData ed : listedDecoration) {
            AddDecoration event = (AddDecoration) ed.event();
            Tag tag = event.tag();
            Set<EIN.EventData> data = new HashSet<>();
            tag.tags().forEach(e -> data.addAll(tagData.getOrDefault(e, Collections.emptySet())));
            tagMoveDecorationsMap.put(tag.toString(), sort(data));
        }
    }

    /**
     * 해당 태그가 포함된 장식 이동 이벤트를 모두 얻습니다
     */
    public List<EIN.EventData> getMoveDecorations(Tag tag) {
        TaskManager.lockThread(refreshing);
        List<EIN.EventData> listedAction = this.listedAction.getOrDefault(MoveDecorations.class, Collections.emptyList());
        if (tag.toString().isBlank()) {
            return Collections.emptyList();
        }
        Set<String> tags = new HashSet<>(tag.tags());

        return tagMoveDecorationsMap.computeIfAbsent(tag.toString(),
                f -> sort(listedAction.stream()
                        .filter(e -> ((MoveDecorations) e.event()).tag().tags().stream().anyMatch(tags::contains))
                        .toList()
                )
        );
    }

    public List<EIN.EventData> getRecolorTrack(int floor) {
        TaskManager.lockThread(refreshing);
        List<EIN.EventData> listedActionRecolorTrack = this.listedAction.getOrDefault(RecolorTrack.class, Collections.emptyList());

        return floorAffectsRecolorTrackMap.computeIfAbsent(floor,
                f -> sort(listedActionRecolorTrack.stream()
                        .filter(e -> canAffectTarget(floor, e))
                        .toList()
                )
        );
    }

    public List<EIN.EventData> getMoveTrack(int floor) {
        TaskManager.lockThread(refreshing);
        List<EIN.EventData> listedActionMoveTrack = this.listedAction.getOrDefault(MoveTrack.class, Collections.emptyList());
        List<EIN.EventData> listedActionAnimateTrack = this.listedAction.getOrDefault(AnimateTrack.class, Collections.emptyList());

        return floorAffectsMoveTrackMap.computeIfAbsent(floor,
                f -> {
                    List<EIN.EventData> data = listedActionMoveTrack.stream()
                            .filter(e -> canAffectTarget(floor, e))
                            .collect(Collectors.toList());

                    if (floor == level.getLength()) {
                        return sort(data);
                    }


                    EIN.EventData targetAT = getAnimateTrackFromSettings();

                    for (EIN.EventData aed : listedActionAnimateTrack) {
                        if (aed.eventID().floor() > floor) {
                            break;
                        } else {
                            targetAT = aed;
                        }
                    }

                    double beatsBehindDelayMultiplier = level.getBPM(Math.max(0, targetAT.eventID().floor() - 1)) / level.getBPM(floor);

                    AnimateTrack atEvent = (AnimateTrack) targetAT.event();
                    RelativeTile targetTile = new RelativeTile(-1, RelativeTo.Tile.THIS_TILE);

                    double beatsBehindDelay = atEvent.beatsBehind() * beatsBehindDelayMultiplier;

                    MoveTrack.Builder mb = new MoveTrack.Builder()
                            .setStartTile(targetTile)
                            .setEndTile(targetTile)
                            .setDuration(0.5)
                            .setEase(Ease.INOUT_SINE)
                            .setAngleOffset(beatsBehindDelay * 180);


                    switch (atEvent.disappearAnimation()) {
                        case FADE -> mb.setOpacity(0d);
                        case SHRINK -> mb.setScale(Point2D.ORIGIN);
                        case RETRACT -> mb.setScale(Point2D.ORIGIN)
                                .setPositionOffset(getDefaultTilePosition(floor + 1).add(getDefaultTilePosition(floor).invert()));
                        case SHRINK_SPIN -> mb.setScale(Point2D.ORIGIN)
                                .setRotationOffset(-180d);
                        case SCATTER_FAR -> mb.setPositionOffset(Point2D.ORIGIN.getInCircleRandomPoint(7))
                                .setRotationOffset(AdvancedMath.random(-90, 90));
                        case SCATTER -> mb.setPositionOffset(Point2D.ORIGIN.getInCircleRandomPoint(4))
                                .setRotationOffset(AdvancedMath.random(-90, 90));
                        default -> {
                            //noop
                        }
                    }

                    if (atEvent.disappearAnimation() != TrackDisappearAnimation.NONE) {
                        data.add(new EIN.EventData(new EID(floor + 1, EID.TEMP), mb.build()));
                    }

                    return sort(data);
                });
    }

    public List<EIN.EventData> getFilter(Filter filter) {
        TaskManager.lockThread(refreshing);
        List<EIN.EventData> listedAction = this.listedAction.getOrDefault(SetFilter.class, Collections.emptyList());

        return filterListMap.computeIfAbsent(filter,
                f -> sort(listedAction.stream()
                        .filter(e -> ((SetFilter) e.event()).filter() == filter || ((SetFilter) e.event()).disableOthers())
                        .toList())
        );
    }

    public List<EIN.EventData> getMoveCamera() {
        return sort(this.listedAction.getOrDefault(MoveCamera.class, Collections.emptyList()));
    }

    public List<EIN.EventData> getCustomBackground() {
        return sort(this.listedAction.getOrDefault(CustomBackground.class, Collections.emptyList()));
    }

    public List<EIN.EventData> getFlash() {
        return sort(this.listedAction.getOrDefault(Flash.class, Collections.emptyList()));
    }

    public List<EIN.EventData> getBloom() {
        return sort(this.listedAction.getOrDefault(Bloom.class, Collections.emptyList()));
    }

    /**
     * MoveCamera 등 카메라 관련 이벤트가 적용된 최종 카메라 상태를 정의합니다
     */
    public VFXCamera getCameraState(int frame) {
        TaskManager.lockThread(refreshing);
        double traveled = getTraveledAngle(frame);
        List<EIN.EventData> listedAction = getMoveCamera();
        Settings settings = level.getSettings();

        AtomicReference<RelativeTo.Camera> relativeTo = new AtomicReference<>(settings.relativeTo());

        AtomicReference<Double> prevCamPositionX = new AtomicReference<>((double) 0);
        AtomicReference<Double> currCamPositionX = new AtomicReference<>(relativeTo.get() == RelativeTo.Camera.TILE || relativeTo.get() == RelativeTo.Camera.GLOBAL ? settings.position().x() : (defaultCamPosition(traveled).x() + settings.position().x()));
        AtomicReference<Double> camPositionXRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> camPositionXEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevCamPositionY = new AtomicReference<>((double) 0);
        AtomicReference<Double> currCamPositionY = new AtomicReference<>(relativeTo.get() == RelativeTo.Camera.TILE || relativeTo.get() == RelativeTo.Camera.GLOBAL ? settings.position().y() : (defaultCamPosition(traveled).y() + settings.position().y()));
        AtomicReference<Double> camPositionYRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> camPositionYEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevCamRotation = new AtomicReference<>((double) 0);
        AtomicReference<Double> currCamRotation = new AtomicReference<>(settings.rotation());
        AtomicReference<Double> camRotationRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> camRotationEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevCamZoom = new AtomicReference<>((double) 0);
        AtomicReference<Double> currCamZoom = new AtomicReference<>(settings.zoom() / 100);
        AtomicReference<Double> camZoomRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> camZoomEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevEventPositionX = new AtomicReference<>(settings.position().x());
        AtomicReference<Double> prevEventPositionY = new AtomicReference<>(settings.position().y());

        Point2D defaultCamPosition = defaultCamPosition(traveled);

        if (listedAction != null) {
            frameGenerator.generate(listedAction, MoveCamera.class, traveled, (eventFloor, event, ratio) -> {
                Point2D position = event.position() == null && (
                        event.relativeTo() == RelativeTo.Camera.LAST_POSITION ||
                        event.relativeTo() == RelativeTo.Camera.LAST_POSITION_NO_ROTATION
                ) ? Point2D.ORIGIN : event.position();

                relativeTo.set(Objects.requireNonNullElse(event.relativeTo(), relativeTo.get()));


                if (position != null) {
                    if (!Double.isNaN(position.x())) {
                        prevCamPositionX.set(currCamPositionX.get());
                        switch (relativeTo.get()) {
                            case TILE -> currCamPositionX.set(getDefaultTilePosition(eventFloor).x() + position.x());
                            case GLOBAL -> currCamPositionX.set(position.x());
                            case PLAYER -> currCamPositionX.set(defaultCamPosition.x() + position.x());
                            default -> currCamPositionX.updateAndGet(v -> v + position.x());
                        }
                        camPositionXRatio.set(ratio);
                        camPositionXEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                        prevEventPositionX.set(position.x());
                    }
                    if (!Double.isNaN(position.y())) {
                        prevCamPositionY.set(currCamPositionY.get());
                        switch (relativeTo.get()) {
                            case TILE -> currCamPositionY.set(getDefaultTilePosition(eventFloor).y() + position.y());
                            case GLOBAL -> currCamPositionY.set(position.y());
                            case PLAYER -> currCamPositionY.set(defaultCamPosition.y() + position.y());
                            default -> currCamPositionY.updateAndGet(v -> v + position.y());
                        }
                        camPositionYRatio.set(ratio);
                        camPositionYEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                        prevEventPositionY.set(position.y());
                    }
                }
                if (position == null && event.relativeTo() != null) {
                    switch (relativeTo.get()) {
                        case TILE -> {
                            currCamPositionX.set(tileState.get(eventFloor).location().positionOffset().x() + prevEventPositionX.get());
                            currCamPositionY.set(tileState.get(eventFloor).location().positionOffset().y() + prevEventPositionY.get());
                        }
                        case GLOBAL -> {
                            currCamPositionX.set(prevEventPositionX.get());
                            currCamPositionY.set(prevEventPositionY.get());
                        }
                        case PLAYER -> {
                            currCamPositionX.set(defaultCamPosition.x() + prevEventPositionX.get());
                            currCamPositionY.set(defaultCamPosition.y() + prevEventPositionY.get());
                        }
                        default -> {
                            currCamPositionX.updateAndGet(v -> v + prevEventPositionX.get());
                            currCamPositionY.updateAndGet(v -> v + prevEventPositionY.get());
                        }
                    }
                    camPositionXRatio.set(ratio);
                    camPositionXEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                    camPositionYRatio.set(ratio);
                    camPositionYEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                }

                if (event.rotation() != null) {
                    prevCamRotation.set(currCamRotation.get());
                    if (relativeTo.get() == RelativeTo.Camera.LAST_POSITION) {
                        currCamRotation.updateAndGet(v -> v + event.rotation());
                    } else {
                        currCamRotation.set(event.rotation());
                    }

                    camRotationRatio.set(ratio);
                    camRotationEase.set(event.ease() == null ? Ease.LINEAR : event.ease());

                }
                if (event.zoom() != null) {
                    prevCamZoom.set(currCamZoom.get());
                    currCamZoom.set(event.zoom() / 100);
                    camZoomRatio.set(ratio);
                    camZoomEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                }
            });
        }
        return new VFXCamera(new Point2D(
                AdvancedMath.ratioDivide(prevCamPositionX.get(), currCamPositionX.get(), camPositionXRatio.get(), camPositionXEase.get().fun()),
                AdvancedMath.ratioDivide(prevCamPositionY.get(), currCamPositionY.get(), camPositionYRatio.get(), camPositionYEase.get().fun())),
                AdvancedMath.ratioDivide(prevCamRotation.get(), currCamRotation.get(), camRotationRatio.get(), camRotationEase.get().fun()),
                AdvancedMath.ratioDivide(prevCamZoom.get(), currCamZoom.get(), camZoomRatio.get(), camZoomEase.get().fun()));
    }

    /**
     * MoveTrack, AnimateTrack 등 타일 관련 이벤트를 반영한 최종 타일 상태를 정의합니다
     */
    public Tile getTileState(int frame, int targetFloor) {
        TaskManager.lockThread(refreshing);
        double traveled = getTraveledAngle(frame);
        List<EIN.EventData> listedAction = getMoveTrack(targetFloor);

        AtomicReference<Double> prevPositionX = new AtomicReference<>((double) 0);
        AtomicReference<Double> currPositionX = new AtomicReference<>(tileState.get(targetFloor).location().positionOffset().x());
        AtomicReference<Double> positionXRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> positionXEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevPositionY = new AtomicReference<>((double) 0);
        AtomicReference<Double> currPositionY = new AtomicReference<>(tileState.get(targetFloor).location().positionOffset().y());
        AtomicReference<Double> positionYRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> positionYEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevRotation = new AtomicReference<>((double) 0);
        AtomicReference<Double> currRotation = new AtomicReference<>(tileState.get(targetFloor).location().rotationOffset());
        AtomicReference<Double> rotationRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> rotationEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevScaleX = new AtomicReference<>((double) 0);
        AtomicReference<Double> currScaleX = new AtomicReference<>(tileState.get(targetFloor).location().scale().x());
        AtomicReference<Double> scaleXRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> scaleXEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevScaleY = new AtomicReference<>((double) 0);
        AtomicReference<Double> currScaleY = new AtomicReference<>(tileState.get(targetFloor).location().scale().y());
        AtomicReference<Double> scaleYRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> scaleYEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevOpacity = new AtomicReference<>((double) 0);
        AtomicReference<Double> currOpacity = new AtomicReference<>(tileState.get(targetFloor).location().opacity());
        AtomicReference<Double> opacityRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> opacityEase = new AtomicReference<>(Ease.LINEAR);

        frameGenerator.generate(
                listedAction, MoveTrack.class, traveled, ((eventFloor, event, ratio) -> {
                    if (event.positionOffset() != null) {
                        if (!Double.isNaN(event.positionOffset().x())) {
                            prevPositionX.set(currPositionX.get());
                            currPositionX.set(tileState.get(targetFloor).location().positionOffset().x() + event.positionOffset().x());
                            positionXRatio.set(ratio);
                            positionXEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                        }
                        if (!Double.isNaN(event.positionOffset().y())) {
                            prevPositionY.set(currPositionY.get());
                            currPositionY.set(tileState.get(targetFloor).location().positionOffset().y() + event.positionOffset().y());
                            positionYRatio.set(ratio);
                            positionYEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                        }

                    }

                    if (event.rotationOffset() != null) {
                        prevRotation.set(currRotation.get());
                        currRotation.set(tileState.get(targetFloor).location().rotationOffset() + event.rotationOffset());
                        rotationRatio.set(ratio);
                        rotationEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                    }

                    if (event.scale() != null) {
                        if (!Double.isNaN(event.scale().x())) {
                            prevScaleX.set(currScaleX.get());
                            currScaleX.set(event.scale().x());
                            scaleXRatio.set(ratio);
                            scaleXEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                        }
                        if (!Double.isNaN(event.scale().y())) {
                            prevScaleY.set(currScaleY.get());
                            currScaleY.set(event.scale().y());
                            scaleYRatio.set(ratio);
                            scaleYEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                        }
                    }

                    if (event.opacity() != null) {
                        prevOpacity.set(currOpacity.get());
                        currOpacity.set(event.opacity());
                        opacityRatio.set(ratio);
                        opacityEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                    }

                }));


        TileColorState resultColor = getTrackColorState(frame, targetFloor);
        return new Tile(
                new Point2D(
                        AdvancedMath.ratioDivide(prevPositionX.get(), currPositionX.get(), positionXRatio.get(), positionXEase.get().fun()),
                        AdvancedMath.ratioDivide(prevPositionY.get(), currPositionY.get(), positionYRatio.get(), positionYEase.get().fun())
                ),
                AdvancedMath.ratioDivide(prevRotation.get(), currRotation.get(), rotationRatio.get(), rotationEase.get().fun()),
                new Point2D(
                        AdvancedMath.ratioDivide(prevScaleX.get(), currScaleX.get(), scaleXRatio.get(), scaleXEase.get().fun()),
                        AdvancedMath.ratioDivide(prevScaleY.get(), currScaleY.get(), scaleYRatio.get(), scaleYEase.get().fun())
                ),
                AdvancedMath.ratioDivide(prevOpacity.get(), currOpacity.get(), opacityRatio.get(), opacityEase.get().fun()),
                resultColor.trackStyle(),
                resultColor.color());
    }

    /**
     * ColorTrack, RecolorTrack 등 타일 색상 관련 이벤트를 반영한 최종 타일 색상 상태를 정의합니다
     */
    private TileColorState getTrackColorState(int frame, int floor) {
        TaskManager.lockThread(refreshing);
        double traveled = getTraveledAngle(frame);
        List<EIN.EventData> listedAction = getRecolorTrack(floor);
        TileColor currentTileColor = getDefaultTileColor(floor);

        AtomicReference<HexColor> prevColor = new AtomicReference<>(HexColor.WHITE);
        AtomicReference<HexColor> currColor = new AtomicReference<>(currentTileColor.event().trackColor());
        AtomicReference<HexColor> prevSecColor = new AtomicReference<>(HexColor.WHITE);
        AtomicReference<HexColor> currSecColor = new AtomicReference<>(currentTileColor.event().secondaryTrackColor());
        AtomicReference<Double> prevTrackColorAnimDuration = new AtomicReference<>((double) 1);
        AtomicReference<Double> currTrackColorAnimDuration = new AtomicReference<>(currentTileColor.event().trackColorAnimDuration());
        int prevTrackPulseLength = 1;
        int currTrackPulseLength = currentTileColor.event().trackPulseLength();
        AtomicReference<TrackColorType> prevTrackColorType = new AtomicReference<>(TrackColorType.SINGLE);
        AtomicReference<TrackColorType> currTrackColorType = new AtomicReference<>(currentTileColor.event().trackColorType());
        AtomicReference<TrackColorPulse> prevTrackColorPulse = new AtomicReference<>(TrackColorPulse.NONE);
        AtomicReference<TrackColorPulse> currTrackColorPulse = new AtomicReference<>(currentTileColor.event().trackColorPulse());

        AtomicReference<Double> colorRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> colorEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<TrackStyle> trackStyle = new AtomicReference<>(currentTileColor.event().trackStyle());
        AtomicInteger prevEventFloor = new AtomicInteger();
        AtomicInteger currEventFloor = new AtomicInteger();

        frameGenerator.generate(listedAction, RecolorTrack.class, traveled, (eventFloor, event, ratio) -> {
            prevEventFloor.set(currEventFloor.get());
            currEventFloor.set(eventFloor);
            prevColor.set(currColor.get());
            currColor.set(event.trackColor());
            prevSecColor.set(currSecColor.get());
            currSecColor.set(event.secondaryTrackColor());
            prevTrackColorAnimDuration.set(currTrackColorAnimDuration.get());
            currTrackColorAnimDuration.set(event.trackColorAnimDuration());
            prevTrackColorType.set(currTrackColorType.get());
            currTrackColorType.set(event.trackColorType());
            prevTrackColorPulse.set(currTrackColorPulse.get());
            currTrackColorPulse.set(event.trackColorPulse());
            trackStyle.set(event.trackStyle());
            colorRatio.set(ratio);
            colorEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
        });

        HexColor resultPrevColor = getTrackColor(prevTrackColorType.get(), floor, prevEventFloor.get(), prevColor.get(), prevSecColor.get(), traveled, prevTrackColorAnimDuration.get(), prevTrackPulseLength, prevTrackColorPulse.get());
        HexColor resultCurrColor = getTrackColor(currTrackColorType.get(), floor, currEventFloor.get(), currColor.get(), currSecColor.get(), traveled, currTrackColorAnimDuration.get(), currTrackPulseLength, currTrackColorPulse.get());
        return new TileColorState(trackStyle.get(), HexColor.ratioDivide(resultPrevColor, resultCurrColor, colorRatio.get(), colorEase.get().fun()));
    }

    /**
     * MoveDecorations 등 장식 이동 관련 이벤트가 적용된 최종 장식의 상태를 정의합니다.
     */
    public VFXDecoration getDecorationState(int frame, EIN.EventData target) {
        TaskManager.lockThread(refreshing);
        double traveled = getTraveledAngle(frame);
        AddDecoration decoration = (AddDecoration) target.event();
        List<EIN.EventData> listedAction = getMoveDecorations(decoration.tag());

        AtomicReference<Double> prevPositionX = new AtomicReference<>((double) 0);
        AtomicReference<Double> currPositionX = new AtomicReference<>(decoration.position().x());
        AtomicReference<Double> positionXRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> positionXEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevPositionY = new AtomicReference<>((double) 0);
        AtomicReference<Double> currPositionY = new AtomicReference<>(decoration.position().y());
        AtomicReference<Double> positionYRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> positionYEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevPivotX = new AtomicReference<>((double) 0);
        AtomicReference<Double> currPivotX = new AtomicReference<>(decoration.pivotOffset().x());
        AtomicReference<Double> pivotXRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> pivotXEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevPivotY = new AtomicReference<>((double) 0);
        AtomicReference<Double> currPivotY = new AtomicReference<>(decoration.pivotOffset().y());
        AtomicReference<Double> pivotYRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> pivotYEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevParOffX = new AtomicReference<>((double) 0);
        AtomicReference<Double> currParOffX = new AtomicReference<>(decoration.parallaxOffset() == null ? 0 : decoration.parallaxOffset().x());
        AtomicReference<Double> parOffXRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> parOffXEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevParOffY = new AtomicReference<>((double) 0);
        AtomicReference<Double> currParOffY = new AtomicReference<>(decoration.parallaxOffset() == null ? 0 : decoration.parallaxOffset().y());
        AtomicReference<Double> parOffYRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> parOffYEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevRotation = new AtomicReference<>((double) 0);
        AtomicReference<Double> currRotation = new AtomicReference<>(decoration.rotation());
        AtomicReference<Double> rotationRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> rotationEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevScaleX = new AtomicReference<>((double) 0);
        AtomicReference<Double> currScaleX = new AtomicReference<>(decoration.scale().x());
        AtomicReference<Double> scaleXRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> scaleXEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevScaleY = new AtomicReference<>((double) 0);
        AtomicReference<Double> currScaleY = new AtomicReference<>(decoration.scale().y());
        AtomicReference<Double> scaleYRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> scaleYEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<HexColor> prevColor = new AtomicReference<>(HexColor.WHITE);
        AtomicReference<HexColor> currColor = new AtomicReference<>(decoration.color());
        AtomicReference<Double> colorRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> colorEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevOpacity = new AtomicReference<>((double) 0);
        AtomicReference<Double> currOpacity = new AtomicReference<>(decoration.opacity());
        AtomicReference<Double> opacityRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> opacityEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Short> currDepth = new AtomicReference<>(decoration.depth());
        AtomicReference<ImageFile> currImage = new AtomicReference<>(decoration.image());

        AtomicBoolean currVisible = new AtomicBoolean(Objects.requireNonNullElse(decoration.visible(), true));
        AtomicReference<RelativeTo.MoveDecoration> currRelativeTo = new AtomicReference<>(RelativeTo.MoveDecoration.typeOf(decoration.relativeTo().toString())); //lastPosition 미적용
        AtomicReference<RelativeTo.MoveDecoration> relativeTo = new AtomicReference<>(currRelativeTo.get()); //lastPosition 적용

        AtomicReference<Double> prevParallaxX = new AtomicReference<>((double) 0);
        AtomicReference<Double> currParallaxX = new AtomicReference<>(decoration.parallax().x());
        AtomicReference<Double> parallaxXRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> parallaxXEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<Double> prevParallaxY = new AtomicReference<>((double) 0);
        AtomicReference<Double> currParallaxY = new AtomicReference<>(decoration.parallax().y());
        AtomicReference<Double> parallaxYRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> parallaxYEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<MaskingType> maskingType = new AtomicReference<>(Objects.requireNonNullElse(decoration.maskingType(), MaskingType.NONE));
        AtomicBoolean useMaskingDepth = new AtomicBoolean(Objects.requireNonNullElse(decoration.useMaskingDepth(), false));
        AtomicReference<Short> maskingFrontDepth = new AtomicReference<>(Objects.requireNonNullElse(decoration.maskingFrontDepth(), (short) -1));
        AtomicReference<Short> maskingBackDepth = new AtomicReference<>(Objects.requireNonNullElse(decoration.maskingBackDepth(), (short) -1));


        frameGenerator.generate(listedAction, MoveDecorations.class, traveled, (floor, event, ratio) -> {
            Point2D position = event.positionOffset();

            Boolean visible = event.visible();
            relativeTo.set(Objects.requireNonNullElse(event.relativeTo(), relativeTo.get()));
            currRelativeTo.set(relativeTo.get() == RelativeTo.MoveDecoration.LAST_POSITION ? currRelativeTo.get() : relativeTo.get());

            if (visible != null) {
                currVisible.set(visible);
            }

            if (position != null) {
                if (!Double.isNaN(position.x())) {
                    prevPositionX.set(currPositionX.get());
                    if (relativeTo.get() == RelativeTo.MoveDecoration.LAST_POSITION) {
                        currPositionX.updateAndGet(v -> v + position.x());
                    } else if (relativeTo.get() == RelativeTo.MoveDecoration.CAMERA) {
                        currPositionX.set(decoration.position().x() + position.x() * 1.5); //TODO : IN-GAME-BUG
                    } else {
                        currPositionX.set(decoration.position().x() + position.x());
                    }

                    positionXRatio.set(ratio);
                    positionXEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                }
                if (!Double.isNaN(position.y())) {
                    prevPositionY.set(currPositionY.get());
                    if (relativeTo.get() == RelativeTo.MoveDecoration.LAST_POSITION) {
                        currPositionY.updateAndGet(v -> v + position.y());
                    } else if (relativeTo.get() == RelativeTo.MoveDecoration.CAMERA) {
                        currPositionY.set(decoration.position().y() + position.y() * 1.5); //TODO : IN-GAME-BUG
                    } else {
                        currPositionY.set(decoration.position().y() + position.y());
                    }
                    positionYRatio.set(ratio);
                    positionYEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                }
            }

            if (event.pivotOffset() != null) {
                if (!Double.isNaN(event.pivotOffset().x())) {
                    prevPivotX.set(currPivotX.get());
                    currPivotX.set(event.pivotOffset().x());
                    pivotXRatio.set(ratio);
                    pivotXEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                }
                if (!Double.isNaN(event.pivotOffset().y())) {
                    prevPivotY.set(currPivotY.get());
                    currPivotY.set(event.pivotOffset().y());
                    pivotYRatio.set(ratio);
                    pivotYEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                }
            }

            if (event.parallaxOffset() != null) {
                if (!Double.isNaN(event.parallaxOffset().x())) {
                    prevParOffX.set(currParOffX.get());
                    currParOffX.set(event.parallaxOffset().x());
                    parOffXRatio.set(ratio);
                    parOffXEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                }
                if (!Double.isNaN(event.parallaxOffset().y())) {
                    prevParOffY.set(currParOffY.get());
                    currParOffY.set(event.parallaxOffset().y());
                    parOffYRatio.set(ratio);
                    parOffYEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                }
            }

            if (event.scale() != null) {
                if (!Double.isNaN(event.scale().x())) {
                    prevScaleX.set(currScaleX.get());
                    currScaleX.set(event.scale().x());
                    scaleXRatio.set(ratio);
                    scaleXEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                }
                if (!Double.isNaN(event.scale().y())) {
                    prevScaleY.set(currScaleY.get());
                    currScaleY.set(event.scale().y());
                    scaleYRatio.set(ratio);
                    scaleYEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                }
            }

            if (event.parallax() != null) {
                if (!Double.isNaN(event.parallax().x())) {
                    prevParallaxX.set(currParallaxX.get());
                    currParallaxX.set(event.parallax().x());
                    parallaxXRatio.set(ratio);
                    parallaxXEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                }
                if (!Double.isNaN(event.parallax().y())) {
                    prevParallaxY.set(currParallaxY.get());
                    currParallaxY.set(event.parallax().y());
                    parallaxYRatio.set(ratio);
                    parallaxYEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                }
            }

            if (event.rotationOffset() != null) {
                prevRotation.set(currRotation.get());
                currRotation.set(decoration.rotation() + event.rotationOffset());
                rotationRatio.set(ratio);
                rotationEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
            }
            if (event.color() != null) {
                prevColor.set(currColor.get());
                currColor.set(event.color());
                colorRatio.set(ratio);
                colorEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
            }
            if (event.opacity() != null) {
                prevOpacity.set(currOpacity.get());
                currOpacity.set(event.opacity());
                opacityRatio.set(ratio);
                opacityEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
            }
            if (event.depth() != null) {
                currDepth.set(event.depth());
            }
            if (event.image() != null) {
                currImage.set(event.image());
            }
            if (event.maskingType() != null) {
                maskingType.set(event.maskingType());
            }
            if (event.useMaskingDepth() != null) {
                useMaskingDepth.set(event.useMaskingDepth());
            }
            if (event.maskingFrontDepth() != null) {
                maskingFrontDepth.set(event.maskingFrontDepth());
            }
            if (event.maskingBackDepth() != null) {
                maskingBackDepth.set(event.maskingBackDepth());
            }
        });

        return new VFXDecoration(
                target.eventID().floor(),
                decoration,
                currVisible.get(),
                currRelativeTo.get(),
                currImage.get(),
                new Point2D(
                        AdvancedMath.ratioDivide(prevPositionX.get(), currPositionX.get(), positionXRatio.get(), positionXEase.get().fun()),
                        AdvancedMath.ratioDivide(prevPositionY.get(), currPositionY.get(), positionYRatio.get(), positionYEase.get().fun())
                ),
                new Point2D(
                        AdvancedMath.ratioDivide(prevPivotX.get(), currPivotX.get(), pivotXRatio.get(), pivotXEase.get().fun()),
                        AdvancedMath.ratioDivide(prevPivotY.get(), currPivotY.get(), pivotYRatio.get(), pivotYEase.get().fun())
                ),
                new Point2D(
                        AdvancedMath.ratioDivide(prevParOffX.get(), currParOffX.get(), parOffXRatio.get(), parOffXEase.get().fun()),
                        AdvancedMath.ratioDivide(prevParOffY.get(), currParOffY.get(), parOffYRatio.get(), parOffYEase.get().fun())
                ),
                AdvancedMath.ratioDivide(prevRotation.get(), currRotation.get(), rotationRatio.get(), rotationEase.get().fun()),
                new Point2D(
                        AdvancedMath.ratioDivide(prevScaleX.get(), currScaleX.get(), scaleXRatio.get(), scaleXEase.get().fun()),
                        AdvancedMath.ratioDivide(prevScaleY.get(), currScaleY.get(), scaleYRatio.get(), scaleYEase.get().fun())
                ),
                HexColor.ratioDivide(prevColor.get(), currColor.get(), colorRatio.get(), colorEase.get().fun()),
                AdvancedMath.ratioDivide(prevOpacity.get(), currOpacity.get(), opacityRatio.get(), opacityEase.get().fun()),
                currDepth.get(),
                new Point2D(
                        AdvancedMath.ratioDivide(prevParallaxX.get(), currParallaxX.get(), parallaxXRatio.get(), parallaxXEase.get().fun()),
                        AdvancedMath.ratioDivide(prevParallaxY.get(), currParallaxY.get(), parallaxYRatio.get(), parallaxYEase.get().fun())
                ),
                maskingType.get(),
                useMaskingDepth.get(),
                new IntRange(Math.min(maskingFrontDepth.get(), maskingBackDepth.get()), Math.max(maskingFrontDepth.get(), maskingBackDepth.get())));
    }

    /**
     * CustomBackground 등 배경 관련 이벤트를 반영한 최종 배경 상태를 정의합니다
     */
    public CustomBackground getBackgroundState(int frame) {
        TaskManager.lockThread(refreshing);
        double traveled = getTraveledAngle(frame);
        List<EIN.EventData> listedAction = getCustomBackground();

        Settings settings = level.getSettings();

        AtomicReference<CustomBackground> currentEvent = new AtomicReference<>(new CustomBackground(
                true,
                settings.backgroundColor(),
                settings.bgImage(),
                settings.bgImageColor(),
                settings.parallax(),
                settings.bgDisplayMode(),
                settings.imageSmoothing(),
                settings.lockRot(),
                settings.loopBG(),
                settings.scalingRatio(),
                0.0,
                Tag.of()
        ));
        frameGenerator.generate(listedAction, CustomBackground.class, traveled, (floor, event, ratio) -> currentEvent.set(event));

        return currentEvent.get();
    }

    /**
     * Flash 등 배경/전경 이벤트가 적용된 최종 배경, 전경을 정의합니다.
     */
    public VFXFlash getFlashState(int frame) {
        TaskManager.lockThread(refreshing);
        double traveled = getTraveledAngle(frame);
        List<EIN.EventData> listedAction = getFlash();

        AtomicReference<HexColor> prevBackColor = new AtomicReference<>(HexColor.WHITE);
        AtomicReference<HexColor> currBackColor = new AtomicReference<>(HexColor.WHITE);

        AtomicReference<Double> prevBackOpacity = new AtomicReference<>((double) 0);
        AtomicReference<Double> currBackOpacity = new AtomicReference<>((double) 0);

        AtomicReference<Double> backRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> backEase = new AtomicReference<>(Ease.LINEAR);

        AtomicReference<HexColor> prevFrontColor = new AtomicReference<>(HexColor.WHITE);
        AtomicReference<HexColor> currFrontColor = new AtomicReference<>(HexColor.WHITE);

        AtomicReference<Double> prevFrontOpacity = new AtomicReference<>((double) 0);
        AtomicReference<Double> currFrontOpacity = new AtomicReference<>((double) 0);

        AtomicReference<Double> frontRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> frontEase = new AtomicReference<>(Ease.LINEAR);


        frameGenerator.generate(
                listedAction, Flash.class, traveled, (eventFloor, event, ratio) -> {
                    if (event.plane() == Plane.BACKGROUND) {
                        prevBackColor.set(event.startColor());
                        currBackColor.set(event.endColor());

                        prevBackOpacity.set(event.startOpacity());
                        currBackOpacity.set(event.endOpacity());

                        backRatio.set(ratio);
                        backEase.set(event.ease() == null ? Ease.LINEAR : event.ease());

                    } else {
                        prevFrontColor.set(event.startColor());
                        currFrontColor.set(event.endColor());

                        prevFrontOpacity.set(event.startOpacity());
                        currFrontOpacity.set(event.endOpacity());

                        frontRatio.set(ratio);
                        frontEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
                    }
                }
        );


        return new VFXFlash(
                HexColor.ratioDivide(prevBackColor.get(), currBackColor.get(), backRatio.get(), backEase.get().fun()),
                AdvancedMath.ratioDivide(prevBackOpacity.get(), currBackOpacity.get(), backRatio.get(), backEase.get().fun()),
                HexColor.ratioDivide(prevFrontColor.get(), currFrontColor.get(), frontRatio.get(), frontEase.get().fun()),
                AdvancedMath.ratioDivide(prevFrontOpacity.get(), currFrontOpacity.get(), frontRatio.get(), frontEase.get().fun())
        );
    }

    /**
     * SetFilter 등 필터 설정 이벤트가 적용된 최종 필터 상태를 정의합니다.
     * 해당 필터가 꺼짐 상태인 경우, NaN 을 반환합니다.
     */
    public double getFilterState(int frame, Filter filter) {
        TaskManager.lockThread(refreshing);
        double traveled = getTraveledAngle(frame);
        List<EIN.EventData> listedAction = getFilter(filter);

        AtomicReference<Double> prevIntensity = new AtomicReference<>((double) 0);
        AtomicReference<Double> currIntensity = new AtomicReference<>(filter.defaultIntensity());
        AtomicReference<Double> intensityRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> intensityEase = new AtomicReference<>(Ease.LINEAR);
        AtomicBoolean enabled = new AtomicBoolean(false);

        frameGenerator.generate(listedAction, SetFilter.class, traveled, (floor, event, ratio) -> {
            if (event.filter() == filter) {
                enabled.set(event.enabled());
                prevIntensity.set(currIntensity.get());
                currIntensity.set(event.intensity());
                intensityRatio.set(ratio);
                intensityEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
            }

            if (event.filter() != filter && Boolean.TRUE.equals(event.disableOthers())) {
                enabled.set(false);
            }
        });

        return enabled.get() ? AdvancedMath.ratioDivide(prevIntensity.get(), currIntensity.get(), intensityRatio.get(), intensityEase.get().fun()) : Double.NaN;
    }

    /**
     * Bloom 등 빛/그림자 관련 이벤트가 적용된 최종 상태를 정의합니다.
     */
    public VFXBloom getBloomState(int frame) {
        TaskManager.lockThread(refreshing);
        double traveled = getTraveledAngle(frame);
        List<EIN.EventData> listedAction = getBloom();

        AtomicReference<Double> prevThreshold = new AtomicReference<>((double) 0);
        AtomicReference<Double> currThreshold = new AtomicReference<>((double) 50);

        AtomicReference<Double> prevIntensity = new AtomicReference<>((double) 0);
        AtomicReference<Double> currIntensity = new AtomicReference<>((double) 100);

        AtomicReference<HexColor> prevColor = new AtomicReference<>(HexColor.WHITE);
        AtomicReference<HexColor> currColor = new AtomicReference<>(HexColor.WHITE);

        AtomicReference<Double> transitionRatio = new AtomicReference<>((double) 1);
        AtomicReference<Ease> transitionEase = new AtomicReference<>(Ease.LINEAR);

        AtomicBoolean enabled = new AtomicBoolean(false);


        frameGenerator.generate(listedAction, Bloom.class, traveled, (floor, event, ratio) -> {
            enabled.set(event.enabled());

            prevThreshold.set(currThreshold.get());
            currThreshold.set(event.threshold());

            prevIntensity.set(currIntensity.get());
            currIntensity.set(event.intensity());

            prevColor.set(currColor.get());
            currColor.set(event.color());

            transitionRatio.set(ratio);
            transitionEase.set(event.ease() == null ? Ease.LINEAR : event.ease());
        });


        return enabled.get() ?
                new VFXBloom(
                        AdvancedMath.ratioDivide(prevThreshold.get(), currThreshold.get(), transitionRatio.get(), transitionEase.get().fun()),
                        AdvancedMath.ratioDivide(prevIntensity.get(), currIntensity.get(), transitionRatio.get(), transitionEase.get().fun()),
                        HexColor.ratioDivide(prevColor.get(), currColor.get(), transitionRatio.get(), transitionEase.get().fun())
                ) : VFXBloom.nullBloomEffect();
    }

    /**
     * 이벤트 기준 좌표가 해당 타일에 영향을 주면 true, 아니면 false 반환합니다.
     */
    private boolean canAffectTarget(int targetFloor, EIN.EventData e) {
        TaskManager.lockThread(refreshing);
        if (!(e.event() instanceof RelativeTileEvent event)) {
            return false;
        }

        RelativeTile startTile = event.startTile();
        RelativeTile endTile = event.endTile();
        Integer gap = event.gap();

        int startFloor = startTile.floor(e.eventID().floor(), level.getLength());
        int endFloor = endTile.floor(e.eventID().floor(), level.getLength());

        return new IntRange(startFloor, endFloor).inRange(targetFloor) && (gap == null || gap == 0 || (targetFloor - startFloor) % gap == 0);
    }

    public HexColor getTrackColor(TrackColorType type, int targetFloor, int eventFloor, HexColor color, HexColor secondary, double traveled, double trackColorAnimDuration, int trackPulseLength, TrackColorPulse trackColorPulse) {
        TaskManager.lockThread(refreshing);
        double timeSec = level.getTimeSecByAngleOffset(eventFloor, level.convertAngleOffset(0, traveled, eventFloor));
        double delaySec = (targetFloor - eventFloor) * Math.abs(trackColorAnimDuration / trackPulseLength);

        if (trackColorPulse == TrackColorPulse.FORWARD && trackPulseLength >= 2) {
            timeSec -= delaySec;
        }
        if (trackColorPulse == TrackColorPulse.BACKWARD && trackPulseLength >= 2) {
            timeSec += delaySec;
        }

        double ratio = (timeSec / trackColorAnimDuration % 1 + 1) % 1;

        HexColor result = HexColor.TRANSPARENT;
        switch (type) {
            case SINGLE, VOLUME -> result = color;
            case GLOW -> result = HexColor.ratioDivide(color, secondary, 1 - 2 * Math.abs(ratio - 0.5));
            case BLINK -> result = HexColor.ratioDivide(color, secondary, ratio);
            case SWITCH -> result = ratio < 0.5 ? color : secondary;
            case RAINBOW -> {

                double saturation = color.getSaturation();
                double a = color.a();
                HexColor grayScale = HexColor.get(color.r(), color.g(), color.b()).grayScale();
                HexColor maxSaturation;
                double r = ratio * 7 % 1;

                if (ratio < 1 / 7.0) {
                    maxSaturation = HexColor.ratioDivide(HexColor.R_RED, HexColor.R_ORANGE, r);
                } else if (ratio < 2 / 7.0) {
                    maxSaturation = HexColor.ratioDivide(HexColor.R_ORANGE, HexColor.R_YELLOW, r);
                } else if (ratio < 3 / 7.0) {
                    maxSaturation = HexColor.ratioDivide(HexColor.R_YELLOW, HexColor.R_GREEN, r);
                } else if (ratio < 4 / 7.0) {
                    maxSaturation = HexColor.ratioDivide(HexColor.R_GREEN, HexColor.R_BLUE, r);
                } else if (ratio < 5 / 7.0) {
                    maxSaturation = HexColor.ratioDivide(HexColor.R_BLUE, HexColor.R_INDIGO, r);
                } else if (ratio < 6 / 7.0) {
                    maxSaturation = HexColor.ratioDivide(HexColor.R_INDIGO, HexColor.R_VIOLET, r);
                } else {
                    maxSaturation = HexColor.ratioDivide(HexColor.R_VIOLET, HexColor.R_RED, r);
                }
                HexColor c = HexColor.ratioDivide(grayScale, maxSaturation, saturation);
                result = HexColor.get(c.r(), c.g(), c.b(), (int)a);
            }
            case STRIPES -> result = (targetFloor - eventFloor) % 2 == 0 ? color : secondary;
        }
        return result;
    }

    /**
     * 초기 타일 좌표를 반환합니다
     */
    Point2D getDefaultTilePosition(int floor) {
        TaskManager.lockThread(refreshing);
        return tileState.get(floor).location().positionOffset();
    }

    /**
     * 초기 타일 색상을 반환합니다
     */
    TileColor getDefaultTileColor(int floor) {
        TaskManager.lockThread(refreshing);
        return tileState.get(floor).color();
    }


    /**
     * 타일 색상
     */
    private record TileColor(int eventFloor,
                             TrackColorEvent event) {

    }


    /**
     * 기본 타일 상태
     */
    private record DefaultTileState(TileLocation location,
                                    TileColor color) {
    }

    private record TileColorState(TrackStyle trackStyle,
                                  HexColor color

    ) {

    }

}





package kr.merutilm.pacl.data;

import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.base.util.ArrayFunction;
import kr.merutilm.base.util.TaskManager;
import kr.merutilm.pacl.al.converter.Converter;
import kr.merutilm.pacl.al.event.events.*;
import kr.merutilm.pacl.al.event.events.action.*;
import kr.merutilm.pacl.al.event.events.decoration.Decorations;
import kr.merutilm.pacl.al.event.selectable.Planets;
import kr.merutilm.pacl.al.event.selectable.SpeedType;
import kr.merutilm.pacl.al.event.struct.EID;
import kr.merutilm.pacl.al.event.struct.Tag;
import kr.merutilm.pacl.al.generator.Generator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public final class CustomLevel {
    private final List<AngleData> angleData = new ArrayList<>();
    private Settings settings = new Settings.Builder().build();
    private final List<Boolean> currentFloorPlanets = new ArrayList<>(); //true : 3 , false : 2
    private final List<Boolean> currentFloorTwirl = new ArrayList<>(); //true : counterClockWise , false : clockWise
    private final List<Double> currentFloorBpm = new ArrayList<>();
    private final List<Double> currentFloorRadius = new ArrayList<>();
    private final List<Double> totalAngle = new ArrayList<>();
    private boolean bpmAnalyzed = false; //bpm 가장 먼저 분석할 요소
    private boolean travelAnalyzed = false; //twirl, planets, [totalAngle : BPM 우선 분석 필요]
    private boolean etcAnalyzed = false; //radius 마지막으로 분석할 요소
    private final AtomicBoolean bpmAnalyzing = new AtomicBoolean(false);
    private final AtomicBoolean travelAnalyzing = new AtomicBoolean(false);
    private final AtomicBoolean etcAnalyzing = new AtomicBoolean(false);
    private int totalEvents = 0;

    private CustomLevel() {
        createAngle(0d);
    }

    /**
     * Clone the level.
     */
    public CustomLevel(CustomLevel level) {
        angleData.addAll(level.angleData.stream().map(AngleData::new).toList());
        settings = level.settings;
        currentFloorPlanets.addAll(List.copyOf(level.currentFloorPlanets));
        currentFloorTwirl.addAll(List.copyOf(level.currentFloorTwirl));
        currentFloorBpm.addAll(List.copyOf(level.currentFloorBpm));
        currentFloorRadius.addAll(List.copyOf(level.currentFloorRadius));
        totalAngle.addAll(List.copyOf(level.totalAngle));
        bpmAnalyzed = level.bpmAnalyzed;
        travelAnalyzed = level.travelAnalyzed;
        etcAnalyzed = level.etcAnalyzed;
        bpmAnalyzing.set(level.bpmAnalyzing.get());
        travelAnalyzing.set(level.travelAnalyzing.get());
        etcAnalyzing.set(level.etcAnalyzing.get());
        totalEvents = level.totalEvents;
    }

    public static CustomLevel emptyLevel() {
        return new CustomLevel();
    }

    /**
     * 미드스핀 상수
     */
    public static final int MIDSPIN = 999;

    /**
     * 설정
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * 설정
     */
    public void setSettings(Settings settings) {
        if (this.settings.bpm() != settings.bpm()) {
            bpmAnalyzed = false;
        }
        this.settings = settings;
    }

    /**
     * 레벨이 분석된 상태인지 확인합니다.
     */
    public void waitUntilTravelAnalyze() {
        if (travelAnalyzing.get()) {
            TaskManager.lockThread(travelAnalyzing);
        }
        if (!travelAnalyzed) {
            travelAnalysis();
        }
    }

    /**
     * 레벨이 분석된 상태인지 확인합니다.
     */
    public void waitUntilBPMAnalyze() {
        if (bpmAnalyzing.get() || travelAnalyzing.get()) {
            TaskManager.lockThread(bpmAnalyzing);
            TaskManager.lockThread(travelAnalyzing);
        }
        if (!bpmAnalyzed) {
            bpmAnalysis();
        }
    }

    /**
     * 레벨이 분석된 상태인지 확인합니다.
     */
    public void waitUntilETCAnalyze() {
        if (etcAnalyzing.get()) {
            TaskManager.lockThread(etcAnalyzing);
        }
        if (!etcAnalyzed) {
            etcAnalysis();
        }
    }

    /**
     * 0번 타일부터 해당 타일까지 여행한 각도를 초기 BPM 기준으로 환산합니다.
     *
     * @param floor 타일 번호
     */
    public double getTraveledAngle(int floor) {
        return getTraveledAngle(floor, 0);
    }


    public double getTraveledAngle(int floor, double angleOffset) {
        waitUntilBPMAnalyze();
        waitUntilTravelAnalyze();

        return totalAngle.get(floor) + angleOffset * settings.bpm() / getBPM(floor);
    }

    /**
     * 레벨을 불러옵니다.
     */
    @Nullable
    static CustomLevel load(PACL editor, String filePath) {

        if (editor == null) {
            System.out.println("Loading...");
        }

        File file = FileIO.loadFile(filePath);

        if (file == null) {
            return null;
        }

        CustomLevel level = FileIO.readFile(editor, file);

        if (level != null) {
            if (editor == null) {
                System.out.println("Finished Loading!!");
            } else {
                editor.setInputFile(file);
            }
        }

        return level;
    }

    /**
     * 레벨을 불러옵니다.
     */
    public static CustomLevel load(String filePath) {
        return load(null, filePath);
    }

    /**
     * 지정된 경로에 레벨을 저장합니다.
     */
    public void save(String filePath) {
        printTotalEvents();
        System.out.println();
        System.out.println("Saving...");
        FileIO.parseFile(this, filePath);
    }

    private void printTotalEvents() {
        System.out.print("\rTotal Actions : " + totalEvents);
    }

    /**
     * 레벨 템플릿을 생성합니다.<p>
     * 다음이 포함되어 있습니다 : 10타일, 100 BPM, 그 외 기본 설정
     */
    public static CustomLevel createLevel() {
        CustomLevel customLevel = emptyLevel();
        for (int i = 0; i < 10; i++) {
            customLevel.createAngle(0);
        }
        return customLevel;
    }

    /**
     * @return 타일 개수
     */
    public int getLength() {
        return angleData.size() - 1;
    }

    /**
     * @param floor 타일 번호
     * @return 해당 타일이 미드스핀(오각형) 타일인지 검사합니다.
     */
    public boolean isMidSpinTile(int floor) {
        if (floor == getLength()) {
            return false;
        }
        return getAngle(floor + 1) == MIDSPIN;
    }

    /**
     * @param floor 타일 번호
     * @return 소용돌이가 적용된 상태인지 검사합니다.
     */
    public boolean isTwirling(int floor) {

        waitUntilTravelAnalyze();

        return unsafeIsTwirling(floor);
    }

    /**
     * @param floor 타일 번호
     * @return 소용돌이가 적용된 상태인지 검사합니다.
     */
    private boolean unsafeIsTwirling(int floor) {
        return currentFloorTwirl.get(floor);
    }

    /**
     * 소용돌이를 고려하지 않고 해당 타일이 연결된 타일과 이루는 각도를 얻습니다.
     *
     * @param floor 타일 번호
     * @return 절대 여행 각도
     */
    public double getAbsoluteIncludedAngle(int floor) {
        return calculateIncludedAngle(floor);
    }

    /**
     * 해당 타일이 연결된 앞뒤 타일과 이루는 각도를 얻습니다.
     *
     * @param floor 타일 번호
     * @return 각도
     */
    public double getIncludedAngle(int floor) {

        waitUntilTravelAnalyze();

        return unsafeGetIncludedAngle(floor);
    }

    private double unsafeGetIncludedAngle(int floor) {
        double angle = calculateIncludedAngle(floor);

        if (angle == 0) {
            return 0;
        }

        if (unsafeIsTwirling(floor) && angle != 360) {
            angle = 360 - angle;
        }

        return angle;
    }

    /**
     * 해당 타일에 공이 도달했을 때, 다음 타일까지의 여행 각도를 구합니다.
     */
    public double getTravelAngle(int floor) {
        waitUntilTravelAnalyze();
        return unsafeGetTravelAngle(floor);
    }

    private double unsafeGetTravelAngle(int floor) {
        double angle = unsafeGetIncludedAngle(floor);
        if (angle == 0) {
            return 0;
        }
        if (Boolean.TRUE.equals(currentFloorPlanets.get(floor)) && !isMidSpinTile(floor - 1)) {
            angle -= 60;
            if (angle <= 0) {
                angle += 360;
            }
        }
        Pause pause = getActivatedAction(floor, Pause.class);
        if (pause != null) {
            assert pause.duration() != null;
            angle += pause.duration() * 180;
        }
        Hold hold = getActivatedAction(floor, Hold.class);
        if (hold != null) {
            assert hold.duration() != null;
            angle += hold.duration() * 360;
        }

        return angle;
    }

    private double calculateIncludedAngle(int floor) {
        checkValidTravelAngle(floor);

        if (floor == getLength()) {
            return 180;
        }
        if (isMidSpinTile(floor)) {
            return 0;
        }
        double angle = 180 - getAngleExceptMidSpin(floor + 1) + getAngleExceptMidSpin(floor);
        while (angle <= 0) {
            angle += 360;
        }
        while (angle > 360) {
            angle -= 360;
        }
        return angle;
    }

    private void checkValidTravelAngle(int floor) {
        if (floor > getLength()) {
            throw new IndexOutOfBoundsException("current level tiles : " + getLength() + "\nprovided : " + floor);
        }
        if (floor < 0) {
            throw new IllegalArgumentException("floor must larger than 0!");
        }
    }

    /**
     * 발동 시각이 동일하도록 이벤트가 설치된 타일을 이동합니다
     */
    public double convertAngleOffset(int floor, double angleOffset, int destFloor) {
        double start = getTraveledAngle(floor, angleOffset);
        double dest = getTraveledAngle(destFloor);
        return (start - dest) * getBPM(destFloor) / settings.bpm();
    }

    /**
     * 각도 오프셋이 여행각도를 초과되지 않도록 조정합니다
     */
    public int convertFloor(int floor, double angleOffset) {
        return getFloor(getTraveledAngle(floor, angleOffset));
    }


    /**
     * @param floor 타일 번호
     * @return 해당 타일의 bpm
     */
    public double getBPM(int floor) {
        return getBPM(floor, 100);
    }

    /**
     * @param floor 타일 번호
     * @param pitch 피치
     * @return 해당 타일의 bpm
     */
    public double getBPM(int floor, double pitch) {
        waitUntilBPMAnalyze();
        return currentFloorBpm.get(floor) * pitch / 100;
    }

    /**
     * @param floor 타일 번호
     * @return 행성 간 거리
     */
    public double getRadius(int floor) {
        waitUntilETCAnalyze();
        return currentFloorRadius.get(floor);
    }

    /**
     * 선택된 타일의 각도를 얻습니다.
     *
     * @param floor 타일 번호
     * @return 각도(미드스핀은 999)
     */
    public double getAngle(int floor) {
        if (floor > getLength()) {
            throw new IndexOutOfBoundsException("current level tiles : " + getLength() + "\nprovided : " + floor);
        }
        if (floor < 0) {
            throw new IllegalArgumentException("floor must same or more than 0!");
        }

        return angleData.get(floor).angle();
    }

    public void setAngle(int floor, double angle) {
        angleData.get(floor).setAngle(angle);
    }

    /**
     * 선택한 타일을 밟고 일정 시간 뒤의 각도를 얻습니다.
     *
     * @param floor 타일 번호
     * @param sec   시간
     * @return 각도
     */
    public double getAngleOffsetByTimeSec(int floor, double sec) {
        return getAngleOffsetByTimeSec(floor, sec, 100);
    }

    /**
     * 선택한 타일을 밟고 일정 시간 뒤의 각도를 얻습니다.
     *
     * @param floor 타일 번호
     * @param sec   시간
     * @param pitch 피치
     * @return 각도
     */
    public double getAngleOffsetByTimeSec(int floor, double sec, int pitch) {
        return sec * getBPM(floor, pitch) * 3;
    }

    /**
     * 선택한 타일을 밟고 해당 각도에 도달하는 시각을 얻습니다.
     *
     * @param floor       타일 번호
     * @param angleOffset 각도 오프셋
     * @return 시각(초)
     */
    public double getTimeSecByAngleOffset(int floor, double angleOffset) {
        return getTimeSecByAngleOffset(floor, angleOffset, 100);
    }

    /**
     * 선택한 타일을 밟고 해당 각도에 도달하는 시각을 얻습니다.
     *
     * @param floor       타일 번호
     * @param angleOffset 각도 오프셋
     * @param pitch       피치
     * @return 시각(초)
     */
    public double getTimeSecByAngleOffset(int floor, double angleOffset, int pitch) {
        return getTimeSecByDuration(floor, angleOffset / 180, pitch);
    }

    /**
     * 선택한 타일을 밟고 일정 기간(비트) 뒤의 시각을 얻습니다.
     *
     * @param floor    타일 번호
     * @param duration 기간
     * @return 시각(초)
     */
    public double getTimeSecByDuration(int floor, double duration) {
        return getTimeSecByDuration(floor, duration, 100);
    }

    /**
     * 선택한 타일을 밟고 일정 기간(비트) 뒤의 시각을 얻습니다.
     *
     * @param floor    타일 번호
     * @param duration 기간
     * @param pitch    피치
     * @return 시각(초)
     */
    public double getTimeSecByDuration(int floor, double duration, int pitch) {
        return 60 / getBPM(floor, pitch) * duration;
    }

    /**
     * 시초선을 x축의 양의 방향으로 두고 선택된 타일의 각도를 얻습니다.
     *
     * @param floor 타일 번호
     * @return 각도를 구할 때 미드스핀이 향하는 각도도 계산합니다.
     */
    public double getAngleExceptMidSpin(int floor) {
        double angle = getAngle(floor);
        if (floor == 0) {
            return 0;
        }
        if (angle == 999) {
            return (getAngleExceptMidSpin(floor - 1) + 180) % 360;
        }

        return angle;
    }

    /**
     * 이벤트를 제거합니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @param eventID the address
     */
    public void destroyAction(EID eventID) {
        destroyAction(eventID.floor(), eventID.address());
    }

    /**
     * 이벤트를 제거합니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @param floor   the floor
     * @param address the address
     */
    public void destroyAction(int floor, int address) {
        Actions actions = getAction(new EID(floor, address));
        assert actions != null;
        if (actions.isRelatedToTiming()) {
            bpmAnalyzed = false;
            if (!(actions instanceof SetSpeed)) {
                travelAnalyzed = false;
            }
        }
        if (actions instanceof ScaleRadius) {
            etcAnalyzed = false;
        }

        angleData.get(floor).currentTileActions().set(address, null);
        totalEvents--;
    }

    /**
     * 장식 이벤트를 제거합니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @param eventID the address
     */
    public void destroyDecoration(EID eventID) {
        destroyDecoration(eventID.floor(), eventID.address());
    }


    /**
     * 장식 이벤트를 제거합니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @param floor   the floor
     * @param address the address
     */
    public void destroyDecoration(int floor, int address) {
        Decorations decorations = getDecoration(new EID(floor, address));
        assert decorations != null;
        angleData.get(floor).currentTileDecorations().set(address, null);
        totalEvents--;
    }

    /**
     * 이벤트를 만듭니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @param floor 만들 타일 번호
     * @param event 이벤트
     */
    public void createEvent(int floor, @Nonnull Event event) {
        checkAnalysis(floor, event);

        if (event instanceof Actions actions) {
            angleData.get(floor).currentTileActions().add(actions);
        }
        if (event instanceof Decorations decorations) {
            angleData.get(floor).currentTileDecorations().add(decorations);
        }
        totalEvents++;
    }

    /**
     * 이벤트를 여러 개 만듭니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @param event 이벤트
     */
    public void createEvent(List<EIN.ABSEventData> event) {
        for (EIN.ABSEventData e : event) {
            createEvent(e.floor(), e.event());
        }
    }

    /**
     * 이벤트를 여러 개 만듭니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @param floor 만들 타일 번호
     * @param event 이벤트
     */
    public void createEvent(int floor, List<Event> event) {
        for (Event e : event) {
            createEvent(floor, e);
        }
    }

    /**
     * 분석 상태를 점검합니다.
     */
    private void checkAnalysis(Integer floor, Event event) {
        if (bpmAnalyzed && (event.isRelatedToTiming())) {
            if (event instanceof SetSpeed s) {
                assert floor != null;
                if (s.speedType() == SpeedType.BPM) {
                    bpmAnalyzed = getBPM(floor) == s.bpm();
                } else {
                    bpmAnalyzed = false;
                }
                bpmAnalyzed = bpmAnalyzed && s.angleOffset() == 0;
            } else {
                bpmAnalyzed = false;
                travelAnalyzed = false;
            }
        }
        if (event instanceof ScaleRadius) {
            etcAnalyzed = false;
        }
    }

    /**
     * 이벤트를 이동합니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @param target    타겟 장식
     * @param destFloor 이동할 타일 번호
     */
    public void moveAction(EID target, int destFloor) {
        Actions actions = getAction(target);
        assert actions != null;
        createEvent(destFloor, actions);
        destroyAction(target);
    }

    /**
     * 장식 이벤트를 이동합니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @param target    타겟 장식
     * @param destFloor 이동할 타일 번호
     */
    public void moveDecoration(EID target, int destFloor) {
        Decorations decorations = getDecoration(target);
        assert decorations != null;
        createEvent(destFloor, decorations);
        destroyDecoration(target);
    }


    /**
     * 현재 타일의 이벤트 이름을 모두 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1) -> O(n)</strong>
     *
     * @param floor the floor
     * @return the current floor tile event types
     */
    private Set<Class<? extends Event>> getCurrentFloorActionTypes(int floor) {
        AngleData currentAngleData = angleData.get(floor);
        List<Actions> actions = currentAngleData.currentTileActions();
        List<Decorations> decorations = currentAngleData.currentTileDecorations();
        List<Event> events = new ArrayList<>();
        events.addAll(actions);
        events.addAll(decorations);

        return events
                .stream()
                .filter(Objects::nonNull)
                .map(Event::getClass)
                .collect(Collectors.toSet());
    }

    /**
     * 해당 타일에 설치된 장식의 태그 목록을 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1) -> O(n)</strong>
     *
     * @param floor the floor
     * @return the deco event tags
     */
    public List<String> getDecorationTags(int floor) {
        Set<String> tags = new HashSet<>();
        List<Decorations> floorData = getFloorDecorations(floor);

        for (Decorations data : floorData) {
            Tag t = data.tag();
            if (t != null) {
                tags.addAll(t.tags());
            }
        }

        return tags.stream().toList();

    }

    /**
     * 지정된 타일에서 해당 태그가 사용된 모든 장식 이동 이벤트의 상대 위치를 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1) -> O(n)</strong>
     *
     * @param floor the floor
     * @param tag   the tag
     * @return the tile event eid by tag
     */
    public List<EID> getActionIDsByTag(int floor, @Nonnull String tag) {
        List<EID> eventID = new ArrayList<>();
        List<Actions> floorData = getFloorActions(floor);

        for (int i = 0; i < floorData.size(); i++) {

            if (floorData.get(i).eventTypeEquals(MoveDecorations.class)) {
                Tag s = ((MoveDecorations) floorData.get(i)).tag();
                if (s != null && s.tags().contains(tag)) {
                    eventID.add(new EID(floor, i));
                }
            }
        }

        return eventID;
    }

    /**
     * 지정된 타일에서 해당 태그가 사용된 모든 장식 추가 이벤트의 위치를 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1) -> O(n)</strong>
     *
     * @param floor the floor
     * @param tag   the tag
     * @return the deco event eid by tag
     */
    public List<EID> getDecorationEIDsByTag(int floor, @Nonnull String tag) {
        List<EID> eventID = new ArrayList<>();
        List<Decorations> floorData = getFloorDecorations(floor);
        for (int i = 0; i < floorData.size(); i++) {
            Tag s = floorData.get(i).tag();
            if (s != null && s.tags().contains(tag)) {
                eventID.add(new EID(floor, i));
            }
        }

        return eventID;
    }


    /**
     * 해당 이벤트가 존재하는지 확인합니다.
     *
     * @param floor     the floor
     * @param eventType the event type
     * @return the boolean
     */
    public boolean containsEvent(int floor, @Nonnull Class<? extends Event> eventType) {
        return getCurrentFloorActionTypes(floor).contains(eventType);
    }

    /**
     * 일치하는 타입을 가진 이벤트를 얻습니다.
     * 없으면 null을 리턴합니다.
     *
     * @param floor     타일 번호
     * @param eventType 타입
     */
    @Nullable
    public <T extends Actions> T getAction(int floor, @Nonnull Class<T> eventType) {
        return getFloorActions(floor).stream()
                .filter(Objects::nonNull)
                .filter(e -> e.eventTypeEquals(eventType))
                .map(eventType::cast)
                .findFirst()
                .orElse(null);
    }


    /**
     * 해당 위치에 있는 타일 이벤트를 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @return null : 삭제된 이벤트
     */
    @Nullable
    public Actions getAction(EID e) {
        return getAction(e.floor(), e.address());
    }

    /**
     * 해당 위치에 있는 타일 이벤트를 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @return null : 삭제된 이벤트
     */
    @Nullable
    public Actions getAction(int floor, int address) {
        return angleData.get(floor).currentTileActions().get(address);
    }

    /**
     * 일치하는 타입을 가진 이벤트를 얻습니다.
     * 없으면 null을 리턴합니다.
     *
     * @param floor     타일 번호
     * @param eventType 타입
     * @param index     인덱스
     */
    @Nullable
    public <T extends Actions> T getAction(int floor, @Nonnull Class<T> eventType, int index) {
        List<T> filtered = getActions(floor, eventType);
        return filtered.size() <= index ? null : filtered.get(index);
    }


    /**
     * 일치하는 타입을 가진 이벤트를 얻습니다.
     * 없으면 비어있는 리스트를 반환합니다.
     *
     * @param floor     타일 번호
     * @param eventType 타입
     */
    public <T extends Actions> List<T> getActions(int floor, @Nonnull Class<T> eventType) {
        return getFloorActions(floor).stream()
                .filter(Objects::nonNull)
                .filter(e -> e.eventTypeEquals(eventType))
                .map(eventType::cast)
                .toList();

    }

    /**
     * 해당 위치에 있는 타일 이벤트을 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @return null : 삭제된 이벤트
     */
    @Nullable
    public Actions getActivatedAction(EID e) {
        return getActivatedAction(e.floor(), e.address());
    }

    /**
     * 해당 위치에 있는 타일 이벤트를 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @return null : 삭제된 이벤트
     */
    @Nullable
    public Actions getActivatedAction(int floor, int address) {
        Actions action = angleData.get(floor).currentTileActions().get(address);
        return action == null || !action.isActive() ? null : action;
    }

    /**
     * 일치하는 타입을 가진 활성 상태의 이벤트를 얻습니다.
     * 없거나 비활성 상태이면 null을 리턴합니다.
     *
     * @param floor     타일 번호
     * @param eventType 타입
     */
    @Nullable
    public <T extends Actions> T getActivatedAction(int floor, @Nonnull Class<T> eventType) {
        T action = getAction(floor, eventType);
        return action == null || !action.isActive() ? null : action;
    }

    /**
     * 일치하는 타입을 가진 이벤트를 얻습니다.
     * 없으면 비어있는 리스트를 반환합니다.
     *
     * @param floor     타일 번호
     * @param eventType 타입
     */
    public <T extends Actions> List<T> getActivatedActions(int floor, @Nonnull Class<T> eventType) {
        return getActions(floor, eventType).stream().filter(Event::isActive).toList();
    }

    /**
     * 타일 이벤트를 설정합니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @param e       이벤트 주소
     * @param actions 새로 설정할 데이터
     */
    public void setAction(EID e, Actions actions) {
        checkAnalysis(e.floor(), actions);
        angleData.get(e.floor()).currentTileActions().set(e.address(), actions);
    }


    /**
     * 해당 위치에 있는 장식 이벤트를 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @return null : 삭제된 이벤트
     */
    @Nullable
    public Decorations getDecoration(EID e) {
        return getDecoration(e.floor(), e.address());
    }

    /**
     * 해당 위치에 있는 장식 이벤트를 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @return null : 삭제된 이벤트
     */
    @Nullable
    public Decorations getDecoration(int floor, int address) {
        return angleData.get(floor).currentTileDecorations().get(address);
    }

    @Nonnull
    private List<EID> getEIDs(int floor, @Nonnull Class<? extends Event> eventType, List<? extends Event> eventData) {
        List<EID> resultList = new ArrayList<>();
        for (int i = 0; i < eventData.size(); i++) {
            if (eventData.get(i) != null && eventData.get(i).eventTypeEquals(eventType)) {
                resultList.add(new EID(floor, i));
            }
        }
        return resultList;
    }

    /**
     * 해당 타일에 있는 특정 타일 이벤트의 위치를 모두 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1) -> O(n)</strong>
     *
     * @param floor     the floor
     * @param eventType the event type
     * @return the tile event address
     */
    public List<EID> getActionEIDs(int floor, @Nonnull Class<? extends Actions> eventType) {
        List<Actions> floorData = getFloorActions(floor);
        return getEIDs(floor, eventType, floorData);
    }

    /**
     * 해당 타일에 있는 특정 장식 이벤트의 위치를 모두 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1) -> O(n)</strong>
     *
     * @param floor     the floor
     * @param eventType the event type
     * @return the tile event address
     */
    public List<EID> getDecorationEIDs(int floor, @Nonnull Class<? extends Decorations> eventType) {
        List<Decorations> floorData = getFloorDecorations(floor);
        return getEIDs(floor, eventType, floorData);
    }

    /**
     * 현재 총 이벤트의 개수를 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @return the current total events
     */
    public int getCurrentTotalEvents() {
        return totalEvents;
    }


    /**
     * 장식 이벤트를 설정합니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     *
     * @param e           이벤트 주소
     * @param decorations 새로 설정할 데이터
     */
    public void setDecoration(EID e, Decorations decorations) {
        checkAnalysis(e.floor(), decorations);
        angleData.get(e.floor()).currentTileDecorations().set(e.address(), decorations);
    }

    /**
     * 타일에 설치된 이벤트를 얻습니다.
     *
     * @return 이벤트
     */
    public List<List<Actions>> getTileActions() {
        return angleData.stream().map(AngleData::currentTileActions).toList();
    }

    /**
     * 타일에 설치된 장식을 얻습니다.
     *
     * @return 장식
     */
    public List<List<Decorations>> getTileDecorations() {
        return angleData.stream().map(AngleData::currentTileDecorations).toList();
    }


    /**
     * 해당 타일에 있는 타일 이벤트 목록을 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     */
    public List<Actions> getFloorActions(int floor) {
        return Collections.unmodifiableList(angleData.get(floor).currentTileActions());
    }

    /**
     * 해당 타일에 있는 장식 이벤트 목록을 얻습니다.
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     */
    public List<Decorations> getFloorDecorations(int floor) {
        return Collections.unmodifiableList(angleData.get(floor).currentTileDecorations());
    }


    /**
     * 선택한 타일의 이벤트와 함께 타일을 제거합니다.<p>
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     */
    public void removeAngle(int floor) {
        totalEvents -= angleData.get(floor).currentTileActions().size();
        totalEvents -= angleData.get(floor).currentTileDecorations().size();
        angleData.remove(floor);
        bpmAnalyzed = false;
        travelAnalyzed = false;
    }

    /**
     * 마지막 타일 뒤로 새 타일을 상대 각도로 설치합니다.<p>
     * 360이 넘는 값을 전달할경우, 자동으로 비트 일시정지 타일이 설치됩니다<p>
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(n)</strong>
     */
    public void createRelativeAngle(double relativeAngle) {
        createRelativeAngle(getLength(), relativeAngle);
    }

    /**
     * 지정된 타일 뒤로 새 타일을 상대 각도로 설치합니다.<p>
     * 360이 넘는 값을 전달할경우, 자동으로 비트 일시정지 타일이 설치됩니다<p>
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(n)</strong>
     */
    public void createRelativeAngle(int floor, double relativeAngle) {
        if (isTwirling(floor)) {
            createRelativeAngleExceptAnalysis(floor, 360 * (Math.floor(relativeAngle / 360) - (relativeAngle / 360 % 1 == 0 ? 1 : 0)) + 360 - relativeAngle % 360);
        } else {
            createRelativeAngleExceptAnalysis(floor, relativeAngle);
        }
    }

    /**
     * 마지막 타일 뒤로 새 타일을 상대 각도로 설치합니다. 미드스핀은 0이며, 소용돌이, 행성 수를 고려하지 않습니다.<p>
     * 360이 넘는 값을 전달할경우, 자동으로 비트 일시정지 타일이 설치됩니다<p>
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     */
    public void createRelativeAngleExceptAnalysis(double relativeAngle) {
        createRelativeAngleExceptAnalysis(getLength(), relativeAngle);
    }

    /**
     * 지정된 타일 뒤로 새 타일을 상대 각도로 설치합니다. 미드스핀은 0이며, 소용돌이, 행성 수를 고려하지 않습니다.<p>
     * 360이 넘는 값을 전달할경우, 자동으로 비트 일시정지 타일이 설치됩니다<p>
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     */
    public void createRelativeAngleExceptAnalysis(int floor, double relativeAngle) {
        if (relativeAngle < 0) {
            throw new IllegalArgumentException("lower than zero");
        }
        if (relativeAngle == 0) {
            createAngle(MIDSPIN);
            return;
        }
        double prevAngle = getAngleExceptMidSpin(floor);
        double angle = (180 - relativeAngle + prevAngle) % 360;

        if (relativeAngle > 360) {
            int beats = (int) (relativeAngle / 360) * 2 + (relativeAngle % 360 == 0 ? -2 : 0);
            createEvent(floor, new Pause.Builder()
                    .setDuration((double) beats)
                    .setCountdownTicks(0)
                    .build());

        }
        createAngle(floor, angle);
    }

    /**
     * 마지막 타일 뒤로 새 타일을 설치합니다.<p>
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     */
    public void createAngle(double angle) {
        angleData.add(new AngleData(angle));
        bpmAnalyzed = false;
        travelAnalyzed = false;
    }

    /**
     * 지정된 타일 뒤로 새 타일을 설치합니다.<p>
     * <p>
     * <p>
     * <p>
     * <strong>Complexity : O(1)</strong>
     */
    public void createAngle(int floor, double angle) {
        if (floor == getLength()) {
            createAngle(angle);
            return;
        }

        angleData.add(floor + 1, new AngleData(angle));
        bpmAnalyzed = false;
        travelAnalyzed = false;
    }

    /**
     * 분석
     */
    private void travelAnalysis() {
        travelAnalyzing.set(true);
        travelAnalyzed = true;
        currentFloorTwirl.clear();
        currentFloorPlanets.clear();

        int tiles = getLength();
        boolean twirl = false;
        boolean planets = false;

        for (int floor = 0; floor <= tiles; floor++) { //bpm,twirl

            if (containsEvent(floor, Twirl.class)) {
                twirl = !twirl;
            }
            MultiPlanet multiPlanet = getActivatedAction(floor, MultiPlanet.class);
            if (multiPlanet != null) {
                planets = multiPlanet.planets() == Planets.THREE_PLANETS;
            }

            currentFloorTwirl.add(twirl);
            currentFloorPlanets.add(planets);
        }
        travelAnalyzing.set(false);
        TaskManager.notifyAvailableThread();
    }

    private void etcAnalysis() {
        etcAnalyzing.set(true);
        currentFloorRadius.clear();

        int tiles = getLength();
        double radius = 100;

        for (int floor = 0; floor <= tiles; floor++) { //bpm,twirl

            ScaleRadius scaleRadius = getActivatedAction(floor, ScaleRadius.class);
            if (scaleRadius != null) {
                radius = scaleRadius.scale();
            }
            currentFloorRadius.add(radius);
        }
        etcAnalyzing.set(false);
        etcAnalyzed = true;
        TaskManager.notifyAvailableThread();
    }

    /**
     * 분석
     */
    private void bpmAnalysis() {

        travelAnalysis(); //우선 분석하여 twirl, planets 기반으로 작동하는 UnsafeGetTravelAngle 사용 가능

        bpmAnalyzing.set(true);
        totalAngle.clear();
        currentFloorBpm.clear();
        totalAngle.add(0d);
        bpmAnalyzed = true;

        double currentBpm = settings.bpm();
        int tiles = getLength();
        double travelAngleSum = 0;

        for (int floor = 0; floor <= tiles; floor++) { //bpm,twirl

            List<EID> setSpeedList = getActionEIDs(floor, SetSpeed.class);
            if (setSpeedList.isEmpty()) {
                currentFloorBpm.add(currentBpm);
            } else {
                List<SetSpeed> setSpeedListCurrFloor = setSpeedList
                        .stream()
                        .map(e -> (SetSpeed) getActivatedAction(e))
                        .filter(Objects::nonNull)
                        .sorted(Comparator.comparing(e -> Objects.requireNonNullElse(e.angleOffset(), 0.0)))
                        .toList();

                double prevAngle = 0;
                double timeSec = 0;
                double angle = 0;
                for (SetSpeed setSpeed : setSpeedListCurrFloor) {

                    angle = Objects.requireNonNullElse(setSpeed.angleOffset(), 0.0);
                    double d = angle - prevAngle;
                    prevAngle = angle;
                    timeSec += d / (3 * currentBpm);

                    if (setSpeed.speedType() == SpeedType.BPM) {
                        currentBpm = setSpeed.bpm();
                    } else {
                        currentBpm *= setSpeed.multiplier();
                    }
                }
                double travel = unsafeGetTravelAngle(floor);

                timeSec += (travel - angle) / (3 * currentBpm);

                if (timeSec == 0) {
                    currentFloorBpm.add(currentBpm);
                } else {
                    currentFloorBpm.add(travel / (3 * timeSec));
                }
            }

            travelAngleSum += unsafeGetTravelAngle(floor) * settings.bpm() / (currentFloorBpm.isEmpty() ? settings.bpm() : currentFloorBpm.get(currentFloorBpm.size() - 1));

            totalAngle.add(travelAngleSum);
        }
        totalAngle.remove(totalAngle.size() - 1); //마지막 타일 이후 여행각도 180 제거
        bpmAnalyzing.set(false);
        TaskManager.notifyAvailableThread();
    }

    /**
     * @return 해당 값에 있는 타일
     */
    public int getFloor(double t) {
        waitUntilBPMAnalyze();
        if (t <= 0) {
            return 0;
        }
        return ArrayFunction.searchLastIndex(totalAngle, t) - 1;
    }

    /**
     * <p>이벤트를 시간대별로 그룹화합니다.</p>
     * <li><p>활성화 이벤트만을 골라냅니다..</p></li>
     * <li><p>이벤트 반복이 있는 경우, 이벤트 반복을 제거하고 적용된 이벤트를 복제합니다.</p></li>
     * <li><p>조건부 이벤트가 있는 경우, '정확' 태그만 반영되고 나머지 태그만을 가진 이벤트는 제거됩니다.</p></li>
     */
    public Map<Class<? extends Event>, List<EIN.EventData>> generateListedAction() {

        List<EIN.EventData> result = new ArrayList<>();

        for (int floor = 0; floor <= getLength(); floor++) {

            List<EIN.EventData> curFloorAction = new ArrayList<>();
            List<Actions> floorActions = getFloorActions(floor);
            for (int i = 0; i < floorActions.size(); i++) {
                Actions floorAction = floorActions.get(i);
                if (floorAction != null && floorAction.isActive()) {
                    curFloorAction.add(new EIN.EventData(new EID(floor, i), floorAction));
                }
            }

            SetConditionalEvents sce = getActivatedAction(floor, SetConditionalEvents.class);
            if (sce != null) {
                Set<String> filteringTags = new HashSet<>();
                filteringTags.add(sce.earlyPerfectTag());
                filteringTags.add(sce.latePerfectTag());
                filteringTags.add(sce.veryEarlyTag());
                filteringTags.add(sce.veryLateTag());
                filteringTags.add(sce.tooEarlyTag());
                filteringTags.add(sce.tooLateTag());
                filteringTags.add(sce.lossTag());
                filteringTags.remove(sce.perfectTag()); //PERFECT ONLY

                curFloorAction = curFloorAction.stream().filter(e -> {
                    if (e.event() instanceof EventTagEvent ev) {
                        return ev.eventTag().tags().stream().noneMatch(filteringTags::contains);
                    } else {
                        return true;
                    }
                }).toList();
            }
            List<RepeatEvents> rpe = getActivatedActions(floor, RepeatEvents.class);
            List<EIN.EventData> repeatedActionList = new ArrayList<>();
            for (RepeatEvents repeatEvents : rpe) {


                List<EIN.EventData> hasThisTagEvents = curFloorAction.stream()
                        .filter(e -> {
                            if (e.event() instanceof EventTagEvent ev) {
                                return ev.eventTag().tags().stream().anyMatch(repeatEvents.tag().tags()::contains);
                            } else {
                                return false;
                            }
                        }).toList();
                switch (repeatEvents.repeatType()) {
                    case BEAT -> {
                        int repetition = repeatEvents.repetitions();
                        double interval = repeatEvents.interval();

                        for (int i = 1; i <= repetition; i++) {
                            double angleOffset = interval * 180 * i;

                            for (EIN.EventData target : hasThisTagEvents) {
                                EventBuilder builder = target.event().edit();

                                if (target.event() instanceof AngleOffsetEvent a && builder instanceof AngleOffsetEventBuilder b) {

                                    b.setAngleOffset(AdvancedMath.fixDouble(angleOffset + a.angleOffset()));
                                    repeatedActionList.add(new EIN.EventData(new EID(floor, EID.TEMP), builder.build()));
                                }
                            }
                        }
                    }
                    case FLOOR -> {

                        int floorOffset = repeatEvents.floorCount();
                        double angleOffset = getTravelAngle(floor);

                        for (int i = 1; i <= floorOffset; i++) {
                            int rpf = floor + i;

                            for (EIN.EventData target : hasThisTagEvents) {
                                EventBuilder builder = target.event().edit();

                                if (Boolean.TRUE.equals(repeatEvents.executeOnCurrentFloor())) {
                                    repeatedActionList.add(new EIN.EventData(new EID(rpf, EID.TEMP), builder.build()));
                                } else {
                                    if (target.event() instanceof AngleOffsetEvent a && builder instanceof AngleOffsetEventBuilder b) {
                                        b.setAngleOffset(AdvancedMath.fixDouble(angleOffset + a.angleOffset()));
                                    }
                                    repeatedActionList.add(new EIN.EventData(new EID(floor, EID.TEMP), builder.build()));
                                }

                            }

                            if(rpf == getLength()){
                                break;
                            }

                            angleOffset += getTravelAngle(rpf);
                        }
                    }
                    case null, default -> {
                        //noop
                    }
                }

            }

            result.addAll(repeatedActionList);
            result.addAll(curFloorAction);
        }

        return result.stream().collect(Collectors.groupingBy(e -> e.event().getClass()));
    }

    public List<EIN.EventData> generateListedDecoration() {
        List<EIN.EventData> result = new ArrayList<>();
        for (int floor = 0; floor <= getLength(); floor++) {
            for (int address = 0; address < getFloorDecorations(floor).size(); address++) {
                result.add(new EIN.EventData(new EID(floor, address), getFloorDecorations(floor).get(address)));
            }
        }
        return result;
    }

    public void convert(Converter converter) {
        converter.convert(this);
    }

    public static CustomLevel generate(Generator generator) {
        return generator.generate();
    }

    public boolean needAnalyse() {
        return !bpmAnalyzed;
    }
}

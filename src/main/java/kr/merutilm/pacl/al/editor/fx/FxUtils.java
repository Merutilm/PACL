package kr.merutilm.pacl.al.editor.fx;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.pacl.data.CustomLevel;

/**
 * The type Action macro.
 */
public final class FxUtils {
    private FxUtils() {
    }

    /**
     * 지정된 범위의 모든 타일에 같은 함수를 실행합니다.
     *
     * @param event      the event
     * @param startFloor 시작 타일
     * @param endFloor   끝 타일
     */
    public static void fxFloors(FxFloors event, int startFloor, int endFloor) {

        int min = Math.min(startFloor, endFloor);
        int max = Math.max(startFloor, endFloor);

        for (int floor = min; floor <= max; floor++) {
            event.run(floor);
        }
    }

    /**
     * 한 타일에 같은 함수를 실행합니다.
     *
     * @param event      the event
     * @param floor      기준 타일
     * @param repetition 반복 횟수
     */
    public static void fxOneTile(FxOneTile event, int floor, int repetition) {
        for (int attempts = 1; attempts <= repetition; attempts++) {
            event.run(floor, attempts);
        }
    }

    /**
     * 김밥을 말아줍니다.
     * 가감속이 지원되는 이벤트의 경우 권장 프레임은 10~15입니다.
     *
     * @param event    the event
     * @param floor    기준 타일
     * @param duration 이벤트 기간
     * @param fps      초당 프레임률
     */
    public static void fxFrame(CustomLevel level, FxFrame event, int floor, double duration, double fps) {
        double allFrames = Math.round(level.getTimeSecByDuration(floor, duration) * fps) + 1;
        double frameChangeDuration = duration / allFrames;
        for (int attempts = 0; attempts <= allFrames; attempts++) {
            double angleOffset = attempts / allFrames * duration * 180;
            event.run(floor, attempts + 1, frameChangeDuration, level.getTimeSecByAngleOffset(floor, angleOffset), angleOffset);
        }
    }

    /**
     * 한 타일에 사용자 정의 가감속을 실행합니다.
     * 가감속의 시작 값은 0, 끝 값은 1입니다.
     *
     * @param event    the event
     * @param floor    기준 타일
     * @param duration 이벤트 기간
     * @param fps      초당 프레임률
     * @param ease     사용자 정의 가감속
     */
    public static void fxCustomEase(CustomLevel level, FxCustomEase event, int floor, double duration, double fps, FunctionEase ease) {
        int allFrames = (int) Math.round(level.getTimeSecByDuration(floor, duration) * fps);
        double frameChangeDuration = duration / allFrames;

        for (int attempts = 0; attempts <= allFrames; attempts++) {
            event.run(floor, frameChangeDuration, ((double) attempts) / allFrames * duration * 180, ease.apply(attempts / (double) allFrames));
        }
    }

}

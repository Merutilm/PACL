package kr.merutilm.pacl.al.editor.fx;


@FunctionalInterface
public interface FxFrame {
    /**
     * 함수를 실행합니다.
     *
     * @param floor          실행 타일
     * @param attempts       시도 횟수
     * @param changeDuration 변경 기간
     * @param curTimeSec     현재 시각(초)
     * @param angleOffset    각도 오프셋
     */
    void run(int floor, int attempts, double changeDuration, double curTimeSec, double angleOffset);
}

package kr.merutilm.pacl.al.editor.fx;

@FunctionalInterface
public interface FxCustomEase {
    /**
     * 함수를 실행합니다.
     *
     * @param floor          실행 타일
     * @param changeDuration 변경 기간
     * @param angleOffset    각도 오프셋
     * @param easeValue      임의로 설정한 가감속의 결과값 (0~1)
     */
    void run(int floor, double changeDuration, double angleOffset, double easeValue);
}

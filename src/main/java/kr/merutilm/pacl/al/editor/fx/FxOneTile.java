package kr.merutilm.pacl.al.editor.fx;


@FunctionalInterface
public interface FxOneTile {
    /**
     * 함수를 실행합니다.
     *
     * @param floor    실행 타일
     * @param attempts 실행 횟수
     */
    void run(int floor, int attempts);
}

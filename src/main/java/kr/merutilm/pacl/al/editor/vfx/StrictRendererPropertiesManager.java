package kr.merutilm.pacl.al.editor.vfx;

/**
 * 연산 과정이 간단해지도록 몇 개의 파라미터를 추가로 넣은 엄격한 속성 관리자
 */
public interface StrictRendererPropertiesManager {
    /**
     * @return 시작 시각(초)
     */
    double startSec();
    /**
     * @return 끝 시각(초)
     */
    double endSec();




}

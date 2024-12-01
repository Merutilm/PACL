package kr.merutilm.pacl.al.editor.vfx;

/**
 * 렌더러에 추가되는 오브젝트 속성 관리자
 */
public interface RendererPropertiesManager<A extends RendererPropertiesManager<A>> {
    Class<A> type();
    /**
     * 실행 타일
     */
    int floor();
    /**
     * 실행 그룹
     */
    int groupID();
    /**
     * 기간
     */
    double duration();
    /**
     * 각도 오프셋
     */
    double angleOffset();

}

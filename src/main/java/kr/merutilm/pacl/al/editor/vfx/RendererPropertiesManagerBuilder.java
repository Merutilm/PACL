package kr.merutilm.pacl.al.editor.vfx;

/**
 * 렌더러에 추가되는 오브젝트 속성 관리자
 */
public interface RendererPropertiesManagerBuilder<A extends RendererPropertiesManager<A>, B extends RendererPropertiesManagerBuilder<A,B>> {
    B setFloor(int floor);
    B setDuration(double duration);
    B setAngleOffset(double angleOffset);
    A build();
}

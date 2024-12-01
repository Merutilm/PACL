package kr.merutilm.pacl.al.editor.vfx.inittrigger;

import java.util.List;

import kr.merutilm.pacl.al.editor.vfx.RendererPropertiesManager;

/**
 * 대상의 속성에 간접적으로 영향을 주는 인터페이스<p>
 * 일정 시간동안 타겟에 일정한 값을 지속적으로 전달합니다.<p>
 * 동일한 타입에 중첩하여 적용할 수 있습니다.
 *
 * @param <V> 일정한 값
 */
public interface Trigger<T extends Trigger<T, V>, V> extends RendererPropertiesManager<T> {
    /**
     * 값
     */
    V value();

    TriggerBuilder<T, V> edit();

    /**
     * 김밥말이 매크로를 사용하여 렌더링합니다.
     *
     * @param triggerList TriggerList
     * @param refresher   새로고침할 함수
     * @param <V>         State
     * @param <G>         Convert to Strict Modifier List
     */
    static <V, G extends StrictTrigger<V>> void fxFrameRender(List<List<G>> triggerList,
                                                              Refresher<V, G> refresher) {

        for (int i = 0; i < triggerList.size(); i++) {
            List<G> currentGroupTriggers = triggerList.get(i);
            if (!currentGroupTriggers.isEmpty()) {
                refresher.refresh(i + 1, currentGroupTriggers);
            }
        }
    }

    /**
     * @param <V> Value
     */
    @FunctionalInterface
    interface Refresher<V, G extends StrictTrigger<V>> {
        /**
         * 새로고침 함수
         *
         * @param groupID 그룹 아이디
         */
        void refresh(int groupID, List<G> currentGroupTriggers);
    }
}

package kr.merutilm.pacl.al.editor.vfx.initmodifier;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.base.util.ArrayFunction;
import kr.merutilm.pacl.al.editor.vfx.RendererPropertiesManager;

import java.util.List;

/**
 * 대상의 속성에 직접적으로 영향을 주는 인터페이스<p>
 * 현 [State]에서 목표 [State]로 동작<p>
 * 직접 값을 수정하므로 중첩이 불가능합니다.
 *
 * @param <F> 적용할 속성
 * @param <D> 해당 속성이 사용하는 변수
 */
public interface Modifier<F extends Modifier<F, D>, D> extends RendererPropertiesManager<F> {

    /**
     * 목표 State
     */
    D destination();

    /**
     * 가감속 함수
     */
    FunctionEase ease();

    ModifierBuilder<F, D> edit();

    /**
     * 김밥말이 매크로를 사용하여 렌더링합니다.
     *
     * @param modifierList ModifierList
     * @param curTimeSec   현재 시각(초) -> fxFrame 이용
     * @param refresher    새로고침할 함수
     * @param <S>          State
     * @param <G>          Convert to Strict Modifier List
     */
    static <S, G extends StrictModifier<S>> void fxFrameRender(List<List<G>> modifierList,
                                                               double curTimeSec,
                                                               Refresher<S> refresher) {

        for (int i = 0; i < modifierList.size(); i++) {
            List<G> currentGroupModifiers = modifierList.get(i);
            if (!currentGroupModifiers.isEmpty()) {
                int index = ArrayFunction.searchIndex(currentGroupModifiers.stream().map(StrictModifier::startSec).toList(), curTimeSec) - 1;

                StrictModifier<S> strictModifier = currentGroupModifiers.get(index);
                S startState = strictModifier.startState();
                S endState = strictModifier.endState();

                double ratio = strictModifier.ease().apply((curTimeSec - strictModifier.startSec()) / (strictModifier.endSec() - strictModifier.startSec()));

                refresher.refresh(i + 1, ratio, startState, endState);
            }
        }
    }

    /**
     * @param <S> State
     */
    @FunctionalInterface
    interface Refresher<S> {
        /**
         * 새로고침 함수
         *
         * @param groupID    그룹 아이디
         * @param ratio      진행도 비율(0~1)
         * @param startState 시작 State
         * @param endState   끝 State
         */
        void refresh(int groupID, double ratio, S startState, S endState);
    }

}

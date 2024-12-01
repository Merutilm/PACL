package kr.merutilm.pacl.al.editor.vfx.initmodifier;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.base.selectable.Ease;
import kr.merutilm.pacl.al.editor.vfx.StrictRendererPropertiesManager;
import kr.merutilm.pacl.data.CustomLevel;

import java.util.*;
import java.util.function.IntFunction;

/**
 * 시작 [State], 끝 [State]이 모두 존재하는 발동 시각 중심의 엄격한 속성 수정자 인터페이스
 *
 * @param <S> State
 */
public interface StrictModifier<S> extends StrictRendererPropertiesManager {
    /**
     * @return 시작 State 값
     */
    S startState();

    /**
     * @return 끝 State 값
     */
    S endState();

    /**
     * @return 가감속 함수
     */
    FunctionEase ease();


    /**
     * Modifier 목록을 StrictModifier 리스트로 변환합니다.
     *
     * @param <F>              Modifier
     * @param <S>              State
     * @param <G>              StrictModifier
     * @param level            레벨
     * @param firstFloor       시각 계산용 시작 타일
     * @param modifierSet      속성 수정자 목록
     * @param indexFirstValues 인덱스별 초기 값
     * @param constructor      변환할 트리거
     * @return 변환된 리스트
     * @author Luxus
     */
    static <F extends Modifier<F, S>, S, G extends StrictModifier<S>> List<List<G>> of(CustomLevel level,
                                                                                       int firstFloor,
                                                                                       List<Set<F>> modifierSet,
                                                                                       IntFunction<? extends S> indexFirstValues,
                                                                                       Constructor<S, G> constructor) {
        List<List<F>> sortedModifierList = modifierSet.stream()
                .map(modifier -> modifier.stream()
                        .map(v -> v.edit()
                                .setFloor(firstFloor)
                                .setAngleOffset(level.convertAngleOffset(v.floor(), v.angleOffset(), firstFloor))
                                .build()
                        )
                        .sorted(Comparator.comparing(Modifier::angleOffset))
                        .toList()
                ).toList();

        final List<List<G>> strictModifierList = new ArrayList<>();

        for (int i = 0; i < sortedModifierList.size(); i++) {
            List<F> currentGroupModifiers = sortedModifierList.get(i);
            strictModifierList.add(new ArrayList<>());

            double lastEndSec = -Double.MAX_VALUE;

            S lastState = indexFirstValues.apply(i);


            for (int j = 0; j < currentGroupModifiers.size(); j++) {

                S prevState;

                if (j == 0) {
                    prevState = lastState;
                } else {
                    Modifier<F, S> prevModifier = currentGroupModifiers.get(j - 1);
                    prevState = prevModifier.destination();
                }
                Modifier<F, S> modifier = currentGroupModifiers.get(j);
                S curState = modifier.destination();

                double startSec = level.getTimeSecByAngleOffset(firstFloor, modifier.angleOffset());
                double endSec = level.getTimeSecByAngleOffset(firstFloor, modifier.angleOffset() + modifier.duration() * 180);
                if (lastEndSec < startSec) {
                    strictModifierList.get(i).add(constructor.create(
                            lastEndSec,
                            prevState,
                            startSec,
                            prevState,
                            modifier.ease()
                    ));
                }

                strictModifierList.get(i).add(constructor.create(
                        startSec,
                        prevState,
                        endSec,
                        curState,
                        modifier.ease()
                ));
                lastEndSec = endSec;
                lastState = curState;
            }

            strictModifierList.get(i).add(constructor.create(
                    lastEndSec,
                    lastState,
                    Double.MAX_VALUE,
                    lastState,
                    Ease.LINEAR.fun()
            ));

        }
        return strictModifierList.stream().map(Collections::unmodifiableList).toList();
    }

    /**
     * 해당 타입을 가진 StrictModifier 를 생성합니다.
     *
     * @param <S> State
     * @param <F> Modifier type
     */
    @FunctionalInterface
    interface Constructor<S, F> {
        F create(double startSec,
                 S prevState,
                 double endSec,
                 S nextState,
                 FunctionEase ease);
    }


}

package kr.merutilm.pacl.al.editor.vfx.inittrigger;

import java.util.*;

import kr.merutilm.pacl.al.editor.vfx.StrictRendererPropertiesManager;
import kr.merutilm.pacl.data.CustomLevel;

/**
 * 시작 시각, 끝 시각 기반으로 적용중인 트리거의 목록을 모두 불러오는 엄격한 트리거 인터페이스
 *
 * @param <V> value
 */
public interface StrictTrigger<V> extends StrictRendererPropertiesManager {
    List<V> currentValue();

    /**
     * Trigger 목록을 StrictTrigger 리스트로 변환합니다.
     *
     * @param <T>        Trigger
     * @param <V>        Value
     * @param level      레벨
     * @param triggerSet 트리거 목록
     */
    static <T extends Trigger<T, V>, V, G extends StrictTrigger<V>> List<List<G>> of(CustomLevel level,
                                                                                     int firstFloor,
                                                                                     List<Set<T>> triggerSet,
                                                                                     Constructor<V, G> constructor) {

        List<List<T>> sortedTriggerList = triggerSet.stream()
                .map(modifier -> modifier.stream()
                        .map(v -> v.edit()
                                .setFloor(firstFloor)
                                .setAngleOffset(level.convertAngleOffset(v.floor(), v.angleOffset(), firstFloor))
                                .build()
                        )
                        .sorted(Comparator.comparing(Trigger::angleOffset))
                        .toList()
                ).toList();
        final List<List<G>> strictTriggerList = new ArrayList<>();
        for (List<T> currentGroupTriggers : sortedTriggerList) {
            List<TimeSecValueLogger<V>> secList = new ArrayList<>();

            for (Trigger<T, V> trigger : currentGroupTriggers) { //추가 단계
                double angleOffset = trigger.angleOffset();
                double startSec = level.getTimeSecByAngleOffset(firstFloor, angleOffset);
                double endSec = level.getTimeSecByAngleOffset(firstFloor, angleOffset + trigger.duration() * 180);
                secList.add(new TimeSecValueLogger<>(startSec, trigger.value(), startSec, endSec)); //오름차순 정렬 됨
                secList.add(new TimeSecValueLogger<>(endSec, trigger.value(), startSec, endSec)); //정렬 안 됨
            }
            secList = secList.stream()
                    .sorted(Comparator.comparing(TimeSecValueLogger::comparingSec))
                    .toList(); //오름차순 정렬

            // 정렬 단계
            List<G> finalGroupStrictTriggers = new ArrayList<>();
            List<AbstractStrictTrigger<V>> abstractStrictTriggers = new ArrayList<>();
            Set<V> values = new HashSet<>();
            Set<Double> addedTimeSec = new HashSet<>();
            for (TimeSecValueLogger<V> logger : secList) {
                // start < end, 오름차순 정렬이므로 end 전에 start 가 필수적으로 나옴 Set 이용
                // 키 존재 : END, 키가 존재하지 않는 경우 : START


                if (abstractStrictTriggers.isEmpty()) {
                    abstractStrictTriggers.add(new AbstractStrictTrigger<>(-Double.MAX_VALUE, List.of()));
                }
                V value = logger.value();

                if (values.contains(value)) {
                    values.remove(value);
                } else {
                    values.add(value);
                }
                AbstractStrictTrigger<V> abstractStrictTrigger = new AbstractStrictTrigger<>(logger.comparingSec(), List.copyOf(values));
                if (addedTimeSec.contains(logger.comparingSec())) { //오름차순 정렬인데 다음 값이 동일하므로 덮어씌움.
                    abstractStrictTriggers.set(abstractStrictTriggers.size() - 1, abstractStrictTrigger);
                } else {
                    abstractStrictTriggers.add(abstractStrictTrigger);
                    addedTimeSec.add(logger.comparingSec());
                }
            }
            for (int i = 0; i < abstractStrictTriggers.size(); i++) {
                AbstractStrictTrigger<V> abstractStrictTrigger = abstractStrictTriggers.get(i);
                double endSec = i == abstractStrictTriggers.size() - 1 ? Double.MAX_VALUE : abstractStrictTriggers.get(i + 1).startSec();
                finalGroupStrictTriggers.add(constructor.create(abstractStrictTrigger.startSec(), abstractStrictTrigger.currentValue(), endSec));
            }

            strictTriggerList.add(finalGroupStrictTriggers);
        }


        return strictTriggerList;
    }

    record AbstractStrictTrigger<V>(double startSec,
                                    List<V> currentValue) {

    }

    record TimeSecValueLogger<V>(double comparingSec,
                                 V value,
                                 double startSec,
                                 double endSec) {
    }

    @FunctionalInterface
    interface Constructor<V, G> {
        G create(double startSec,
                 List<V> currentValue,
                 double endSec);
    }
}

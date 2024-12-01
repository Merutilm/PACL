package kr.merutilm.pacl.al.editor.vfx.inittrigger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import kr.merutilm.base.exception.UnknownModifierException;

public final class TriggerCreator {

    private final Map<Class<? extends Trigger<?, ?>>, TriggerCollection<? extends Trigger<?, ?>, ?>> creatorMap = new HashMap<>();

    public TriggerCreator(Consumer<TriggerCreator> init) {
        init.accept(this);
    }

    public <T extends Trigger<T, V>, V> void addCreator(TriggerCollection<T, V> triggerCollection) {
        if (creatorMap.containsKey(triggerCollection.type)) {
            throw new IllegalArgumentException("Duplicate Trigger key : " + triggerCollection.type.getSimpleName());
        }
        creatorMap.put(triggerCollection.type, triggerCollection);
    }

    // process
    public <T extends Trigger<T, V>, V> void create(T trigger) {
        getCollection(trigger.type()).create(this, trigger);
    }

    public <T extends Trigger<T, V>, V> List<Set<T>> getTriggerSet(Class<T> clazz) {
        return getCollection(clazz).triggerSet;
    }

    @SuppressWarnings("unchecked")
    private <T extends Trigger<T, V>, V> TriggerCollection<T, V> getCollection(Class<T> clazz) {
        TriggerCollection<T, V> modifierCollection = (TriggerCollection<T, V>) creatorMap.get(clazz);
        if (modifierCollection == null) {
            throw new UnknownModifierException("해당 수정자는 이곳에서 지원하지 않습니다.");
        }
        return modifierCollection;
    }
}

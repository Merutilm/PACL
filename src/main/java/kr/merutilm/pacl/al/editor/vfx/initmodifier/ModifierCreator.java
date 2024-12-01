package kr.merutilm.pacl.al.editor.vfx.initmodifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import kr.merutilm.base.exception.UnknownModifierException;

public final class ModifierCreator {

    private final Map<Class<? extends Modifier<?, ?>>, ModifierCollection<? extends Modifier<?, ?>, ?>> creatorMap = new HashMap<>();

    public ModifierCreator(Consumer<ModifierCreator> init) {
        init.accept(this);
    }

    public <F extends Modifier<F, S>, S> void addCreator(ModifierCollection<F, S> modifierCollection) {
        if (creatorMap.containsKey(modifierCollection.type)) {
            throw new IllegalArgumentException("Duplicate Modifier key : " + modifierCollection.type.getSimpleName());
        }
        creatorMap.put(modifierCollection.type, modifierCollection);
    }

    // process
    public <F extends Modifier<F, S>, S> void create(F trigger) {
        getCollection(trigger.type()).create(this, trigger);
    }


    public <F extends Modifier<F, S>, S> List<Set<F>> getModifierSet(Class<F> clazz) {
        return getCollection(clazz).modifierSet;
    }

    @SuppressWarnings("unchecked")
    private <F extends Modifier<F, S>, S> ModifierCollection<F, S> getCollection(Class<F> clazz) {
        ModifierCollection<F, S> modifierCollection = (ModifierCollection<F, S>) creatorMap.get(clazz);
        if (modifierCollection == null) {
            throw new UnknownModifierException("해당 수정자는 이곳에서 지원하지 않습니다.");
        }
        return modifierCollection;
    }
}

package kr.merutilm.pacl.al.editor.vfx.initmodifier;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class ModifierCollection<F extends Modifier<F, S>, S> {
    protected final List<Set<F>> modifierSet = new ArrayList<>();
    final Class<F> type;
    protected ModifierCollection(Class<F> type) {
        this.type = type;
    }

    public abstract void create(@Nonnull ModifierCreator creator, @Nonnull F modifier);

}

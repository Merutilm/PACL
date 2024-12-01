package kr.merutilm.pacl.al.editor.vfx.inittrigger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class TriggerCollection<T extends Trigger<T, V>, V> {
    protected final List<Set<T>> triggerSet = new ArrayList<>();
    final Class<T> type;
    protected TriggerCollection(Class<T> type) {
        this.type = type;
    }

    public abstract void create(@Nonnull TriggerCreator creator, @Nonnull T trigger);

}

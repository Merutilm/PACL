package kr.merutilm.pacl.al.editor.vfx.inittrigger;

import kr.merutilm.pacl.al.editor.vfx.RendererPropertiesManagerBuilder;

public interface TriggerBuilder<T extends Trigger<T, V>, V> extends RendererPropertiesManagerBuilder<T, TriggerBuilder<T, V>> {
    TriggerBuilder<T, V> setValue(V value);
}

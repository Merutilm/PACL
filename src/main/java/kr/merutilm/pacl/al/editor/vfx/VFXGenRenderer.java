package kr.merutilm.pacl.al.editor.vfx;

import kr.merutilm.pacl.al.editor.vfx.initmodifier.Modifier;
import kr.merutilm.pacl.al.editor.vfx.initmodifier.ModifierCreator;
import kr.merutilm.pacl.al.editor.vfx.inittrigger.Trigger;
import kr.merutilm.pacl.al.editor.vfx.inittrigger.TriggerCreator;

public abstract class VFXGenRenderer<M extends VFXGenerator> {
    private boolean rendered = false;

    public abstract M getMacro();

    public abstract ModifierCreator getModifierCreator();

    public abstract TriggerCreator getTriggerCreator();

    public <F extends Modifier<F, D>, D> void createModifier(F modifier) {
        getModifierCreator().create(modifier);
    }

    public <T extends Trigger<T, V>, V> void createTrigger(T trigger) {
        getTriggerCreator().create(trigger);
    }

    public void render() {
        if (rendered) {
            throw new UnsupportedOperationException("Current Renderer is already closed");
        } else {
            invokeRender();
            rendered = true;
        }
    }

    protected abstract void invokeRender();


}

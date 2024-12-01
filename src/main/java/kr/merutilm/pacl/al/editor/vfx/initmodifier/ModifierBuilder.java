package kr.merutilm.pacl.al.editor.vfx.initmodifier;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.pacl.al.editor.vfx.RendererPropertiesManagerBuilder;

import javax.annotation.Nonnull;

public interface ModifierBuilder<F extends Modifier<F, D>, D> extends RendererPropertiesManagerBuilder<F, ModifierBuilder<F, D>> {
    ModifierBuilder<F, D> setDestination(D destination);

    ModifierBuilder<F, D> setEase(@Nonnull FunctionEase ease);
}

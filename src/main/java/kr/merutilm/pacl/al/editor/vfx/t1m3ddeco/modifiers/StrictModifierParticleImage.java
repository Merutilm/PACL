package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.modifiers;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.base.struct.ImageFile;

public record StrictModifierParticleImage(double startSec,
                                          ImageFile startState,
                                          double endSec,
                                          ImageFile endState,
                                          FunctionEase ease) implements M3CStrictModifier<ImageFile> {
}

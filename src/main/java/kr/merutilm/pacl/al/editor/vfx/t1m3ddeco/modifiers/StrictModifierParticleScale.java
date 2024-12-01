package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.modifiers;

import kr.merutilm.base.functions.FunctionEase;

public record StrictModifierParticleScale(double startSec,
                                          Double startState,
                                          double endSec,
                                          Double endState,
                                          FunctionEase ease) implements M3CStrictModifier<Double> {
}

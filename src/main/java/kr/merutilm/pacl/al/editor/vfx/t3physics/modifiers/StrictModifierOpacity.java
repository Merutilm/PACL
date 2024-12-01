package kr.merutilm.pacl.al.editor.vfx.t3physics.modifiers;

import kr.merutilm.base.functions.FunctionEase;

public record StrictModifierOpacity(double startSec,
                                    Double startState,
                                    double endSec,
                                    Double endState,
                                    FunctionEase ease

) implements MPSStrictModifier<Double> {

}

package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.modifiers;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable.SolidState;

public record StrictModifierMoveSolid(double startSec,
                                      SolidState startState,
                                      double endSec,
                                      SolidState endState,
                                      FunctionEase ease) implements M3CStrictModifier<SolidState> {
}

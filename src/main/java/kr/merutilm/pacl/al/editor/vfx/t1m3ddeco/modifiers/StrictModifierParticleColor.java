package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.modifiers;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.base.struct.HexColor;

public record StrictModifierParticleColor(double startSec,
                                          HexColor startState,
                                          double endSec,
                                          HexColor endState,
                                          FunctionEase ease) implements M3CStrictModifier<HexColor> {
}

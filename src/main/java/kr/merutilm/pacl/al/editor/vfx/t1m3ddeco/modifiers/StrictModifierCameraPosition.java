package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.modifiers;

import kr.merutilm.base.functions.FunctionEase;
import kr.merutilm.base.struct.Point3D;

public record StrictModifierCameraPosition(double startSec,
                                           Point3D startState,
                                           double endSec,
                                           Point3D endState,
                                           FunctionEase ease) implements M3CStrictModifier<Point3D> {
}

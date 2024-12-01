package kr.merutilm.pacl.al.editor.vfx.t3physics.triggers;

import java.util.List;

import kr.merutilm.base.struct.PolarPoint;

public record StrictTriggerForce(double startSec,
                                    List<PolarPoint> currentValue,
                                    double endSec) implements MPSStrictTrigger<PolarPoint> {
}

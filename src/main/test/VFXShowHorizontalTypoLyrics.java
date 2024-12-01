package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.IntRange;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.base.struct.Range;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.VFXGeneratorTypos;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.variable.Fade;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.variable.Pulse;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXShowHorizontalTypoLyrics implements VFX {

    @Override
    public void apply(CustomLevel level) {
        VFXGeneratorTypos macroTypos = new VFXGeneratorTypos(level, 4);
        macroTypos.attributeAsset
                .setDefaultDepth((short) -29600)
                .setLockToCamera(true);

        macroTypos.in(1, "Check this out!", new Point2D(-11, 0), 0, 0.4, 40,
                new Fade(8, Ease.OUT_CUBIC, new Point2D(0, 11), 100, 0.02, 0), 0);
        macroTypos.out(1, 1,
                new Fade(4, Ease.IN_CUBIC, new Point2D(0, 11), 0, 0.02, 0), 0);


    }

    private void add(VFXGeneratorTypos macroTypos, int floor, int groupID, double duration, Point2D p, double scale, String str) {

        macroTypos.in(floor,
                str,
                p,
                0,
                0.4,
                scale,
                new Fade(2, Ease.OUT_CUBIC, new Point2D(0, -1), 100, 0.01, 0.5),
                0
        );

        macroTypos.out(floor,
                groupID,
                new Pulse(duration / 2, Ease.IN_CUBIC, new Point2D(0, -1), 100, 0.03, 1, true, new IntRange(3, 5), 50, new Range(0.03, 0.08)),
                180 * duration);
    }
}

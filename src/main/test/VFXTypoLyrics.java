package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.IntRange;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.base.struct.Range;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.VFXGeneratorTypos;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.variable.Fade;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.variable.Pulse;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXTypoLyrics implements VFX {

    @Override
    public void apply(CustomLevel level) {
        VFXGeneratorTypos macroTypos = new VFXGeneratorTypos(level, 3);
        macroTypos.attributeAsset
                .setDefaultDepth((short) -29600)
                .setLockToCamera(true);

        macroTypos.in(80, "T-T-Turn up the bass and let the speakers, the speakers", new Point2D(0, -11), 0, 0.4, 40,
                new Fade(8, Ease.OUT_CUBIC, new Point2D(0, 1), 100, 0.02, 0.2), 0);
        macroTypos.out(97, 1,
                new Fade(4, Ease.IN_CUBIC, new Point2D(0, -1), 0, 0.01, 1), 0);

        macroTypos.in(99, "Blow,", new Point2D(3, 2), 0, 0.4, 120,
                new Fade(1, Ease.OUT_CUBIC, new Point2D(0, -1), 100, 0.05, 0), 0);
        macroTypos.out(100, 2,
                new Pulse(2, Ease.IN_CUBIC, new Point2D(0, -1), 0, 0, 0, true, new IntRange(3, 5), 50, new Range(0.03, 0.08)), 0);

        macroTypos.in(100, "Blow", new Point2D(2, 1), 0, 0.4, 150,
                new Fade(1, Ease.OUT_CUBIC, new Point2D(0, -1), 100, 0.05, 0), 0);
        macroTypos.out(101, 3,
                new Pulse(4, Ease.IN_CUBIC, new Point2D(0, -1), 0, 0, 0, true, new IntRange(3, 5), 50, new Range(0.03, 0.08)), 0);

        macroTypos.in(101, "T-T-Turn up the bass and let the speakers, the speakers", new Point2D(0, -11), 0, 0.4, 40,
                new Fade(8, Ease.OUT_CUBIC, new Point2D(0, 1), 100, 0.02, 0.2), 0);
        macroTypos.out(118, 4,
                new Fade(4, Ease.IN_CUBIC, new Point2D(0, -1), 100, 0.01, 1), 0);

        macroTypos.in(120, "Blow,", new Point2D(-3, 3), 0, 0.4, 120,
                new Fade(2, Ease.OUT_CUBIC, new Point2D(0, -1), 100, 0.05, 0), 0);
        macroTypos.out(122, 5,
                new Pulse(2, Ease.IN_CUBIC, new Point2D(0, -1), 0, 0, 0, true, new IntRange(3, 5), 50, new Range(0.03, 0.08)), 0);

        macroTypos.in(121, "Blow,", new Point2D(-2, 2), 0, 0.4, 135,
                new Fade(2, Ease.OUT_CUBIC, new Point2D(0, -1), 100, 0.05, 0), 0);
        macroTypos.out(122, 6,
                new Pulse(2, Ease.IN_CUBIC, new Point2D(0, -1), 0, 0, 0, true, new IntRange(3, 5), 50, new Range(0.03, 0.08)), 90);

        macroTypos.in(122, "Blow", new Point2D(-1, 1), 0, 0.4, 150,
                new Fade(1, Ease.OUT_CUBIC, new Point2D(0, -1), 100, 0.05, 0), 0);
        macroTypos.out(123, 7,
                new Pulse(8, Ease.IN_CUBIC, new Point2D(0, -1), 0, 0, 0, true, new IntRange(3, 5), 50, new Range(0.03, 0.08)), 0);

    }
}

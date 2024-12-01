package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.IntRange;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.base.struct.Range;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.VFXGeneratorTypos;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.variable.Fade;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.variable.Pulse;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXHorizontalTextTypo implements VFX {
    @Override
    public void apply(CustomLevel level) {
        // settings start
        int renderGroup = 2;

        int startTile = 1060;
        int endTile = 1065;

        double duration = 4;
        double scale = 50;

        String text = "Judgement";

        // settings end

        // calculate ...

        VFXGeneratorTypos macro = new VFXGeneratorTypos(level, renderGroup);
        macro.attributeAsset.setDefaultDepth((short) -10000);


        macro.in(startTile, text, new Point2D(0, -6), 0, 1, scale,
                new Fade(duration, Ease.IN_CUBIC, Point2D.ORIGIN, 100, 0, 0), 0);
        macro.moveText(startTile, 1, duration, null, scale, 0.5, Ease.OUT_CUBIC, 0);
        macro.out(endTile, 1, new Pulse(4, Ease.IN_CUBIC, new Point2D(0, -1), 0, 0, 0, true, new IntRange(5, 15), 40, new Range(0.04, 0.08)), 0);
    }


}

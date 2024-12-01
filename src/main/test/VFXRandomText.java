package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.base.util.StringUtils;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.VFXGeneratorTypos;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.variable.Fade;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXRandomText implements VFX {
    @Override
    public void apply(CustomLevel level) {
        VFXGeneratorTypos macro = new VFXGeneratorTypos(level, 1);
        macro.attributeAsset.setDefaultDepth((short) -10000);
        int startTile = 3328;
        double duration = 9;
        String text = "Epilepsy Warning";

        macro.in(startTile, StringUtils.randomString(text.length()), new Point2D(0, 0), 0, 40, new Fade(12, Ease.IN_CUBIC, Point2D.ORIGIN, 100, 0, 0), 0);
        for (int i = 0; i < duration * 180; i += 20) {
            macro.changeText(startTile, 1, StringUtils.randomString(text.length()), i);
        }
        macro.changeText(3342, 1, text);
        macro.out(3342, 1, new Fade(4, Ease.IN_QUAD, Point2D.ORIGIN, 0, 0, 0), 0);
    }


}

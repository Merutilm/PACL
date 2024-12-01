package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.functions.FunctionEase;
import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.base.util.AdvancedMath;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.VFXGeneratorTypos;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.variable.Fade;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXCountTypo implements VFX {
    @Override
    public void apply(CustomLevel level) {
        // settings start
        int renderGroup = 1;

        int startTile = 1060;
        int endTile = 1065;

        double fadeDuration = 2;

        double animateDuration = 4;
        FunctionEase animateEase = Ease.INOUT_CUBIC.fun();

        int startCount = 99;
        int endCount = 75;

        double changeIntervalDivisor = 16;
        // settings end

        // calculate ...

        VFXGeneratorTypos macro = new VFXGeneratorTypos(level, renderGroup);
        macro.attributeAsset.setDefaultDepth((short) -10000);
        String text = "%";

        macro.in(startTile, startCount + text, new Point2D(0, -2.7), 0, 80,
                new Fade(fadeDuration, Ease.OUT_CUBIC, Point2D.ORIGIN, 100, 0, 0), 0);
        String prevResult = "";

        for (int i = 0; i <= animateDuration * changeIntervalDivisor; i++) {
            String result = (int) AdvancedMath.ratioDivide(startCount, endCount, i / (animateDuration * changeIntervalDivisor), animateEase) + text;
            if (!prevResult.equals(result)) {
                macro.changeText(startTile, 1, result, i * 180 / changeIntervalDivisor);
                prevResult = result;
            }
        }
        macro.out(endTile, 1, new Fade(fadeDuration, Ease.IN_CUBIC, Point2D.ORIGIN, 0, 0, 0), 0);
    }


}

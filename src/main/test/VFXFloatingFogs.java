package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.struct.DecorationFile;
import kr.tkdydwk7071.base.struct.HexColor;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t4mfog.VFXGeneratorFog;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXFloatingFogs implements VFX {
    @Override
    public void apply(CustomLevel level) {
        VFXGeneratorFog macroFog = new VFXGeneratorFog(level, 3);
        macroFog.attributeAsset.setDecorationImage(new DecorationFile("fog.png"));
        double div = 1;
        for (int i = 2320; i <= level.getLength() - 1; i++) {

            macroFog.addMovingFogs(i, 5, (short) -54, 18, 55, 12 / div, 2 / div, 25, 90,
                    0, 0, 24, 64, 0.8, HexColor.BLACK, 60, 70,
                    90, 0.25 / div, 0.8);
        }
    }
}

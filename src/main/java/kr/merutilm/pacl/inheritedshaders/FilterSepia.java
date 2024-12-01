package kr.merutilm.pacl.inheritedshaders;

import kr.merutilm.base.exception.IllegalRenderStateException;
import kr.merutilm.base.io.BitMap;
import kr.merutilm.base.parallel.RenderState;
import kr.merutilm.base.parallel.ShaderDispatcher;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.util.AdvancedMath;

public final class FilterSepia extends ShaderDispatcher {

    public FilterSepia(RenderState renderState, int renderID, BitMap bitMap, double intensity) throws IllegalRenderStateException{
        super(renderState, renderID, bitMap);
        createRenderer(((x, y, xr, yr, rx, ry, i, c, t) -> {
            HexColor g = c.grayScale();
            HexColor k = c.functionExceptAlpha(g, (e, ta) -> HexColor.safetyFix((int)AdvancedMath.ratioDivide(e, ta, intensity)));
            int rr = HexColor.safetyFix((int)AdvancedMath.ratioDivide(k.r(), k.r() + 110.0, intensity));
            int rg = HexColor.safetyFix((int)AdvancedMath.ratioDivide(k.g(), k.g() + 44.0, intensity));
            int rb = HexColor.safetyFix((int)AdvancedMath.ratioDivide(k.b(), k.b() + 20.0, intensity));
            return HexColor.get(rr, rg, rb);
        }));
    }
}

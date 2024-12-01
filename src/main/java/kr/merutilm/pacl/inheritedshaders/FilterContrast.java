package kr.merutilm.pacl.inheritedshaders;

import kr.merutilm.base.exception.IllegalRenderStateException;
import kr.merutilm.base.io.BitMap;
import kr.merutilm.base.parallel.RenderState;
import kr.merutilm.base.parallel.ShaderDispatcher;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.util.AdvancedMath;

public final class FilterContrast extends ShaderDispatcher {

    public FilterContrast(RenderState renderState, int renderID, BitMap bitMap, double intensity) throws IllegalRenderStateException {
        super(renderState, renderID, bitMap);
        createRenderer(((x, y, xr, yr, rx, ry, i, c, t) -> c.functionExceptAlpha(e -> HexColor.safetyFix((int)AdvancedMath.ratioDivide(e, 128, -intensity)))));
    }
}

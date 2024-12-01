package kr.merutilm.pacl.inheritedshaders;

import kr.merutilm.base.exception.IllegalRenderStateException;
import kr.merutilm.base.io.BitMap;
import kr.merutilm.base.parallel.RenderState;
import kr.merutilm.base.parallel.ShaderDispatcher;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.util.AdvancedMath;

public final class FilterFilmGrain extends ShaderDispatcher {

    public FilterFilmGrain(RenderState renderState, int renderID, BitMap bitMap, double intensity) throws IllegalRenderStateException{
        super(renderState, renderID, bitMap);
        createRenderer(((x, y, xr, yr, rx, ry, i, c, t) -> {
            HexColor rc = HexColor.ratioDivide(HexColor.BLACK, HexColor.WHITE, AdvancedMath.doubleRandom(1));

            double r = 5.25;
            double p = 1.35;

            double o = 1 - r / (Math.pow(Math.abs(intensity), p) + r);
            return c.blend(HexColor.ColorBlendMode.NORMAL, rc, o);
        }));
    }


}

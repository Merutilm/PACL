package kr.merutilm.pacl.inheritedshaders;

import kr.merutilm.base.exception.IllegalRenderStateException;
import kr.merutilm.base.io.BitMap;
import kr.merutilm.base.parallel.RenderState;
import kr.merutilm.base.parallel.ShaderDispatcher;

public final class FilterPoster extends ShaderDispatcher {

    public FilterPoster(RenderState renderState, int renderID, BitMap bitMap, double intensity) throws IllegalRenderStateException {
        super(renderState, renderID, bitMap);
        createRenderer(((x, y, xr, yr, rx, ry, i, c, t) -> {
            double divisor = 256 / intensity; //1280 << none
            return c.functionExceptAlpha(e -> (int)(Math.floor(e * divisor) / divisor));
        }));
    }
}

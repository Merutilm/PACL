package kr.merutilm.pacl.inheritedshaders;

import kr.merutilm.base.exception.IllegalRenderStateException;
import kr.merutilm.base.io.BitMap;
import kr.merutilm.base.parallel.RenderState;
import kr.merutilm.base.parallel.ShaderDispatcher;

public final class FilterInvert extends ShaderDispatcher {
    public FilterInvert(RenderState renderState, int renderID, BitMap bitMap) throws IllegalRenderStateException {
        super(renderState, renderID, bitMap);
        createRenderer(((x, y, xr, yr, rx, ry, i, c, t) -> c.invert()));
    }

}

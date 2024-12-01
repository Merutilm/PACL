package kr.merutilm.pacl.inheritedshaders;

import kr.merutilm.base.exception.IllegalRenderStateException;
import kr.merutilm.base.io.BitMap;
import kr.merutilm.base.parallel.RenderState;
import kr.merutilm.base.parallel.ShaderDispatcher;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.util.AdvancedMath;

public final class FilterAberration extends ShaderDispatcher {

    public FilterAberration(RenderState renderState, int renderID, BitMap bitMap, double intensity) throws IllegalRenderStateException{
        super(renderState, renderID, bitMap);
        int dx = (int) (intensity * bitMap.getWidth());
        int dy = (int) (-intensity * bitMap.getHeight());
        createRenderer(((x, y, xr, yr, rx, ry, i, c, t) -> {
            int tx = AdvancedMath.restrict(0, bitMap.getWidth() - 1, x + dx);
            int ty = AdvancedMath.restrict(0, bitMap.getHeight() - 1, y + dy);

            int r = texture2D(tx, y).r();
            int g = texture2D(x, y).g();
            int b = texture2D(x, ty).b();

            return HexColor.get(r, g, b);
        }));
    }

}

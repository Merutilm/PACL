package kr.merutilm.pacl.inheritedshaders;

import java.util.Arrays;

import kr.merutilm.base.exception.IllegalRenderStateException;
import kr.merutilm.base.io.BitMap;
import kr.merutilm.base.parallel.RenderState;
import kr.merutilm.base.parallel.ShaderDispatcher;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.util.AdvancedMath;


public final class FilterGaussianBlur extends ShaderDispatcher {

    public FilterGaussianBlur(RenderState renderState, int renderID, BitMap bitMap, double intensity) throws IllegalRenderStateException {
        super(renderState, renderID, bitMap);
        int off = (int) AdvancedMath.abs(intensity * bitMap.getWidth());
        int[] tempCanvas25 = Arrays.stream(bitMap.getCanvas())
                .map(e -> HexColor.toInteger(HexColor.intR(e) / 25, HexColor.intG(e) / 25, HexColor.intB(e) / 25))
                .toArray();

        createRenderer(((x, y, xr, yr, rx, ry, i, c, t) -> {
            if (off == 0) {
                return c;
            }
            int r = 0;
            int g = 0;
            int b = 0;
            for (int fx = x - off; fx <= x + off; fx += off / 2) {
                for (int fy = y - off; fy <= y + off; fy += off / 2) {
                    int index = bitMap.convertLocation(fx, fy);
                    int col = tempCanvas25[index];
                    r += HexColor.intR(col);
                    g += HexColor.intG(col);
                    b += HexColor.intB(col);
                    tryBreak();
                }
            }
            return HexColor.getSafe(r, g, b);
        }));
    }



}

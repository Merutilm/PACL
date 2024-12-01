package kr.merutilm.pacl.inheritedshaders;

import java.util.Arrays;

import kr.merutilm.base.exception.IllegalRenderStateException;
import kr.merutilm.base.io.BitMap;
import kr.merutilm.base.parallel.RenderState;
import kr.merutilm.base.parallel.ShaderDispatcher;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.util.AdvancedMath;


public final class FilterBlur extends ShaderDispatcher {

    public FilterBlur(RenderState renderState, int renderID, BitMap bitMap, double intensity) throws IllegalRenderStateException {
        super(renderState, renderID, bitMap);
        int off = (int) AdvancedMath.abs(intensity * bitMap.getWidth());

        int[] multiplier;
        int[] tempCanvas9;

        if (off != 0) {
            multiplier = new int[]{
                    1, 2, 1,
                    2, 3, 2,
                    1, 2, 1
            };
            int sum = Arrays.stream(multiplier).sum();
            tempCanvas9 = Arrays.stream(bitMap.getCanvas())
                    .map(e -> HexColor.toInteger(HexColor.intR(e) / sum, HexColor.intG(e) / sum, HexColor.intB(e) / sum))
                    .toArray();


        } else {
            multiplier = null;
            tempCanvas9 = null;
        }

        createRenderer(((x, y, xr, yr, rx, ry, i, c, t) -> {
            if (off == 0) {
                return c;
            }

            int r = 0;
            int g = 0;
            int b = 0;
            int mi = 0;
            for (int fx = x - off; fx <= x + off; fx += off) {
                for (int fy = y - off; fy <= y + off; fy += off) {
                    int index = bitMap.convertLocation(fx, fy);
                    int col = tempCanvas9[index];
                    int m = multiplier[mi];
                    r += HexColor.intR(col) * m;
                    g += HexColor.intG(col) * m;
                    b += HexColor.intB(col) * m;
                    mi++;
                    tryBreak();
                }
            }
            return HexColor.get(r, g, b);
        }));
    }

}

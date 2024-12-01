package kr.merutilm.pacl.inheritedshaders;

import java.util.Arrays;
import java.util.stream.Stream;

import kr.merutilm.base.exception.IllegalRenderStateException;
import kr.merutilm.base.io.BitMap;
import kr.merutilm.base.parallel.RenderState;
import kr.merutilm.base.parallel.ShaderDispatcher;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.util.AdvancedMath;

public final class FilterSharpen extends ShaderDispatcher {


    public FilterSharpen(RenderState renderState, int renderID, BitMap bitMap, double v) throws IllegalRenderStateException{
        super(renderState, renderID, bitMap);
        double intensity = v / 2560;
        double strength = v * 9;
        int offX = (int) AdvancedMath.abs(intensity * bitMap.getWidth());
        int offY = (int) AdvancedMath.abs(intensity * bitMap.getHeight());
        createRenderer(((x, y, xr, yr, rx, ry, i, c, t) -> {
            int[] col = Stream.of(
                    c,
                    texture2D(x - offX, y),
                    texture2D(x + offX, y),
                    texture2D(x, y - offY),
                    texture2D(x, y + offY)
            ).mapToInt(HexColor::toInteger).toArray();

            double[] result = Stream.of(BitMap.redChannels(col),
                            BitMap.greenChannels(col),
                            BitMap.blueChannels(col)
                    ).mapToDouble(e -> {
                        double avg = Arrays.stream(e).average().orElse(0);
                        double min = Arrays.stream(e).min().orElse(0);
                        double max = Arrays.stream(e).max().orElse(0);
                        return AdvancedMath.restrict(min, max, (strength + 1) * e[0] - avg * strength);
                    })
                    .toArray();

            return HexColor.get((int)result[0], (int)result[1], (int)result[2]);
        }));
    }


}

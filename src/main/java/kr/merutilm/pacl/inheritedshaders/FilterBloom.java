package kr.merutilm.pacl.inheritedshaders;

import kr.merutilm.base.exception.IllegalRenderStateException;
import kr.merutilm.base.io.BitMap;
import kr.merutilm.base.parallel.RenderState;
import kr.merutilm.base.parallel.ShaderDispatcher;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.pacl.data.VFXBloom;

import java.util.Arrays;

public final class FilterBloom extends ShaderDispatcher {
    public FilterBloom(RenderState renderState, int renderID, BitMap bitMap, double intensity, VFXBloom currentBloomEffect) throws IllegalRenderStateException{
        super(renderState, renderID, bitMap);
        int lightBoundaries = (int) (currentBloomEffect.threshold() * 2.56);
        HexColor color = currentBloomEffect.color();
        HexColor bc = HexColor.get(color.r(), color.g(), color.b());

        int range = bitMap.getWidth() / 512;

        int[] blurredAvailableCanvas = Arrays.stream(bitMap.getCanvas())
                .map(e -> HexColor.grayScaleValue(e) < lightBoundaries ? 255 << 24 : e)
                .toArray();

        BitMap.gaussianBlur(blurredAvailableCanvas, bitMap.getWidth(), range);
        createRenderer(((x, y, xr, yr, rx, ry, i, c, t) -> c.functionExceptAlpha(HexColor.fromInteger(blurredAvailableCanvas[i]).blend(HexColor.ColorBlendMode.MULTIPLY, bc), (e, ta) -> (int) AdvancedMath.restrict(0, 255, e + ta * intensity))));
    }

}

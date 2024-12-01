package kr.merutilm.pacl.data;

import kr.merutilm.base.struct.HexColor;

public record VFXBloom(double threshold,
                       double intensity,
                       HexColor color) {

    private static final VFXBloom NULL = new VFXBloom(Double.NaN, Double.NaN, HexColor.TRANSPARENT);

    public boolean isValid(){
        return this != NULL;
    }
    public static VFXBloom nullBloomEffect() {
        return NULL;
    }
}

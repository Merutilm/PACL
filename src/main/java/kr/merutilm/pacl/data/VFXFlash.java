package kr.merutilm.pacl.data;

import kr.merutilm.base.struct.HexColor;

public record VFXFlash(HexColor backColor,
                       double backOpacity,
                       HexColor frontColor,
                       double frontOpacity) {
}

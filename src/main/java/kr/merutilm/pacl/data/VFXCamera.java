package kr.merutilm.pacl.data;

import kr.merutilm.base.struct.Point2D;

/**
 * 프레임 카메라
 */
public record VFXCamera(Point2D position,
                        double rotation,
                        double zoom) implements State {

}

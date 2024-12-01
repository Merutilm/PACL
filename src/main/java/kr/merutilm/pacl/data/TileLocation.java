package kr.merutilm.pacl.data;

import kr.merutilm.base.struct.Point2D;

/**
 * 프레임 타일 위치
 */
public record TileLocation(Point2D positionOffset,
                    double rotationOffset,
                    Point2D scale,
                    double opacity) {

}

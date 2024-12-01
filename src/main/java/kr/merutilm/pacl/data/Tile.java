package kr.merutilm.pacl.data;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.event.selectable.TrackStyle;

/**
 * 프레임 타일
 */
public record Tile(Point2D positionOffset,
                   double rotationOffset,
                   Point2D scale,
                   double opacity,
                   TrackStyle trackStyle,
                   HexColor color) implements State {
}

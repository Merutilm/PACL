package kr.merutilm.pacl.inheritedshaders;

import kr.merutilm.base.exception.IllegalRenderStateException;
import kr.merutilm.base.io.BitMap;
import kr.merutilm.base.parallel.RenderState;
import kr.merutilm.base.parallel.ShaderDispatcher;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.util.AdvancedMath;

public final class FilterFisheye extends ShaderDispatcher {

    public FilterFisheye(RenderState renderState, int renderID, BitMap bitMap, double intensity) throws IllegalRenderStateException{
        super(renderState, renderID, bitMap);
        createRenderer(((x, y, xr, yr, rx, ry, i, c, t) -> {
            double dx = rx - 0.5;
            double dy = ry - 0.5;

            double distance = AdvancedMath.hypot(dx, dy);
            double distort = 4.4444 * (intensity - 0.5);

            Point2D dot = new Point2D(dx, dy).toPolar().edit().setRadius(1).build().coordinate(); //외적 구하기

            int fx;
            int fy;

            if (distort > 0) {
                fx = (int) (bitMap.getWidth() * (0.5 + dot.x() * Math.tan(distance * distort) * 0.5 / Math.tan(distort / 2)));
                fy = (int) (bitMap.getHeight() * (0.5 + dot.y() * Math.tan(distance * distort) * 0.5 / Math.tan(distort / 2)));
                return texture2D(fx, fy);
            } else if (distort < 0) {
                fx = (int) (bitMap.getWidth() * (0.5 + dot.x() * Math.atan(distance * -distort * 10) * 0.5 / Math.atan(-distort * 5)));
                fy = (int) (bitMap.getHeight() * (0.5 + dot.y() * Math.atan(distance * -distort * 10) * 0.5 / Math.atan(-distort * 5)));
                return texture2D(fx, fy);
            } else {
                return c;
            }
        }));
    }



}

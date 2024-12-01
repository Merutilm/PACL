package kr.merutilm.pacl.al.editor.vfx.t6typo.variable;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.event.events.action.MoveDecorations;
import kr.merutilm.pacl.al.event.struct.Tag;
import kr.merutilm.pacl.data.CustomLevel;

public abstract class TextAppearance {
    private final Point2D displacement;
    private final double duration;
    private final Ease ease;
    private final double opacity;
    private final double delaySec;
    private final double range;

    private static final TextAppearance NONE = new TextAppearance(0, Ease.LINEAR, Point2D.ORIGIN, 100.0, 0, 0) {
        @Override
        public void in(CustomLevel level, int floor, Point2D characterPosition, Point2D scale, HexColor color, Tag characterTag, double angleOffset) {

            level.createEvent(floor, new MoveDecorations.Builder()
                    .setTag(characterTag)
                    .setDuration(getDuration())
                    .setPivotOffset(characterPosition)
                    .setOpacity(100.0)
                    .setScale(scale)
                    .setColor(color)
                    .setAngleOffset(angleOffset)
                    .build()
            );
        }

        @Override
        public void out(CustomLevel level, int floor, Point2D characterPosition, Point2D scale, HexColor color, Tag characterTag, double angleOffset) {
            level.createEvent(floor, new MoveDecorations.Builder()
                    .setTag(characterTag)
                    .setDuration(getDuration())
                    .setPivotOffset(characterPosition)
                    .setOpacity(0d)
                    .setScale(scale)
                    .setColor(color)
                    .setAngleOffset(angleOffset)
                    .build()
            );
        }
    };

    public static TextAppearance nullAppearance() {
        return NONE;
    }

    protected TextAppearance(double duration, Ease ease, Point2D displacement, double opacity, double delaySec, double range) {
        this.displacement = displacement;
        this.duration = duration;
        this.ease = ease;
        this.opacity = opacity;
        this.delaySec = delaySec;
        this.range = range;
    }

    public double getDuration() {
        return duration;
    }

    public Ease getEase() {
        return ease;
    }

    public Point2D getDisplacement() {
        return displacement;
    }

    public double getOpacity() {
        return opacity;
    }

    public double getDelaySec() {
        return delaySec;
    }

    public double getRange() {
        return range;
    }

    public abstract void in(CustomLevel level, int floor, Point2D characterPosition, Point2D scale, HexColor color, Tag characterTag, double angleOffset);

    public abstract void out(CustomLevel level, int floor, Point2D characterPosition, Point2D scale, HexColor color, Tag characterTag, double angleOffset);
}

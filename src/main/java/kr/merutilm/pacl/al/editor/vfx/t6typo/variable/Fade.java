package kr.merutilm.pacl.al.editor.vfx.t6typo.variable;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.event.events.action.MoveDecorations;
import kr.merutilm.pacl.al.event.struct.Tag;
import kr.merutilm.pacl.data.CustomLevel;

public class Fade extends TextAppearance {

    public Fade(double duration, Ease ease, Point2D displacement, double opacity, double delaySec, double range) {
        super(duration, ease, displacement, opacity, delaySec, range);
    }

    @Override
    public void in(CustomLevel level, int floor, Point2D characterPosition, Point2D scale, HexColor color, Tag characterTag, double angleOffset) {
        level.createEvent(floor, new MoveDecorations.Builder()
                .setTag(characterTag)
                .setPivotOffset(characterPosition.getInCircleRandomPoint(getRange()))
                .setDuration(0d)
                .setScale(scale)
                .setColor(color)
                .setAngleOffset(angleOffset)
                .build()
        );

        level.createEvent(floor, new MoveDecorations.Builder()
                .setTag(characterTag)
                .setOpacity(getOpacity())
                .setDuration(getDuration())
                .setEase(getEase())
                .setAngleOffset(angleOffset)
                .build()
        );


        level.createEvent(floor, new MoveDecorations.Builder()
                .setTag(characterTag)
                .setDuration(getDuration())
                .setPivotOffset(characterPosition.add(getDisplacement()))
                .setEase(getEase())
                .setAngleOffset(angleOffset)
                .build()
        );
    }

    @Override
    public void out(CustomLevel level, int floor, Point2D characterPosition, Point2D scale, HexColor color, Tag characterTag, double angleOffset) {
        level.createEvent(floor, new MoveDecorations.Builder()
                .setOpacity(getOpacity())
                .setDuration(getDuration())
                .setScale(scale)
                .setColor(color)
                .setPivotOffset(characterPosition.add(getDisplacement()).getInCircleRandomPoint(getRange()))
                .setTag(characterTag)
                .setEase(getEase())
                .setAngleOffset(angleOffset)
                .build()
        );
    }
}

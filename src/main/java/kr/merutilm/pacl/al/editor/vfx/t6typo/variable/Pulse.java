package kr.merutilm.pacl.al.editor.vfx.t6typo.variable;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.IntRange;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.struct.Range;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.pacl.al.event.events.action.MoveDecorations;
import kr.merutilm.pacl.al.event.struct.Tag;
import kr.merutilm.pacl.data.CustomLevel;

public class Pulse extends TextAppearance {

    private final IntRange pulsesRange;
    private final double maxPulsingOpacity;
    private final Range invisibleSecRange;
    private final boolean opacityAffectsPulsing;

    public Pulse(double duration, Ease ease, Point2D displacement, double opacity, double delaySec, double range, boolean opacityAffectsPulsing, IntRange pulsesRange, int maxPulsingOpacity, Range invisibleSecRange) {
        super(duration, ease, displacement, opacity, delaySec, range);
        this.pulsesRange = pulsesRange;
        this.maxPulsingOpacity = maxPulsingOpacity;
        this.invisibleSecRange = invisibleSecRange;
        this.opacityAffectsPulsing = opacityAffectsPulsing;
    }

    @Override
    public void in(CustomLevel level, int floor, Point2D characterPosition, Point2D scale, HexColor color, Tag characterTag, double angleOffset) {
        level.createEvent(floor, new MoveDecorations.Builder()
                .setTag(characterTag)
                .setPivotOffset(characterPosition.getInCircleRandomPoint(getRange()))
                .setDuration(0d)
                .setScale(scale)
                .setColor(color)
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

        int pulses = pulsesRange.random();
        double endAngle = angleOffset + getDuration() * 180;
        for (int i = 0; i < pulses; i++) {
            double invisibleSec = invisibleSecRange.random();
            double angleInterval = level.getAngleOffsetByTimeSec(floor, invisibleSec);
            double randomAngle = AdvancedMath.random(angleOffset, endAngle - angleInterval - 0.1);
            double movementRatio = AdvancedMath.getRatio(angleOffset, endAngle, randomAngle);

            level.createEvent(floor, new MoveDecorations.Builder()
                    .setDuration(0d)
                    .setTag(characterTag)
                    .setOpacity(opacityAffectsPulsing ? getOpacity() * movementRatio : getOpacity())
                    .setAngleOffset(randomAngle)
                    .build()
            );

            level.createEvent(floor, new MoveDecorations.Builder()
                    .setDuration(0d)
                    .setTag(characterTag)
                    .setOpacity(AdvancedMath.doubleRandom(opacityAffectsPulsing ? maxPulsingOpacity * movementRatio : maxPulsingOpacity))
                    .setAngleOffset(randomAngle + angleInterval)
                    .build()
            );

        }

        level.createEvent(floor, new MoveDecorations.Builder()
                .setDuration(0d)
                .setTag(characterTag)
                .setOpacity(getOpacity())
                .setAngleOffset(endAngle)
                .build()
        );
    }

    @Override
    public void out(CustomLevel level, int floor, Point2D characterPosition, Point2D scale, HexColor color, Tag characterTag, double angleOffset) {
        level.createEvent(floor, new MoveDecorations.Builder()
                .setDuration(getDuration())
                .setPivotOffset(characterPosition.add(getDisplacement()))
                .setTag(characterTag)
                .setScale(scale)
                .setColor(color)
                .setEase(getEase())
                .setAngleOffset(angleOffset)
                .build()
        );

        int pulses = pulsesRange.random();
        double endAngle = angleOffset + getDuration() * 180;

        for (int i = 0; i < pulses; i++) {
            double invisibleSec = invisibleSecRange.random();
            double angleInterval = level.getAngleOffsetByTimeSec(floor, invisibleSec);
            double randomAngle = AdvancedMath.random(angleOffset, endAngle - angleInterval - 0.1);
            double movementRatio = 1 - AdvancedMath.getRatio(angleOffset, endAngle, randomAngle);

            level.createEvent(floor, new MoveDecorations.Builder()
                    .setDuration(0d)
                    .setTag(characterTag)
                    .setOpacity(AdvancedMath.doubleRandom(opacityAffectsPulsing ? maxPulsingOpacity * movementRatio : maxPulsingOpacity))
                    .setAngleOffset(randomAngle)
                    .build()
            );

            level.createEvent(floor, new MoveDecorations.Builder()
                    .setDuration(0d)
                    .setTag(characterTag)
                    .setOpacity(getOpacity() * movementRatio)
                    .setAngleOffset(randomAngle + angleInterval)
                    .build()
            );

        }
        level.createEvent(floor, new MoveDecorations.Builder()
                .setDuration(0d)
                .setTag(characterTag)
                .setOpacity(0d)
                .setAngleOffset(endAngle)
                .build()
        );
    }
}

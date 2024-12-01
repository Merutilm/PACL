package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.DecorationFile;
import kr.tkdydwk7071.base.struct.HexColor;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.base.struct.Tag;
import kr.tkdydwk7071.base.util.AdvancedMath;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.MoveDecorations;
import kr.tkdydwk7071.pacl.adofailevel.event.events.decoration.AddDecoration;
import kr.tkdydwk7071.pacl.adofailevel.event.selectable.RelativeTo;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class Sample2DLighting implements VFX {
    @Override
    public void apply(CustomLevel level) {
        int startTile = 2409;
        double duration = 144;
        double speed = 10;
        double opacity = 100;
        Point2D lightSize = new Point2D(70, 3);
        DecorationFile di = new DecorationFile("fog_combined.png");
        String tagAll = "nevLTN";

        level.createEvent(startTile, new MoveDecorations.Builder()
                .setDuration(0d)
                .setTag(Tag.of(tagAll))
                .setPositionOffset(new Point2D(Double.NaN, 9))
                .setScale(lightSize)
                .build());

        level.createEvent(startTile, new MoveDecorations.Builder()
                .setDuration(8d)
                .setTag(Tag.of(tagAll))
                .setPositionOffset(new Point2D(Double.NaN, 3))
                .setEase(Ease.OUT_CUBIC)
                .setOpacity(opacity)
                .build());

        level.createEvent(startTile, new MoveDecorations.Builder()
                .setPositionOffset(new Point2D(-speed, Double.NaN))
                .setDuration(duration)
                .setTag(Tag.of(tagAll))
                .build());

        for (int i = 0; i < 100; i++) {

            level.createEvent(startTile, new AddDecoration.Builder()
                    .setScale(Point2D.ORIGIN)
                    .setPosition(new Point2D(150 - i * 3, 0))
                    .setRelativeTo(RelativeTo.AddDecoration.CAMERA)
                    .setLockRotation(true)
                    .setLockScale(true)
                    .setOpacity(0d)
                    .setDecorationImage(di)
                    .setTag(Tag.of(tagAll, tagAll + i))
                    .setDepth((short) 19)
                    .build());

            double interval = AdvancedMath.random(5, 8);

            for (int i1 = 0; i1 < (int) (duration / interval - 1); i1++) {

                level.createEvent(startTile, new MoveDecorations.Builder()
                        .setDuration(0.1d)
                        .setTag(Tag.of(tagAll + i))
                        .setAngleOffset(i1 * interval * 180 + AdvancedMath.random(0, 100))
                        .setEase(Ease.OUT_CUBIC)
                        .setColor(HexColor.get(255, 255, 255, AdvancedMath.random(3, 200)))
                        .build());

                level.createEvent(startTile, new MoveDecorations.Builder()
                        .setDuration(0.1d)
                        .setAngleOffset(i1 * interval * 180 + 40 + AdvancedMath.random(0, 100))
                        .setTag(Tag.of(tagAll + i))
                        .setEase(Ease.OUT_CUBIC)
                        .setColor(HexColor.WHITE)
                        .build());
            }
        }

        level.createEvent(startTile, new MoveDecorations.Builder()
                .setDuration(0d)
                .setAngleOffset(duration * 180)
                .setTag(Tag.of(tagAll))
                .setOpacity(0d)
                .build());
    }
}

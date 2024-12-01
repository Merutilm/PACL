package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.*;
import kr.tkdydwk7071.base.util.AdvancedMath;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.VFXGeneratorTypos;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.variable.TextAppearance;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.MoveDecorations;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXFloatingRandomNumberText implements VFX {
    @Override
    public void apply(CustomLevel level) {


        VFXGeneratorTypos renderer = new VFXGeneratorTypos(level, 5);
        int floor = 2320;
        double renderDuration = 208;
        double moveDuration = 64;
        double angleInterval = 960;
        Range parallaxRange = new Range(50, 80);

        for (int groupID = 1; groupID < renderDuration * 180 / angleInterval; groupID++) {
            renderer.attributeAsset
                    .setDefaultParallax(new Point2D(parallaxRange.random(),100))
                    .setDefaultDepth((short) 20)
                    .setLockToCamera(false);

            create(renderer, floor, groupID, moveDuration, groupID * angleInterval - 720);
        }

        level.createEvent(floor, new MoveDecorations.Builder()
                .setTag(Tag.of(renderer.tagAllTypos))
                .setOpacity(0d)
                .setColor(HexColor.TRANSPARENT)
                .setAngleOffset(renderDuration * 180)
                .build()
        );
    }


    public void create(VFXGeneratorTypos renderer, int floor, int groupID, double moveDuration, double angleOffset) {

        IntRange randomDigits = new IntRange(1, 3);
        double positionRange = 10;
        double randomRange = 3;
        Range scale = new Range(50, 120);
        double spacing = 0.5;
        double changeIntervalBeats = 0.5;
        HexColor color = HexColor.get(255, 255, 255, 40);

        //_______________________________________________

        Point2D start = new Point2D(positionRange, 0).getInCircleRandomPoint(randomRange);
        int digits = randomDigits.random();
        double finalScale = scale.random();


        renderer.in(floor, randomNumber(digits), start, 0, spacing, finalScale, color, TextAppearance.nullAppearance(), angleOffset);
        renderer.moveText(floor, groupID, moveDuration, start.edit().invertX().build(), finalScale, spacing, Ease.LINEAR, angleOffset);

        for (int i = 0; i * changeIntervalBeats < moveDuration; i++) {
            renderer.changeText(floor, groupID, randomNumber(digits), angleOffset + i * changeIntervalBeats * 180);
        }

        renderer.out(floor, groupID, TextAppearance.nullAppearance(), angleOffset + moveDuration * 180);

    }


    public String randomNumber(int digits) {
        return String.format("%0" + digits + "d", AdvancedMath.intRandom((int) Math.pow(10, digits)));
    }
}

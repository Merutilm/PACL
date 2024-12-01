package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.DecorationFile;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.base.struct.Tag;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.MoveDecorations;
import kr.tkdydwk7071.pacl.adofailevel.event.events.decoration.AddDecoration;
import kr.tkdydwk7071.pacl.data.CustomLevel;

import java.util.function.IntBinaryOperator;

final class VFXExpandingCirclesMotion implements VFX {
    @Override
    public void apply(CustomLevel level) {

        int floor = 250;
        double duration = 1;
        int id = 1;
        int layers = 100;
        double delayBeats = 0.01;
        short depth = -32750;
        IntBinaryOperator depthFunction = (d, i) -> d + i * 2;
        double maxOpacity = 50;
        double scalePower = 1;
        double normalScale = 1000;

        String name = "circle_thin";
        Ease ease = Ease.OUT_CUBIC;

        for (int i = 1; i <= layers; i++) {
            level.createEvent(floor, new AddDecoration.Builder()
                    .setDecorationImage(new DecorationFile(name + ".png"))
                    .setOpacity(0d)
                    .setDepth((short) depthFunction.applyAsInt(depth, i))
                    .setScale(new Point2D(0, 0))
                    .setTag(Tag.of("cl" + id, "cl" + id + "." + i))
                    .build()
            );

            double scale = normalScale * Math.pow(scalePower, i);

            level.createEvent(floor, new MoveDecorations.Builder()
                    .setTag(Tag.of("cl" + id + "." + i))
                    .setOpacity((1 - i / layers) * maxOpacity)
                    .setDuration(0d)
                    .setAngleOffset(i * delayBeats * 180)
                    .build()
            );

            level.createEvent(floor, new MoveDecorations.Builder()
                    .setOpacity(0d)
                    .setTag(Tag.of("cl" + id + "." + i))
                    .setScale(new Point2D(scale, scale))
                    .setDuration(duration)
                    .setEase(ease)
                    .setAngleOffset(i * delayBeats * 180)
                    .build()
            );
        }
    }

}

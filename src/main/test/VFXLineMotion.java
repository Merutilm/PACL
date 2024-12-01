package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.functions.FunctionEase;
import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.DecorationFile;
import kr.tkdydwk7071.base.struct.HexColor;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.base.struct.Tag;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.MoveDecorations;
import kr.tkdydwk7071.pacl.adofailevel.event.events.decoration.AddDecoration;
import kr.tkdydwk7071.pacl.adofailevel.event.selectable.BlendMode;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXLineMotion implements VFX {
    @Override
    public void apply(CustomLevel level) {
        String tag = "lm";
        String name = "thin_square";
        int floor = 251;

        double scale = 1000;
        double renderDuration = 128;
        double bounceDuration = 8;

        HexColor color = HexColor.get(11,11,11);

        FunctionEase ratioAngleFunction = r -> Math.pow(Math.sin(11 * Math.PI * r), 2) + 3 * Math.pow(Math.sin(Math.PI * r), 2); //0 -> 0, 1 -> 360
        FunctionEase ratioScaleFunction = r -> 1 - 0.5 * Math.sin(40 * Math.PI * r); /// 0 -> 0, 1 -> scale

        int group = 1;
        int layersPerBounce = 200;
        int depth = 5;


        // --------------------------------------------------

        String allTag = tag + group;

        int layers = (int) (layersPerBounce * renderDuration / bounceDuration);

        for (int i = 1; i <= layersPerBounce; i++) {
            String layerTag = tag + group + "." + i;

            level.createEvent(floor,  new AddDecoration.Builder()
                    .setTag(Tag.of(allTag, layerTag))
                    .setDecorationImage(new DecorationFile(name + ".png"))
                    .setBlendMode(BlendMode.LINEAR_DODGE)
                    .setOpacity(0d)
                    .setScale(Point2D.ORIGIN)
                    .setParallax(new Point2D(100, 100))
                    .setColor(color)
                    .setDepth((short) (depth + i))
                    .build()
            );
        }

        for (int i = 1; i <= layers; i++) {

            String layerTag = tag + group + "." + (i % layersPerBounce + 1);

            double finalScale = scale * ratioScaleFunction.apply((double) i / layers);

            level.createEvent(floor,  new MoveDecorations.Builder()
                    .setTag(Tag.of(layerTag))
                    .setRotationOffset(ratioAngleFunction.apply((double) i / layers) * 360.0 % 360)
                    .setAngleOffset(renderDuration * i / layers * 180)
                    .build()
            );


            level.createEvent(floor,  new MoveDecorations.Builder()
                    .setTag(Tag.of(layerTag))
                    .setScale(new Point2D(finalScale, finalScale))
                    .setDuration(bounceDuration / 2)
                    .setAngleOffset(renderDuration * i / layers * 180)
                    .setEase(Ease.OUT_QUAD)
                    .build()
            );

            level.createEvent(floor,  new MoveDecorations.Builder()
                    .setTag(Tag.of(layerTag))
                    .setScale(Point2D.ORIGIN)
                    .setDuration(bounceDuration / 2)
                    .setAngleOffset((renderDuration * i / layers + bounceDuration / 2) * 180)
                    .setEase(Ease.IN_QUAD)
                    .build()
            );
        }

        level.createEvent(floor,  new MoveDecorations.Builder()
                .setTag(Tag.of(allTag))
                .setOpacity(100d)
                .build()
        );

    }
}

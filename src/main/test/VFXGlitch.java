package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.struct.DecorationFile;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.base.struct.Tag;
import kr.tkdydwk7071.base.util.AdvancedMath;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t5mrays.VFXGeneratorGodRays;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t5mrays.variable.RayType;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.MoveDecorations;
import kr.tkdydwk7071.pacl.adofailevel.event.selectable.BlendMode;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXGlitch implements VFX {
    @Override
    public void apply(CustomLevel level) {

        int renderGroupID = 2;
        VFXGeneratorGodRays godRays = new VFXGeneratorGodRays(level, renderGroupID);
        int start = 3467;
        int end = 3562;
        create(godRays, start, end);

        double converted = level.convertAngleOffset(end, 0, start);
        for (int i = start; i <= end; i++) {
            for (int j = 1; j <= 5; j++) {
                DecorationFile decorationFile;
                int t = (int) level.getIncludedAngle(i);
                if (t > 180 && t != 360) {
                    t = 360 - t;
                }
                int r = j <= 2 ? AdvancedMath.intRandom(2) : AdvancedMath.intRandom(4);
                double m = Math.pow(2, j - 1);
                double opacity = 100 / (m);
                double rotation = 0;
                switch (r) {
                    case 0 -> decorationFile = new DecorationFile("nev_travel_g" + t + ".png");
                    case 1 -> decorationFile = new DecorationFile("nev_travel_h" + t + ".png");
                    case 2 -> {
                        //opacity /= 2;
                        decorationFile = new DecorationFile("CodeGlitch3_small.jpg");
                        rotation = AdvancedMath.doubleRandom(360);
                    }
                    default -> {
                        //opacity /= 2;
                        decorationFile = new DecorationFile("CodeGlitch2_small.jpg");
                        rotation = AdvancedMath.doubleRandom(360);
                    }
                }


                double a = AdvancedMath.doubleRandom(converted);

                level.createEvent(start, new MoveDecorations.Builder()
                        .setDuration(0d)
                        .setImage(decorationFile)
                        .setDepth((short) -503)
                        .setParallax(new Point2D(0, 0))
                        .setPositionOffset(Point2D.ORIGIN.getInCircleRandomPoint(0.1 * Math.pow(1.7, m)))
                        .setRotationOffset(rotation)
                        .setOpacity(opacity)
                        .setTag(Tag.of("nevMGR-I" + renderGroupID + "." + i + "_" + j))
                        .setEventTag(Tag.of("glitch"))
                        .setAngleOffset(a - 360)
                        .build()
                );

                level.createEvent(start, new MoveDecorations.Builder()
                        .setDuration(0d)
                        .setOpacity(0.0)
                        .setTag(Tag.of("nevMGR-I" + renderGroupID + "." + i + "_" + j))
                        .setEventTag(Tag.of("glitch"))
                        .setAngleOffset(a + 360)
                        .build()
                );
            }
        }
    }

    private void create(VFXGeneratorGodRays godRays, int start, int end) {

        for (int i = start; i <= end; i++) {
            godRays.addGodRays(i, i, Point2D.ORIGIN.getInCircleRandomPoint(60),
                    101, 2.3, 5,
                    RayType.HOLLOW, BlendMode.NONE,90, RayType.HOLLOW, 0, 0.7, false, e -> true);
        }

    }

}

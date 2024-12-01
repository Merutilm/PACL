package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.HexColor;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.base.struct.Tag;
import kr.tkdydwk7071.base.util.AdvancedMath;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t2manimtile.VFXGeneratorAnimateTile;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t2manimtile.attribute.AttributeAnimation;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.RecolorTrack;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.RepeatEvents;
import kr.tkdydwk7071.pacl.adofailevel.event.selectable.TrackColorPulse;
import kr.tkdydwk7071.pacl.adofailevel.event.selectable.TrackColorType;
import kr.tkdydwk7071.pacl.adofailevel.event.selectable.TrackStyle;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXNeonBlinkingTile implements VFX {
    @Override
    public void apply(CustomLevel level) {
        int start = 2320;
        int end = level.getLength() - 1;
        double scale = 90;


        for (int floor = start; floor < end; floor++) {
            String eventTag = "nevNNT";
            int repetition = 20;
            double angleOffset = AdvancedMath.doubleRandom(720) - repetition / 2.0 * 180;

            level.createEvent(floor, new RecolorTrack.Builder()
                    .setTrackColor(HexColor.TRANSPARENT)
                    .setTrackStyle(TrackStyle.NEON)
                    .setDuration(0d)
                    .setTrackGlowIntensity(40)
                    .setAngleOffset(angleOffset)
                    .setEventTag(Tag.of(eventTag))
                    .build()
            );

            level.createEvent(floor, new RecolorTrack.Builder()
                    .setTrackColor(HexColor.WHITE)
                    .setSecondaryTrackColor(HexColor.get(87, 87, 87, 255))
                    .setTrackColorAnimDuration(2d)
                    .setTrackPulseLength(10)
                    .setTrackGlowIntensity(40)
                    .setTrackColorType(TrackColorType.GLOW)
                    .setTrackStyle(TrackStyle.NEON)
                    .setTrackColorPulse(TrackColorPulse.FORWARD)
                    .setDuration(0d)
                    .setAngleOffset(angleOffset + AdvancedMath.random(30, 85))
                    .setEventTag(Tag.of(eventTag))
                    .build()
            );

            level.createEvent(floor, new RepeatEvents.Builder()
                    .setTag(Tag.of(eventTag))
                    .setRepetitions(repetition)
                    .setInterval(AdvancedMath.random(3, 4))
                    .build()
            );

        }

        VFXGeneratorAnimateTile animateTile = new VFXGeneratorAnimateTile(level);
        animateTile.addAdvancedTileAnimation(start, end,
                null,
                scale, 100, 90, 1, 100,
                new AttributeAnimation(0,
                        8,
                        new Point2D(0, 0),
                        0,
                        scale,
                        Ease.IN_CUBIC,
                        20,
                        1.5,
                        25,
                        0),
                null
        );
    }
}

package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.struct.DecorationFile;
import kr.tkdydwk7071.base.struct.Tag;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.MoveDecorations;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXDecorationGimBap implements VFX {
    @Override
    public void apply(CustomLevel level) {
        int floor = 2708;
        double angleInterval = 45;
        String name = "NOISE";
        String tag = "vd";
        Tag eventTag = Tag.of("r");
        int amount = 24;
        for (int i = 1; i <= amount; i++) {
            level.createEvent(floor, new MoveDecorations.Builder()
                    .setImage(new DecorationFile(name + " (" + i + ").png"))
                    .setAngleOffset(angleInterval * i)
                    .setPositionOffset(null)
                    .setTag(Tag.of(tag))
                    .setEventTag(eventTag)
                    .build()
            );
        }
    }
}

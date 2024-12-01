package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.struct.EID;
import kr.tkdydwk7071.base.util.AdvancedMath;
import kr.tkdydwk7071.pacl.adofailevel.event.events.*;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.Actions;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.ActionsBuilder;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXAngleCorrection implements VFX {
    @Override
    public void apply(CustomLevel level) {

        int minFloor = 0;
        int maxFloor = level.getLength();

        for (int i = 0; i < level.getLength(); i++) {
            int size = level.getFloorActions(i).size();
            for (int j = 0; j < size; j++) {
                EID id = new EID(i, j);
                Actions actions = level.getAction(id);
                if (actions == null) {
                    continue;
                }
                ActionsBuilder actionsBuilder = actions.edit();

                if (!(actions instanceof RelativeTileEvent) && ((actions instanceof EventTagEvent e && e.eventTag().isEmpty()) || !(actions instanceof EventTagEvent)) && actions.isVFX() && actions instanceof AngleOffsetEvent event && actionsBuilder instanceof AngleOffsetEventBuilder builder) {
                    int floor = AdvancedMath.restrict(minFloor, maxFloor, level.convertFloor(i, event.angleOffset()));
                    builder.setAngleOffset(level.convertAngleOffset(i, event.angleOffset(), floor));
                    if (actions instanceof DoubleDurationEvent e2 && actionsBuilder instanceof DoubleDurationEventBuilder b2) {
                        b2.setDuration(e2.duration() * level.getBPM(floor) / level.getBPM(i));
                    }
                    level.setAction(id, actionsBuilder.build());
                    level.moveAction(id, floor);


                }


            }
        }
    }
}

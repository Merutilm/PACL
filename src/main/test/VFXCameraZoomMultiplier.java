package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.struct.EID;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.Actions;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.MoveCamera;
import kr.tkdydwk7071.pacl.data.CustomLevel;

import java.util.List;

final class VFXCameraZoomMultiplier implements VFX {
    @Override
    public void apply(CustomLevel level) {
        int startTile = 3619;
        int endTile = 4527;
        double multiplier = 1.2;
        for (int i = startTile; i <= endTile; i++) {
            List<Actions> actions = level.getFloorActions(i);
            for (int j = 0; j < actions.size(); j++) {
                EID actionID = new EID(i, j);
                if (actions.get(j) instanceof MoveCamera c) {
                    if (c.zoom() != null) {
                        level.setAction(actionID, c.edit().setZoom(c.zoom() * multiplier).build());
                    }
                }
            }
        }

    }
}

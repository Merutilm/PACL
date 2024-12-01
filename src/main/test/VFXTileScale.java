package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.EID;
import kr.tkdydwk7071.base.struct.Tag;
import kr.tkdydwk7071.base.util.AdvancedMath;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.PositionTrack;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.ScalePlanets;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.ScaleRadius;
import kr.tkdydwk7071.pacl.adofailevel.event.selectable.TargetPlanet;
import kr.tkdydwk7071.pacl.data.CustomLevel;

import java.util.List;

final class VFXTileScale implements VFX {
    @Override
    public void apply(CustomLevel level) {


        //settings start

        int startTile = 1585;
        int endTile = 1989;
        int waves = 4;
        double maxScaleMultiplier = 0.5;
        double tileScaleMultiplier = 0.96;

        //settings end


        for (int floor = startTile; floor <= endTile; floor++) {

            double ratio = AdvancedMath.getRatio(startTile, endTile, floor);
            double rad = 100 * Math.pow(maxScaleMultiplier, Math.sin(Math.PI * ratio * waves));


            level.createEvent(floor, new ScaleRadius(true, rad));
            level.createEvent(floor, new ScalePlanets(true, level.getTravelAngle(floor) / 180, TargetPlanet.ALL, rad, 0d, Ease.LINEAR, Tag.of()));

            List<EID> actionIDs = level.getActionEIDs(floor, PositionTrack.class);

            double finalScale = rad * tileScaleMultiplier;

            if (actionIDs.isEmpty()) {

                level.createEvent(floor, new PositionTrack.Builder()
                        .setScale(finalScale)
                        .build()
                );

            } else {
                EID id = actionIDs.get(0);
                PositionTrack event = (PositionTrack) level.getAction(id);
                assert event != null;

                level.setAction(id, event.edit().setScale(finalScale).build());
            }
        }
    }
}

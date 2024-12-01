package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.struct.EID;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.*;
import kr.tkdydwk7071.pacl.adofailevel.event.events.decoration.AddDecoration;
import kr.tkdydwk7071.pacl.adofailevel.event.events.decoration.AddText;
import kr.tkdydwk7071.pacl.adofailevel.event.events.decoration.Decorations;
import kr.tkdydwk7071.pacl.data.CustomLevel;

import java.util.List;

final class VFXXFlipper implements VFX {
    @Override
    public void apply(CustomLevel level) {
        int startTile = 4742;
        int endTile = 5170;

        for (int i = startTile; i <= endTile; i++) {
            List<Actions> actions = level.getFloorActions(i);
            for (int j = 0; j < actions.size(); j++) {
                EID actionID = new EID(i, j);
                if (actions.get(j) instanceof MoveCamera a) {
                    MoveCamera.Builder builder = a.edit();
                    if (a.position() != null) {
                        builder.setPosition(a.position().edit().invertX().build());
                    }
                    if (a.rotation() != null) {
                        builder.setRotation(-a.rotation());
                    }
                    level.setAction(actionID, builder.build());
                }
                if (actions.get(j) instanceof PositionTrack a) {
                    PositionTrack.Builder builder = a.edit();
                    if (a.positionOffset() != null) {
                        builder.setPositionOffset(a.positionOffset().edit().invertX().build());
                    }
                    if (a.rotation() != null) {
                        builder.setRotation(-a.rotation());
                    }
                    level.setAction(actionID, builder.build());
                }
                if(actions.get(j) instanceof MoveTrack a){
                    MoveTrack.Builder builder = a.edit();
                    if (a.positionOffset() != null) {
                        builder.setPositionOffset(a.positionOffset().edit().invertX().build());
                    }
                    if (a.rotationOffset() != null) {
                        builder.setRotationOffset(-a.rotationOffset());
                    }
                    level.setAction(actionID, builder.build());
                }
                if(actions.get(j) instanceof MoveDecorations a){
                    MoveDecorations.Builder builder = a.edit();
                    if (a.positionOffset() != null) {
                        builder.setPositionOffset(a.positionOffset().edit().invertX().build());
                    }
                    if (a.pivotOffset() != null) {
                        builder.setPivotOffset(a.pivotOffset().edit().invertX().build());
                    }
                    if (a.parallaxOffset() != null) {
                        builder.setParallaxOffset(a.parallaxOffset().edit().invertX().build());
                    }
                    if (a.rotationOffset() != null) {
                        builder.setRotationOffset(-a.rotationOffset());
                    }
                    level.setAction(actionID, builder.build());
                }
            }
            List<Decorations> decorations = level.getFloorDecorations(i);
            for (int j = 0; j < decorations.size(); j++) {
                EID decorationID = new EID(i, j);
                if(decorations.get(j) instanceof AddDecoration d){
                    AddDecoration.Builder builder = d.edit();
                    if (d.position() != null) {
                        builder.setPosition(d.position().edit().invertX().build());
                    }
                    if (d.pivotOffset() != null) {
                        builder.setPivotOffset(d.pivotOffset().edit().invertX().build());
                    }
                    if (d.parallaxOffset() != null) {
                        builder.setParallaxOffset(d.parallaxOffset().edit().invertX().build());
                    }
                    if (d.rotation() != null) {
                        builder.setRotation(-d.rotation());
                    }
                    level.setDecoration(decorationID, builder.build());
                }
                if(decorations.get(j) instanceof AddText d){
                    AddText.Builder builder = d.edit();
                    if (d.position() != null) {
                        builder.setPosition(d.position().edit().invertX().build());
                    }
                    if (d.pivotOffset() != null) {
                        builder.setPivotOffset(d.pivotOffset().edit().invertX().build());
                    }
                    if (d.parallaxOffset() != null) {
                        builder.setParallaxOffset(d.parallaxOffset().edit().invertX().build());
                    }
                    if (d.rotation() != null) {
                        builder.setRotation(-d.rotation());
                    }
                    level.setDecoration(decorationID, builder.build());
                }
            }
        }

    }
}

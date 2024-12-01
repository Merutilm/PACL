package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.struct.EID;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.pacl.adofailevel.event.events.decoration.AddDecoration;
import kr.tkdydwk7071.pacl.adofailevel.event.events.decoration.Decorations;
import kr.tkdydwk7071.pacl.adofailevel.event.selectable.BlendMode;
import kr.tkdydwk7071.pacl.data.CustomLevel;

import java.util.Objects;

final class VFXAllDecorationsBlender implements VFX {
    @Override
    public void apply(CustomLevel level) {
        for (int i = 0; i < level.getLength(); i++) {
            for (int j = 0; j < level.getFloorDecorations(i).size(); j++) {
                EID id = new EID(i, j);
                Decorations decorations = level.getDecoration(id);
                if (decorations instanceof AddDecoration a) {
                    if (Objects.equals(a.tile(),new Point2D(1,1))) {
                        level.setDecoration(id, a.edit().setLocked(true).setBlendMode(BlendMode.DIFFERENCE).build());
                    }
                }

            }
        }
    }
}

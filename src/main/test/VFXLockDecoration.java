package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.struct.EID;
import kr.tkdydwk7071.pacl.adofailevel.event.events.decoration.AddDecoration;
import kr.tkdydwk7071.pacl.adofailevel.event.events.decoration.AddText;
import kr.tkdydwk7071.pacl.adofailevel.event.events.decoration.Decorations;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXLockDecoration implements VFX {
    @Override
    public void apply(CustomLevel level) {
        for (int i = 0; i < level.getLength(); i++) {
            for (int j = 0; j < level.getFloorDecorations(i).size(); j++) {
                EID id = new EID(i, j);
                Decorations decorations = level.getDecoration(id);
                if (decorations instanceof AddDecoration a) {
                    level.setDecoration(id, a.edit().setLocked(true).build());
                }
                if (decorations instanceof AddText a) {
                    level.setDecoration(id, a.edit().setLocked(true).build());
                }

            }
        }
    }
}

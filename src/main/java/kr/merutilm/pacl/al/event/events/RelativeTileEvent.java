package kr.merutilm.pacl.al.event.events;

import kr.merutilm.pacl.al.event.struct.RelativeTile;

public interface RelativeTileEvent {
    RelativeTile startTile();
    RelativeTile endTile();
    Integer gap();
}

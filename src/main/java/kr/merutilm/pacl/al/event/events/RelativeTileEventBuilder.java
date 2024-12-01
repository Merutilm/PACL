package kr.merutilm.pacl.al.event.events;

import kr.merutilm.pacl.al.event.struct.RelativeTile;

public interface RelativeTileEventBuilder {
    RelativeTileEventBuilder setStartTile(RelativeTile startTile);
    RelativeTileEventBuilder setEndTile(RelativeTile endTile);
    RelativeTileEventBuilder setGap(Integer gap);
}

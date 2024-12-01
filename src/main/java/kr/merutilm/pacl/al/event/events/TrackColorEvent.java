package kr.merutilm.pacl.al.event.events;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.pacl.al.event.selectable.TrackColorPulse;
import kr.merutilm.pacl.al.event.selectable.TrackColorType;
import kr.merutilm.pacl.al.event.selectable.TrackStyle;

public interface TrackColorEvent {
    TrackColorType trackColorType();
    HexColor trackColor();
    HexColor secondaryTrackColor();
    Double trackColorAnimDuration();
    TrackColorPulse trackColorPulse();
    Integer trackPulseLength();
    TrackStyle trackStyle();
    Integer trackGlowIntensity();
}

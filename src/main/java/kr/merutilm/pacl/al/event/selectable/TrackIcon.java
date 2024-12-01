package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum TrackIcon implements Selectable {
    NONE("None"),
    SNAIL("Snail"),
    DOUBLE_SNAIL("DoubleSnail"),
    RABBIT("Rabbit"),
    DOUBLE_RABBIT("DoubleRabbit"),
    SWIRL("Swirl"),
    CHECKPOINT("Checkpoint"),
    HOLD_ARROW_SHORT("HoldArrowShort"),
    HOLD_ARROW_LONG("HoldArrowLong"),
    HOLD_RELEASE_SHORT("HoldReleaseShort"),
    HOLD_RELEASE_LONG("HoldReleaseLong"),
    TWO_PLANETS("MultiPlanetTwo"),
    THREE_PLANETS("MultiPlanetThreeMore"),
    PORTAL("Portal");
    private final String name;

    @Override
    public String toString() {
        return name;
    }

    TrackIcon(String name) {
        this.name = name;
    }

    public static TrackIcon typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}
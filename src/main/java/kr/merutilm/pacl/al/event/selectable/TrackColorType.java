package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum TrackColorType implements Selectable {
    SINGLE("Single"),
    STRIPES("Stripes"),
    GLOW("Glow"),
    BLINK("Blink"),
    SWITCH("Switch"),
    RAINBOW("Rainbow"),
    VOLUME("Volume");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    TrackColorType(String name) {
        this.name = name;
    }

    public static TrackColorType typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }

}

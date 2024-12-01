package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum TrackType implements Selectable {
    NORMAL("Normal"),
    MIDSPIN("Midspin");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    TrackType(String name) {
        this.name = name;
    }

    public static TrackType typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}

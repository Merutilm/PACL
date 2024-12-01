package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum TrackStyle implements Selectable {
    STANDARD("Standard"),
    NEON("Neon"),
    NEON_LIGHT("NeonLight"),
    BASIC("Basic"),
    GEMS("Gems"),
    MINIMAL("Minimal");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    TrackStyle(String name) {
        this.name = name;
    }

    public static TrackStyle typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}
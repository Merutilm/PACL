package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum TrackColorPulse implements Selectable {
    NONE("None"),
    FORWARD("Forward"),
    BACKWARD("Backward");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    TrackColorPulse(String name) {
        this.name = name;
    }

    public static TrackColorPulse typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }

}

package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;


public enum BGDisplayMode implements Selectable {
    FIT_TO_SCREEN("FitToScreen"),
    UNSCALED("Unscaled"),
    TILED("Tiled");
    private final String name;

    @Override
    public String toString() {
        return name;
    }

    BGDisplayMode(String name) {
        this.name = name;
    }

    public static BGDisplayMode typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}

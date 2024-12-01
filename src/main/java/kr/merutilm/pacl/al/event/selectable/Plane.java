package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum Plane implements Selectable {
    BACKGROUND("Background"),
    FOREGROUND("Foreground");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    Plane(String name) {
        this.name = name;
    }

    public static Plane typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}
package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum Planets implements Selectable {
    TWO_PLANETS("TwoPlanets"),
    THREE_PLANETS("ThreePlanets");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    Planets(String name) {
        this.name = name;
    }

    public static Planets typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}
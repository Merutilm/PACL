package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum PlanetColorType implements Selectable {
    RED("DefaultRed"),
    BLUE("DefaultBlue"),
    GOLD("Gold"),
    OVERSEER("Overseer"),
    CUSTOM("Custom");
    private final String name;

    @Override
    public String toString() {
        return name;
    }

    PlanetColorType(String name) {
        this.name = name;
    }

    public static PlanetColorType typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}
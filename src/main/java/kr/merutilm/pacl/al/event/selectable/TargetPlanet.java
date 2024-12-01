package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum TargetPlanet implements Selectable {
    FIRE("FirePlanet"),
    ICE("IcePlanet"),
    WIND("WindPlanet"),
    ALL("All");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    TargetPlanet(String name) {
        this.name = name;
    }

    public static TargetPlanet typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}
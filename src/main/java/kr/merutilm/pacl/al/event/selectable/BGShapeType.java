package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum BGShapeType implements Selectable {
    DEFAULT("Default"),
    SOLID_COLOR("SingleColor"),
    NONE("Disabled");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    BGShapeType(String name) {
        this.name = name;
    }

    public static BGShapeType typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}

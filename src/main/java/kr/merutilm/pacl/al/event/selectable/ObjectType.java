package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum ObjectType implements Selectable {
    FLOOR("Floor"),
    PLANET("Planet");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    ObjectType(String name) {
        this.name = name;
    }

    public static ObjectType typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}
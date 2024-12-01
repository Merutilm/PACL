package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum FailHitBox implements Selectable {
    BOX("Box"),
    CIRCLE("Circle"),
    CAPSULE("Capsule");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    FailHitBox(String name) {
        this.name = name;
    }

    public static FailHitBox typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}

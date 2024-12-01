package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum EasePartBehavior implements Selectable {
    MIRROR("Mirror"),
    REPEAT("Repeat");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    EasePartBehavior(String name) {
        this.name = name;
    }

    public static EasePartBehavior typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}

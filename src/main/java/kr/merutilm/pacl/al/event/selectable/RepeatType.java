package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum RepeatType implements Selectable {
    BEAT("Beat"),
    FLOOR("Floor");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    RepeatType(String name) {
        this.name = name;
    }

    public static RepeatType typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }

}

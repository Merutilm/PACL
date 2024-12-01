package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum GameSound implements Selectable {
    HITSOUND("Hitsound"),
    MIDSPIN("Midspin");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    GameSound(String name) {
        this.name = name;
    }

    public static GameSound typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}

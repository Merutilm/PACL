package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum TrackDisappearAnimation implements Selectable {
    NONE("None"),
    SCATTER("Scatter"),
    SCATTER_FAR("Scatter_Far"),
    RETRACT("Retract"),
    SHRINK("Shrink"),
    SHRINK_SPIN("Shrink_Spin"),
    FADE("Fade");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    TrackDisappearAnimation(String name) {
        this.name = name;
    }

    public static TrackDisappearAnimation typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}

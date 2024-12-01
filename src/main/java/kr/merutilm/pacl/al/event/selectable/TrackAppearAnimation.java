package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum TrackAppearAnimation implements Selectable {
    NONE("None"),
    ASSEMBLE("Assemble"),
    ASSEMBLE_FAR("Assemble_Far"),
    EXTEND("Extend"),
    GROW("Grow"),
    GROW_SPIN("Grow_Spin"),
    FADE("Fade"),
    DROP("Drop"),
    RISE("Rise");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    TrackAppearAnimation(String name) {
        this.name = name;
    }

    public static TrackAppearAnimation typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}

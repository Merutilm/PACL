package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum MaskingType implements Selectable {
    NONE("None"),
    MASK("Mask"),
    VISIBLE_INSIDE("VisibleInsideMask"),
    VISIBLE_OUTSIDE("VisibleOutsideMask");
    private final String name;

    @Override
    public String toString() {
        return name;
    }

   MaskingType(String name) {
        this.name = name;
    }

    public static MaskingType typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}

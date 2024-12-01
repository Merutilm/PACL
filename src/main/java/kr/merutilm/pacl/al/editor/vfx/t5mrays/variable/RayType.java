package kr.merutilm.pacl.al.editor.vfx.t5mrays.variable;

import kr.merutilm.base.selectable.Selectable;

public enum RayType implements Selectable {
    SOLID("s"), HOLLOW("h"), LINE("l"), VERTEX("v"), GLITCH("g");

    private final String value;

    @Override
    public String toString() {
        return value;
    }

    RayType(String value) {
        this.value = value;
    }
}

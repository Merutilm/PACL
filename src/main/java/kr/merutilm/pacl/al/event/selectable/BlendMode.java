package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;
import kr.merutilm.base.struct.HexColor;

public enum BlendMode implements Selectable {
    NONE("None"),
    SCREEN("Screen"),
    LINEAR_DODGE("LinearDodge"),
    OVERLAY("Overlay"),
    SOFT_LIGHT("SoftLight"),
    DIFFERENCE("Difference"),
    MULTIPLY("Multiply");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    BlendMode(String name) {
        this.name = name;
    }

    public static BlendMode typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }

    public HexColor.ColorBlendMode match() {
        switch (this) {
            case NONE -> {
                return HexColor.ColorBlendMode.NORMAL;
            }
            case LINEAR_DODGE -> {
                return HexColor.ColorBlendMode.LINEAR_DODGE;
            }
            case SCREEN -> {
                return HexColor.ColorBlendMode.SCREEN;
            }
            case OVERLAY -> {
                return HexColor.ColorBlendMode.OVERLAY;
            }
            case DIFFERENCE -> {
                return  HexColor.ColorBlendMode.DIFFERENCE;
            }
            case SOFT_LIGHT -> {
                return HexColor.ColorBlendMode.SOFT_LIGHT;
            }
            case MULTIPLY -> {
                return HexColor.ColorBlendMode.MULTIPLY;
            }
            default -> throw new UnsupportedOperationException();
        }
    }

}

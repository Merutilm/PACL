package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum Font implements Selectable {
    DEFAULT("Default"),
    ARIAL("Arial"),
    COMIC_SANS_MS("ComicSansMS"),
    COURIER_NEW("CourierNew"),
    GEORGIA("Georgia"),
    IMPACT("Impact"),
    TIMES_NEW_ROMAN("TimesNewRoman");

    private final String name;

    @Override
    public String toString() {
        return name;
    }

    Font(String name) {
        this.name = name;
    }

    public static Font typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}
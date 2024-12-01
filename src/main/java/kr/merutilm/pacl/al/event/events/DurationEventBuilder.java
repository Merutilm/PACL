package kr.merutilm.pacl.al.event.events;

public interface DurationEventBuilder<T extends Number> {
    DurationEventBuilder<T> setDuration(T duration);
}

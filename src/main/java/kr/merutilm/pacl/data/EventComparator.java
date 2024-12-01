package kr.merutilm.pacl.data;

import javax.annotation.Nonnull;

import kr.merutilm.pacl.al.event.events.Event;

/**
 * 생성된 Event 인스턴스가 서로 동일한지 비교하기 위한 클래스. Map 에서 Key 값은 equals 를 사용합니다.
 */
record EventComparator(@Nonnull Event event) {

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof EventComparator com) {
            return event == com.event();
        } else return false;
    }
}

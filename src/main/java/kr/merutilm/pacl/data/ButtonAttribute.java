package kr.merutilm.pacl.data;

import javax.annotation.Nullable;

import kr.merutilm.customswing.CSButton;

/**
 * 버튼의 정보
 */
public final class ButtonAttribute {
    private final CSTimelinePanel.Painter painter;
    private final Location locationAttribute;
    private final EIN.EventData eventData;
    private final @Nullable Double duration;

    public Location getLocationAttribute() {
        return locationAttribute;
    }

    public EIN.EventData getEventInfo() {
        return eventData;
    }

    @Nullable
    public Double getDuration() {
        return duration;
    }

    /**
     * 이벤트 버튼 속성
     */
    ButtonAttribute(CSTimelinePanel.Painter painter, Location locationAttribute, EIN.EventData eventData, @Nullable Double duration) {
        this.painter = painter;
        this.locationAttribute = locationAttribute;
        this.eventData = eventData;
        this.duration = duration;
    }

    /**
     * 해당 버튼이 타임라인에 노출되는 상태면 true 를 리턴합니다.
     */
    boolean isShowing() {
        double startX = locationAttribute.startX();
        double endX = locationAttribute.endX();
        int yLineNumber = locationAttribute.yLineNumber();
        return painter.canVisible(startX, (double)yLineNumber * CSButton.BUTTON_HEIGHT, endX, (yLineNumber + 1) * CSButton.BUTTON_HEIGHT);
    }

    EIN.ABSEventData abstractEventInfo() {
        return new EIN.ABSEventData(eventData.eventID().floor(), eventData.event());
    }

    /**
     * 버튼의 위치 정보
     */
    public record Location(
            double startX,
            double endX,
            int yLineNumber
    ) {
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Location attribute) {
                return attribute.startX == startX && attribute.endX == endX && attribute.yLineNumber == yLineNumber;
            }
            return false;
        }
    }
}

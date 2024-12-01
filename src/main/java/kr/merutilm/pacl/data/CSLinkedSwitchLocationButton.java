package kr.merutilm.pacl.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 페인터용 연결된 이벤트 버튼
 */
public final class CSLinkedSwitchLocationButton {
    private final List<CSSwitchLocationVirtualButton> currentEnabledButton = new ArrayList<>();
    /**
     * 다중 선택 허용 여부
     */
    private boolean multiSelect = false;
    /**
     * 현재 선택 해제 중인지 여부
     */
    private boolean deselecting = false;

    /**
     * 현재 선택된 버튼
     */
    public List<CSSwitchLocationVirtualButton> getSelectedButtons() {
        return List.copyOf(currentEnabledButton);
    }

    /**
     * 다중 선택
     */
    public void multiSelect(List<CSSwitchLocationVirtualButton> target) {
        multiSelect = true;
        target.forEach(b -> b.getRealButton().select());
        multiSelect = false;
    }

    /**
     * 다중 선택
     */
    public void multiSelect(CSSwitchLocationVirtualButton target) {
        multiSelect = true;
        target.getRealButton().select();
        multiSelect = false;
    }

    /**
     * 다중 선택 해제
     */
    public void multiDeselect(List<CSSwitchLocationVirtualButton> target) {
        multiSelect = true;
        target.forEach(b -> b.getRealButton().deselect());
        multiSelect = false;
    }

    /**
     * 다중 선택 해제
     */
    public void multiDeselect(CSSwitchLocationVirtualButton target) {
        multiSelect = true;
        target.getRealButton().deselect();
        multiSelect = false;
    }

    /**
     * 해당 버튼을 다른 버튼과 연동합니다.
     */
    public void link(CSSwitchLocationVirtualButton target) {

        target.getRealButton().addSelectAction(e -> {
            if (!multiSelect) {
                deselectAll();
            }
            currentEnabledButton.add(target);
        });
        target.getRealButton().addDeselectAction(e -> {
            if (!multiSelect) {
                deselectAll();
            }
            if (!deselecting) {
                currentEnabledButton.remove(target);

            }
        });
    }

    /**
     * 현재 선택된 버튼을 모두 선택 해제합니다.
     */
    public void deselectAll() {
        if (deselecting) {
            return;
        }

        deselecting = true;
        Iterator<CSSwitchLocationVirtualButton> it = getSelectedButtons().iterator();
        currentEnabledButton.clear();
        while (it.hasNext()) {
            it.next().getRealButton().deselect();
        }

        deselecting = false;
    }

}

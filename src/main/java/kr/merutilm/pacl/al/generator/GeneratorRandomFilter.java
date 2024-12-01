package kr.merutilm.pacl.al.generator;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.pacl.al.event.events.Event;
import kr.merutilm.pacl.al.event.selectable.Filter;
import kr.merutilm.pacl.data.CustomLevel;

import java.util.List;

public final class GeneratorRandomFilter implements Generator {
    double bpm;
    int tiles;

    public GeneratorRandomFilter(double bpm, int tiles) {
        this.bpm = bpm;
        this.tiles = tiles;
    }

    @Override
    public CustomLevel generate() {
        CustomLevel level = CustomLevel.emptyLevel();
        level.setSettings(level.getSettings().edit()
                .setZoom(200d)
                .setBpm(bpm)
                .setShowDefaultBGIfNoImage(false)
                .build());

        for (int i = 1; i <= tiles; i++) {
            level.createAngle(0d);
            List<Event> filterPack = Filter.random((int) AdvancedMath.random(5, 15, Ease.IN_CUBIC.fun()), 0);
            for (Event action : filterPack) {
                level.createEvent(i, action);
            }
        }
        return level;
    }
}

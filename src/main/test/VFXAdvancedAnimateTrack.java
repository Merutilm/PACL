package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t2manimtile.VFXGeneratorAnimateTile;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t2manimtile.attribute.AttributeAnimation;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXAdvancedAnimateTrack implements VFX {
    @Override
    public void apply(CustomLevel level) {

        double duration = 16;
        int start = 2320;
        int end = level.getLength() - 1;


        VFXGeneratorAnimateTile animateTile = new VFXGeneratorAnimateTile(level);

        create(animateTile, start, end, duration);
    }

    public void create(VFXGeneratorAnimateTile animateTile, int start, int end, double duration) {
        animateTile.addAdvancedTileAnimation(start, end,
                new AttributeAnimation(
                        duration, duration * 0.8,
                        new Point2D(3, 1), 0, 140, Ease.OUT_CUBIC,
                        40, 2, 60, 10),
                90, 100, 90, 1, 100,
                new AttributeAnimation(
                        0d, duration,
                        new Point2D(-6, -3), 0, 10, Ease.IN_CUBIC,
                        40, 2, 60, 10)
                );
    }

    public void createWithoutAheadAnimation(VFXGeneratorAnimateTile animateTile, int start, int end, double duration) {
        animateTile.addAdvancedTileAnimation(start, end,
                null,
                96, 100, 20, 1, 100,
                new AttributeAnimation(
                        0d, duration,
                        new Point2D(36, -24), 0, 10, Ease.IN_CUBIC,
                        40, 14, 60, 10));
    }
}

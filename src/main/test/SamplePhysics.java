package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.DecorationFile;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.base.struct.PolarPoint;
import kr.tkdydwk7071.base.util.AdvancedMath;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t3physics.VFXGenRendererPhysics;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t3physics.modifiers.ModifierOpacity;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t3physics.variable.PhysicsObject;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t3physics.variable.PhysicsObjectState;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class SamplePhysics implements VFX {
    @Override
    public void apply(CustomLevel level) {
        level.setSettings(level.getSettings()
                .edit()
                .setZoom(300)
                .build()
        );
        VFXGenRendererPhysics renderer = new VFXGenRendererPhysics(level, 1);
        renderer.getMacro().attributeAsset
                .setFps(40)
                .setRenderDuration(20);


        for (int i = 1; i < 20; i++) {
            double angleOffset = i * 2.5;

            renderer.createObject(
                    new PhysicsObject(
                            new DecorationFile("nev_travel_s180.png"),
                            (short) 5,
                            100d,
                            angleOffset,
                            new PhysicsObjectState(
                                    new Point2D(-10, 4),
                                    new PolarPoint(AdvancedMath.random(12, 17), AdvancedMath.random(30,40)),
                                    1,
                                    2,
                                    new Point2D(10, 20)

                            )));
            //renderer.createTrigger(new TriggerForce(1, i, 10d, new PolarPoint(2, AdvancedMath.random(0, 360)), 0));
            renderer.createModifier(new ModifierOpacity(1, i, 1, 0d, Ease.IN_CUBIC.fun(), angleOffset + 180));
        }
        // 2 -> 3
        // 3 -> 5
        // 4 -> 7
        // 5 -> 10
        // 1 -> 11 (중력가속도)

        renderer.render();
    }
}

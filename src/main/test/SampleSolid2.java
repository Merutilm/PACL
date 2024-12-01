package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.Point3D;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.VFXGenRenderer3DDecoration;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierMoveSolid;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.variable.Polyhedron;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.variable.SolidState;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.variable.SolidType;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class SampleSolid2 implements VFX {
    @Override
    public void apply(CustomLevel level) {

        level.setSettings(level.getSettings()
                .edit()
                .setBpm(120)
                .build()
        );

        VFXGenRenderer3DDecoration renderer = new VFXGenRenderer3DDecoration(level, 1);

        renderer.getMacro()
                .attributeAsset
                .setRenderStartFloor(1)
                .setFps(60)
                .setSmoothTransition(false)
                .setRenderDuration(30);

        renderer.getMacro()
                .attributeLight
                .setPosition(new Point3D(0, 0, 1000))
                .setShowing(false);

        renderer.getMacro()
                .attributeCamera
                .setViewPosition(new Point3D(0, 0, -8));

        renderer.getMacro()
                .attributeEdge
                .setBrightnessMultiplier(1)
                .setThickness(600);

        Point3D mainSolidSize = new Point3D(600, 600, 600);

        renderer.createSolid(
                new Polyhedron(
                        SolidType.CUBE,
                        SolidState.get(
                                new Point3D(0, 0, 10),
                                mainSolidSize
                        ))
        );

        renderer.createModifier(new ModifierMoveSolid(1, 1, 240,
                SolidState.get(
                        new Point3D(0, 0, 0),
                        new Point3D(2000, 2000, 2000),
                        mainSolidSize),
                Ease.LINEAR.fun(), 0)
        );

        renderer.render();

    }
}

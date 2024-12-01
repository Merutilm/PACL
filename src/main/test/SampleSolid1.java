package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.Point3D;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.VFXGenRenderer3DDecoration;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierMoveSolid;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.variable.Polyhedron;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.variable.SolidState;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.variable.SolidType;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class SampleSolid1 implements VFX {
    @Override
    public void apply(CustomLevel level) {


        VFXGenRenderer3DDecoration renderer = new VFXGenRenderer3DDecoration(level, 1);

        renderer.getMacro()
                .attributeAsset
                .setRenderStartFloor(1)
                .setFps(60)
                .setSmoothTransition(false)
                .setRenderDuration(10);

        renderer.getMacro()
                .attributeLight
                .setPosition(new Point3D(0, 0, -1000))
                .setShowing(false);

        renderer.getMacro()
                .attributeCamera
                .setCameraPosition(Point3D.ORIGIN)
                .setViewPosition(new Point3D(0, 0, -25))
                .setCameraRotation(new Point3D(0, 20, 0));

        renderer.getMacro()
                .attributeEdge
                .setBrightnessMultiplier(1)
                .setThickness(600);

        Point3D ss1 = new Point3D(450, 450, 450);
        Point3D ss2 = new Point3D(170, 170, 170);
        Point3D ss3 = new Point3D(60, 60, 60);

        Point3D sp1 = new Point3D(0, 0, 10);
        Point3D sp2 = new Point3D(0,1,0);
        Point3D sp3 = new Point3D(0, 2, -5);

        renderer.createSolids(
                new Polyhedron(
                        SolidType.CUBE,
                        SolidState.get(sp1, ss1))//,
//                new Polyhedron(
//                        SolidType.CUBE,
//                        SolidState.get(sp2, ss2, HexColor.random())
//                ),
//                new Polyhedron(
//                        SolidType.CUBE,
//                        SolidState.get(sp3, ss3, HexColor.random())
//                )
        );


        renderer.createModifier(new ModifierMoveSolid(1,
                        1, 10,
                        SolidState.get(
                                sp1,
                                new Point3D(-90, 90, 90),
                                ss1
                        ),
                        Ease.LINEAR.fun(), 0
                )
        );

//        renderer.createModifier(new ModifierMoveSolid(1,
//                        2, 10,
//                        SolidState.get(
//                                sp2,
//                                new Point3D(360, 360, 20),
//                                ss2,
//                                HexColor.random()
//                        ),
//                        Ease.LINEAR.fun(), 0
//                )
//        );
//        renderer.createModifier(new ModifierMoveSolid(1,
//                        3, 10,
//                        SolidState.get(
//                                sp3,
//                                new Point3D(-160, 10, -210),
//                                ss3,
//                                HexColor.random()
//                        ),
//                        Ease.LINEAR.fun(), 0
//                )
//        );

//        renderer.createModifier(new ModifierCameraRotation(1,
//                        10,
//                        new Point3D(0, -20, 0),
//                        Ease.LINEAR.fun(), 0
//                )
//        );
        renderer.render();

    }
}

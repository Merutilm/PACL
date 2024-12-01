package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.DecorationFile;
import kr.tkdydwk7071.base.struct.HexColor;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.base.struct.Point3D;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.VFXGenRenderer3DDecoration;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierCameraRotation;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierParticleColor;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierParticlePosition;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.variable.DotState;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.variable.Particle;
import kr.tkdydwk7071.pacl.adofailevel.event.selectable.BlendMode;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXParticleGoes implements VFX {
    @Override
    public void apply(CustomLevel level) {

        VFXGenRenderer3DDecoration renderer = new VFXGenRenderer3DDecoration(level, 1);
        int floor = 196;
        double duration = 16;

        renderer.getMacro()
                .attributeAsset
                .setRenderStartFloor(floor)
                .setFps(15)
                .setRenderDuration(duration)
                .setTileZ(1500);

        renderer.getMacro().attributeParticle
                .setParticleBlendMode(BlendMode.LINEAR_DODGE)
                .setMinScale(10);

        renderer.getMacro()
                .attributeLight
                .setLightColor(HexColor.WHITE)
                .setSpreading(false)
                .setShowing(false);


        for (int i = 1; i < 68; i++) {
            create(renderer, floor, duration, i);
        }

        renderer.createModifier(new ModifierCameraRotation(floor, duration, new Point3D(0, 0, 250), Ease.INOUT_BACK.fun(), 0));
        renderer.render();
    }

    private void create(VFXGenRenderer3DDecoration renderer, int floor, double duration, int groupID) {
        double z = -500;
        Point2D start = Point2D.ORIGIN.getInCircleRandomPoint(35);
        int alpha = 255;
        double angleInterval = 45;


        renderer.createParticle(
                new Particle(
                        new DecorationFile("fog.png"),
                        new DotState(
                                new Point3D(start.x(), start.y(), z),
                                new Point3D(0, 0, 0),
                                new Point3D(0, 0, 0),
                                780,
                                HexColor.get(255, 255, 255, 0)
                        ),
                        2,
                        0.03,
                        0.97,
                        0.8
                )
        );

        double groupAmount = (int) (duration * 180 / angleInterval);

        double angleOffset = (groupID - groupAmount / 2) * angleInterval;

        renderer.createModifier(
                new ModifierParticleColor(floor, groupID, duration / 2,
                        HexColor.get(255, 255, 255, alpha),
                        Ease.OUT_CUBIC.fun(),
                        angleOffset
                )

        );
        renderer.createModifier(
                new ModifierParticlePosition(floor, groupID, duration,
                        new Point3D(0,0, -z),
                        Ease.LINEAR.fun(),
                        angleOffset
                )
        );
        renderer.createModifier(
                new ModifierParticleColor(floor, groupID, duration / 2,
                        HexColor.get(255, 255, 255, 0),
                        Ease.IN_CUBIC.fun(),
                        angleOffset + duration * 90
                )

        );

    }
}

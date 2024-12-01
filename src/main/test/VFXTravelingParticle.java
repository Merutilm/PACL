package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.DecorationFile;
import kr.tkdydwk7071.base.struct.HexColor;
import kr.tkdydwk7071.base.struct.Point3D;
import kr.tkdydwk7071.base.struct.Tag;
import kr.tkdydwk7071.base.util.AdvancedMath;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.VFXGenRenderer3DDecoration;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierParticleCenterRotatePosition;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierParticleColor;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierParticlePosition;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierParticleRotation;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.variable.DotState;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.variable.Particle;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.MoveDecorations;
import kr.tkdydwk7071.pacl.adofailevel.event.selectable.BlendMode;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXTravelingParticle implements VFX {
    @Override
    public void apply(CustomLevel level) {


        VFXGenRenderer3DDecoration renderer = new VFXGenRenderer3DDecoration(level, 3);
        int floor = 2320;
        double renderDuration = 208;
        double moveDuration = 100;
        double angleInterval = 480;

        renderer.getMacro()
                .attributeAsset
                .setRenderStartFloor(floor)
                .setFps(3)
                .setRenderDuration(renderDuration)
                .setZDepthMultiplier(0.1)
                .setTileZ(30);

        renderer.getMacro()
                .attributeLight
                .setSpreading(false)
                .setShowing(false);

        renderer.getMacro()
                .attributeParticle
                .setParticleBlendMode(BlendMode.LINEAR_DODGE);

        renderer.getMacro()
                .attributeCamera
                .setCameraPosition(new Point3D(45, 0, 0));


        for (int groupID = 1; groupID < renderDuration * 180 / angleInterval; groupID++) {
            create(renderer, floor, groupID, moveDuration, groupID * angleInterval - 1260);
        }

        renderer.render();

        level.createEvent(floor,  new MoveDecorations.Builder()
                .setTag(Tag.of(renderer.getMacro().tag3DParticleAll))
                .setOpacity(0d)
                .setColor(HexColor.TRANSPARENT)
                .setAngleOffset(renderDuration * 180)
                .build()
        );

    }

    private void create(VFXGenRenderer3DDecoration renderer, int floor, int groupID, double duration, double angleOffset) {


        double radius = 5;
        double bound = 90;
        double boundError = 15;
        double startRotationRange = 360;
        double moveRotationRange = 360;
        double zOffs = 12;
        double scale = 180;
        double scaleRange = 40;
        double delaySec = 0.06;
        HexColor color = HexColor.get(255,255,255,166);

        //--------------------------------------------


        double rotation = random(0, startRotationRange);
        double finalScale = random(scale, scaleRange);
        double startX = random(bound, boundError);
        double endX = random(-bound, boundError);

        renderer.createParticle(
                new Particle(
                        new DecorationFile("fog_combined.png"),
                        new DotState(
                                new Point3D(startX, 0, random(zOffs, radius)),
                                new Point3D(bound, 0, zOffs),
                                new Point3D(rotation, 0, 0),
                                finalScale,
                                HexColor.TRANSPARENT
                        ),
                        5,
                        delaySec * finalScale / scale * (bound * 2) / Math.abs(endX - startX),
                        0.9,
                        0.7)
        );

        renderer.createModifier(
                new ModifierParticleColor(
                        floor,
                        groupID,
                        0d,
                        color,
                        Ease.LINEAR.fun(),
                        angleOffset
                )
        );

        renderer.createModifier(
                new ModifierParticlePosition(
                        floor,
                        groupID,
                        duration,
                        new Point3D(endX, 0, random(zOffs, radius)),
                        Ease.IN_SINE.fun(),
                        angleOffset
                )
        );

        renderer.createModifier(
                new ModifierParticleCenterRotatePosition(
                        floor,
                        groupID,
                        duration,
                        new Point3D(-bound, 0, zOffs),
                        Ease.IN_SINE.fun(),
                        angleOffset
                )
        );
        renderer.createModifier(
                new ModifierParticleRotation(
                        floor,
                        groupID,
                        duration,
                        new Point3D(random(rotation, moveRotationRange), 0, 0),
                        Ease.INOUT_BACK.fun(),
                        angleOffset
                )
        );

    }


    private double random(double center, double radius) {
        return center + AdvancedMath.random(-radius, radius);
    }
}

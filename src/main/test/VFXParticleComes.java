package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.functions.FunctionEase;
import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.DecorationFile;
import kr.tkdydwk7071.base.struct.HexColor;
import kr.tkdydwk7071.base.struct.Point3D;
import kr.tkdydwk7071.base.struct.Tag;
import kr.tkdydwk7071.base.util.AdvancedMath;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.VFXGenRenderer3DDecoration;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierParticleCenterRotatePosition;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierParticlePosition;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierParticleScale;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.variable.DotState;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.variable.Particle;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.MoveDecorations;
import kr.tkdydwk7071.pacl.adofailevel.event.selectable.BlendMode;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFXParticleComes implements VFX {
    @Override
    public void apply(CustomLevel level) {

        int floor = 250;
        int renderGroup = 3;
        double duration = 2;
        double renderDuration = 4;
        double particleDuration = 1;
        double particlesPerBeats = 12;


        VFXGenRenderer3DDecoration renderer = new VFXGenRenderer3DDecoration(level, renderGroup);

        renderer.getMacro()
                .attributeAsset
                .setRenderStartFloor(floor)
                .setFps(15)
                .setRenderDuration(renderDuration)
                .setTileZ(30);

        renderer.getMacro()
                .attributeLight
                .setSpreading(false)
                .setShowing(false);

        renderer.getMacro()
                .attributeParticle
                .setParticleBlendMode(BlendMode.LINEAR_DODGE)
                .setMinScale(50);


        for (int i = 1; i / particlesPerBeats < particleDuration; i++) {

            create(renderer, floor, duration, i * 180 / particlesPerBeats, i, AdvancedMath.doubleRandom(360));
        }
        renderer.render();

        level.createEvent(floor, new MoveDecorations.Builder()
                .setAngleOffset(renderDuration * 180)
                .setDuration(0d)
                .setOpacity(0d)
                .setColor(HexColor.TRANSPARENT)
                .setTag(Tag.of(renderer.getMacro().tag3DParticleAll))
                .build()
        );
    }

    private void create(VFXGenRenderer3DDecoration renderer, int floor, double duration, double angleOffset, int groupID, double rotation) {
        double scale = 12000;
        renderer.createParticle(
                new Particle(
                        new DecorationFile("nev_fog.png"),
                        new DotState(
                                new Point3D(0, 0, 200),
                                new Point3D(0, 0, 200),
                                new Point3D(0, 0, rotation),
                                1,
                                HexColor.get(255, 255, 255, 65)
                        ),
                        10,
                        0.02,
                        0.9,
                        0.7)
        );
        FunctionEase in = Ease.LINEAR.fun();

        renderer.createModifier(new ModifierParticleScale(floor, groupID, 0, scale, Ease.LINEAR.fun(), angleOffset));
        renderer.createModifier(new ModifierParticleCenterRotatePosition(floor, groupID, duration,
                new Point3D(0, 0, 10),
                in, angleOffset));
        renderer.createModifier(new ModifierParticlePosition(floor, groupID, duration,
                new Point3D(0, 35, 10),
                in, angleOffset));
        renderer.createModifier(new ModifierParticleScale(floor, groupID, 0, 0.0, Ease.LINEAR.fun(), angleOffset + duration * 180));

    }
}

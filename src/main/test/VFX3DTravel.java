package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.DecorationFile;
import kr.tkdydwk7071.base.struct.HexColor;
import kr.tkdydwk7071.base.struct.Point3D;
import kr.tkdydwk7071.base.struct.Tag;
import kr.tkdydwk7071.base.util.AdvancedMath;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.VFXGenRenderer3DDecoration;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierCameraPosition;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierCameraRotation;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierParticleColor;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.modifiers.ModifierParticlePosition;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.variable.DotState;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t1m3ddeco.variable.Particle;
import kr.tkdydwk7071.pacl.adofailevel.event.events.action.MoveDecorations;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class VFX3DTravel implements VFX {
    @Override
    public void apply(CustomLevel level) {

        VFXGenRenderer3DDecoration renderer = new VFXGenRenderer3DDecoration(level, 4);
        int floor = 2637;
        double renderDuration = 63.9;
        double travelSpeed = 200;

        double starScale = 200;
        Point3D starRange = new Point3D(30, 30, 220);
        double starDensity = 0.01;


        double lightScale = 220;
        Point3D lightRange = new Point3D(20, 20, 220);
        double lightDensity = 0.0001;

        double fogScale = 750;
        Point3D fogRange = new Point3D(30, 30, 220);
        double fogDensity = 0.001;

        double delaySec = 0.06;

        //------------------------------------------------------------

        double volume = starRange.x() * starRange.y() * starRange.z();

        int starAmount = (int) (volume * starDensity);
        int lightAmount = (int) (volume * lightDensity);
        int fogAmount = (int) (volume * fogDensity);

        renderer.getMacro().attributeLight
                .setLightColor(HexColor.get(255, 255, 255, 150))
                .setScale(300)
                .setLockDepth(true)
                .setLockDepthValue((short) -40)
                .setLightImage(new DecorationFile("4_lensflare5.png"))
                .setSpreading(false);

        renderer.getMacro().attributeAsset
                .setRenderDuration(renderDuration)
                .setRenderStartFloor(floor)
                .setFps(12)
                .setSmoothTransition(true)
                .setTileZ(30);

        renderer.getMacro().attributeCamera
                .setCameraPosition(new Point3D(0, 0, 0))
                .setCameraRotation(new Point3D(55, 50, 0));

        renderer.getMacro().attributeParticle
                .setMinScale(50);


        renderer.createModifier(new ModifierCameraRotation(floor, 16, new Point3D(10, 10, 30), Ease.OUT_SINE.fun(), 0));
        renderer.createModifier(new ModifierCameraRotation(floor, 24, new Point3D(-10, -30, 0), Ease.INOUT_CUBIC.fun(), 2880));
        renderer.createModifier(new ModifierCameraRotation(floor, 8, new Point3D(0, 10, -20), Ease.IN_CUBIC.fun(), 8640));
        renderer.createModifier(new ModifierCameraRotation(floor, 8, new Point3D(0, -190, -90), Ease.OUT_QUART.fun(), 10080));
        renderer.createModifier(new ModifierCameraPosition(floor, 2, new Point3D(0, 0, 10), Ease.OUT_CUBIC.fun(), 10080));
        renderer.createModifier(new ModifierCameraPosition(floor, 6, new Point3D(0, 0, -85), Ease.IN_CUBIC.fun(), 10440));


        for (int groupID = 1; groupID <= starAmount; groupID++) {
            double x = AdvancedMath.random(-starRange.x() / 2, starRange.x() / 2);
            double y = AdvancedMath.random(-starRange.y() / 2, starRange.y() / 2);
            double z = AdvancedMath.random(0, starRange.z());


            renderer.createParticle(
                    new Particle(
                            new DecorationFile("nev_dot.png"),
                            DotState.get(
                                    new Point3D(x, y, z),
                                    new Point3D(0, 0, 0),
                                    starScale
                            ),
                            1, 0, 1, 1)
            );

            renderer.createModifier(
                    new ModifierParticlePosition(
                            floor, groupID, renderDuration,
                            new Point3D(x, y, z - travelSpeed),
                            Ease.LINEAR.fun(), 0
                    )
            );
        }

        for (int groupID = starAmount + 1; groupID - starAmount <= lightAmount; groupID++) {
            double x = AdvancedMath.random(-lightRange.x() / 2, lightRange.x() / 2);
            double y = AdvancedMath.random(-lightRange.y() / 2, lightRange.y() / 2);
            double z = AdvancedMath.random(0, lightRange.z());

            double movePerBeats = travelSpeed / renderDuration;
            double beatsOnHundred = (z - 100) / movePerBeats;
            double beatsOnHundredToZero = 100 / movePerBeats;

            renderer.createParticle(
                    new Particle(
                            new DecorationFile("fog_combined.png"),
                            new DotState(
                                    new Point3D(x, y, z),
                                    new Point3D(x, y, z),
                                    new Point3D(0, 0, 0),
                                    lightScale,
                                    HexColor.BLACK
                            ),
                            5,
                            delaySec,
                            0.9,
                            0.7)
            );

            renderer.createModifier(
                    new ModifierParticlePosition(
                            floor, groupID, renderDuration,
                            new Point3D(x, y, z - travelSpeed),
                            Ease.LINEAR.fun(), 0
                    )
            );

            renderer.createModifier(
                    new ModifierParticleColor(
                            floor, groupID, beatsOnHundredToZero,
                            HexColor.WHITE,
                            Ease.LINEAR.fun(), beatsOnHundred * 180
                    )
            );
        }

        for (int groupID = starAmount + lightAmount + 1; groupID - starAmount - lightAmount <= fogAmount; groupID++) {
            double x = AdvancedMath.random(-fogRange.x() / 2, fogRange.x() / 2);
            double y = AdvancedMath.random(-fogRange.y() / 2, fogRange.y() / 2);
            double z = AdvancedMath.random(0, fogRange.z());
            int randomHex = (int) (y > 0 ? AdvancedMath.random(16, 48) : AdvancedMath.random(176, 240));
            double opacity = Math.abs(y * 5);

            renderer.createParticle(
                    new Particle(
                            new DecorationFile("fog.png"),
                            new DotState(
                                    new Point3D(x, y, z),
                                    new Point3D(x, y, z),
                                    new Point3D(0, 0, 0),
                                    fogScale,
                                    HexColor.get(
                                            randomHex + AdvancedMath.intRandom(16),
                                            randomHex,
                                            randomHex + AdvancedMath.intRandom(16),
                                            HexColor.convertOpacityToHex(opacity)
                                    )
                            ),
                            1, 0, 1, 1)
            );

            renderer.createModifier(
                    new ModifierParticlePosition(
                            floor, groupID, renderDuration,
                            new Point3D(x, y, z - travelSpeed),
                            Ease.LINEAR.fun(), 0
                    )
            );

        }

        renderer.render();
        level.createEvent(2721, new MoveDecorations.Builder()
                .setTag(Tag.of(renderer.getMacro().tag3DLight))
                .setOpacity(0d)
                .setColor(HexColor.TRANSPARENT)
                .setDuration(0d)
                .build());

        level.createEvent(level.getLength(), new MoveDecorations.Builder()
                .setTag(Tag.of(renderer.getMacro().tag3DParticleAll))
                .setOpacity(0d)
                .setColor(HexColor.TRANSPARENT)
                .setDuration(0d)
                .build());

    }

}

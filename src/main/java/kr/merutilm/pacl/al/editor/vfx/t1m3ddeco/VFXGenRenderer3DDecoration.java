package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.Point3D;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.pacl.al.editor.fx.FxUtils;
import kr.merutilm.pacl.al.editor.vfx.VFXGenRenderer;
import kr.merutilm.pacl.al.editor.vfx.initmodifier.Modifier;
import kr.merutilm.pacl.al.editor.vfx.initmodifier.ModifierCollection;
import kr.merutilm.pacl.al.editor.vfx.initmodifier.ModifierCreator;
import kr.merutilm.pacl.al.editor.vfx.initmodifier.StrictModifier;
import kr.merutilm.pacl.al.editor.vfx.inittrigger.TriggerCreator;
import kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.attribute.AttributeAsset;
import kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.modifiers.*;
import kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable.DotState;
import kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable.Particle;
import kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable.Solid;
import kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable.SolidState;
import kr.merutilm.pacl.data.CustomLevel;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 3D 개체 렌더링 클래스
 */
public final class VFXGenRenderer3DDecoration extends VFXGenRenderer<VFXGenerator3DDecoration> {

    public VFXGenRenderer3DDecoration(CustomLevel level, int renderGroupID) {
        macro = new VFXGenerator3DDecoration(level, renderGroupID);
    }

    private static final String SOLID_NOT_GENERATED_MESSAGE = "번째 Solid 개체는 아직 생성되지 않았습니다!";
    private static final String PARTICLE_NOT_GENERATED_MESSAGE = "번째 Particle 개체는 아직 생성되지 않았습니다!";
    private final VFXGenerator3DDecoration macro;
    private final ModifierCreator modifierCreator = new ModifierCreator(c -> {
        c.addCreator(new ModifierCollection<>(ModifierMoveLight.class) {
            @Override
            public void create(@Nonnull ModifierCreator creator, @Nonnull ModifierMoveLight modifier) {
                int address = modifier.groupID() - 1;
                if (modifierSet.size() <= address) {
                    modifierSet.add(new HashSet<>());
                }

                modifierSet.get(address).add(modifier);
            }
        });

        c.addCreator(new ModifierCollection<>(ModifierMoveSolid.class) {
            @Override
            public void create(@Nonnull ModifierCreator creator, @Nonnull ModifierMoveSolid modifier) {
                int address = modifier.groupID() - 1;
                if (modifierSet.size() <= address) {
                    throw new NullPointerException(modifier.groupID() + SOLID_NOT_GENERATED_MESSAGE);
                }
                modifierSet.get(address).add(modifier);
            }
        });
        c.addCreator(new ModifierCollection<>(ModifierParticleImage.class) {
            @Override
            public void create(@Nonnull ModifierCreator creator, @Nonnull ModifierParticleImage modifier) {
                int address = modifier.groupID() - 1;
                if (modifierSet.size() <= address) {
                    throw new NullPointerException(modifier.groupID() + PARTICLE_NOT_GENERATED_MESSAGE);
                }
                modifierSet.get(address).add(modifier);
            }
        });
        c.addCreator(new ModifierCollection<>(ModifierParticlePosition.class) {
            @Override
            public void create(@Nonnull ModifierCreator creator, @Nonnull ModifierParticlePosition modifier) {
                int address = modifier.groupID() - 1;
                if (modifierSet.size() <= address) {
                    throw new NullPointerException(modifier.groupID() + PARTICLE_NOT_GENERATED_MESSAGE);
                }
                modifierSet.get(address).add(modifier);
            }
        });
        c.addCreator(new ModifierCollection<>(ModifierParticleRotation.class) {
            @Override
            public void create(@Nonnull ModifierCreator creator, @Nonnull ModifierParticleRotation modifier) {
                int address = modifier.groupID() - 1;
                if (modifierSet.size() <= address) {
                    throw new NullPointerException(modifier.groupID() + PARTICLE_NOT_GENERATED_MESSAGE);
                }
                modifierSet.get(address).add(modifier);
            }
        });
        c.addCreator(new ModifierCollection<>(ModifierParticleCenterRotatePosition.class) {
            @Override
            public void create(@Nonnull ModifierCreator creator, @Nonnull ModifierParticleCenterRotatePosition modifier) {
                int address = modifier.groupID() - 1;
                if (modifierSet.size() <= address) {
                    throw new NullPointerException(modifier.groupID() + PARTICLE_NOT_GENERATED_MESSAGE);
                }
                modifierSet.get(address).add(modifier);
            }
        });
        c.addCreator(new ModifierCollection<>(ModifierParticleScale.class) {
            @Override
            public void create(@Nonnull ModifierCreator creator, @Nonnull ModifierParticleScale modifier) {
                int address = modifier.groupID() - 1;
                if (modifierSet.size() <= address) {
                    throw new NullPointerException(modifier.groupID() + PARTICLE_NOT_GENERATED_MESSAGE);
                }
                modifierSet.get(address).add(modifier);
            }
        });

        c.addCreator(new ModifierCollection<>(ModifierParticleColor.class) {
            @Override
            public void create(@Nonnull ModifierCreator creator, @Nonnull ModifierParticleColor modifier) {
                int address = modifier.groupID() - 1;
                if (modifierSet.size() <= address) {
                    throw new NullPointerException(modifier.groupID() + PARTICLE_NOT_GENERATED_MESSAGE);
                }
                modifierSet.get(address).add(modifier);
            }
        });

        c.addCreator(new ModifierCollection<>(ModifierCameraPosition.class) {
            @Override
            public void create(@Nonnull ModifierCreator creator, @Nonnull ModifierCameraPosition modifier) {
                int address = modifier.groupID() - 1;
                if (modifierSet.size() <= address) {
                    modifierSet.add(new HashSet<>());
                }
                modifierSet.get(address).add(modifier);
            }
        });
        c.addCreator(new ModifierCollection<>(ModifierCameraRotation.class) {
            @Override
            public void create(@Nonnull ModifierCreator creator, @Nonnull ModifierCameraRotation modifier) {
                int address = modifier.groupID() - 1;
                if (modifierSet.size() <= address) {
                    modifierSet.add(new HashSet<>());
                }
                modifierSet.get(address).add(modifier);
            }
        });
        c.addCreator(new ModifierCollection<>(ModifierViewPosition.class) {
            @Override
            public void create(@Nonnull ModifierCreator creator, @Nonnull ModifierViewPosition modifier) {
                int address = modifier.groupID() - 1;
                if (modifierSet.size() <= address) {
                    modifierSet.add(new HashSet<>());
                }
                modifierSet.get(address).add(modifier);
            }
        });

        c.addCreator(new ModifierCollection<>(ModifierViewRotation.class) {
            @Override
            public void create(@Nonnull ModifierCreator creator, @Nonnull ModifierViewRotation modifier) {
                int address = modifier.groupID() - 1;
                if (modifierSet.size() <= address) {
                    modifierSet.add(new HashSet<>());
                }
                modifierSet.get(address).add(modifier);
            }
        });

    });

    private final TriggerCreator triggerCreator = new TriggerCreator(c -> {
        //none.
    });

    @Override
    public VFXGenerator3DDecoration getMacro() {
        return macro;
    }

    @Override
    public ModifierCreator getModifierCreator() {
        return modifierCreator;
    }

    @Override
    public TriggerCreator getTriggerCreator() {
        return triggerCreator;
    }


    /**
     * 3D 오브젝트를 만듭니다.
     * 주의 : 크기가 매우 큰 오브젝트에 크기가 매우 작은 오브젝트를 붙여 놓지 마세요.
     * 만약 불가피하다면, 크기가 작은 오브젝트 여러 개를 놓으세요.
     */
    public void createSolid(Solid solid) {

        macro.solids.add(solid);
        macro.solidPlaneVertices.add(new ArrayList<>());

        modifierCreator.getModifierSet(ModifierMoveSolid.class).add(new HashSet<>());

    }

    /**
     * 3D 오브젝트 여러 개를 만듭니다.
     */
    public void createSolids(Solid... solids) {
        for (Solid solid : solids) {
            createSolid(solid);
        }
    }

    public void createParticle(Particle particle) {
        macro.particles.add(particle);

        modifierCreator.getModifierSet(ModifierParticleImage.class).add(new HashSet<>());
        modifierCreator.getModifierSet(ModifierParticlePosition.class).add(new HashSet<>());
        modifierCreator.getModifierSet(ModifierParticleCenterRotatePosition.class).add(new HashSet<>());
        modifierCreator.getModifierSet(ModifierParticleRotation.class).add(new HashSet<>());
        modifierCreator.getModifierSet(ModifierParticleScale.class).add(new HashSet<>());
        modifierCreator.getModifierSet(ModifierParticleColor.class).add(new HashSet<>());
    }

    protected void invokeRender() {
        
        AttributeAsset curAttributeAsset = macro.attributeAsset.build();


        final int firstFloor = curAttributeAsset.renderStartFloor();

        final List<List<StrictModifierCameraPosition>> strictModifierPositionCameraList =
                StrictModifier.of(
                        macro.level,
                        firstFloor,
                        modifierCreator.getModifierSet(ModifierCameraPosition.class),
                        i -> macro.attributeCamera.build().cameraPosition(),
                        StrictModifierCameraPosition::new
                );
        final List<List<StrictModifierCameraRotation>> strictModifierRotationCameraList =
                StrictModifier.of(
                        macro.level,
                        firstFloor,
                        modifierCreator.getModifierSet(ModifierCameraRotation.class),
                        i -> macro.attributeCamera.build().cameraRotation(),
                        StrictModifierCameraRotation::new
                );

        final List<List<StrictModifierViewPosition>> strictModifierPositionViewList =
                StrictModifier.of(
                        macro.level,
                        firstFloor,
                        modifierCreator.getModifierSet(ModifierViewPosition.class),
                        i -> macro.attributeCamera.build().viewPosition(),
                        StrictModifierViewPosition::new
                );
        final List<List<StrictModifierViewRotation>> strictModifierRotationViewList =
                StrictModifier.of(
                        macro.level,
                        firstFloor,
                        modifierCreator.getModifierSet(ModifierViewRotation.class),
                        i -> macro.attributeCamera.build().viewRotation(),
                        StrictModifierViewRotation::new
                );

        final List<List<StrictModifierMoveLight>> strictModifierMoveLightList =
                StrictModifier.of(
                        macro.level,
                        firstFloor,
                        modifierCreator.getModifierSet(ModifierMoveLight.class),
                        i -> macro.attributeLight.build().position(),
                        StrictModifierMoveLight::new
                );

        final List<List<StrictModifierMoveSolid>> strictModifierMoveSolidList =
                StrictModifier.of(
                        macro.level,
                        firstFloor,
                        modifierCreator.getModifierSet(ModifierMoveSolid.class),
                        i -> macro.solids.get(i).state(),
                        StrictModifierMoveSolid::new
                );

        final List<List<StrictModifierParticleImage>> strictModifierParticleImageList =
                StrictModifier.of(
                        macro.level,
                        firstFloor,
                        modifierCreator.getModifierSet(ModifierParticleImage.class),
                        i -> macro.particles.get(i).image(),
                        StrictModifierParticleImage::new
                );

        final List<List<StrictModifierParticlePosition>> strictModifierParticlePositionList =
                StrictModifier.of(
                        macro.level,
                        firstFloor,
                        modifierCreator.getModifierSet(ModifierParticlePosition.class),
                        i -> macro.particles.get(i).state().position(),
                        StrictModifierParticlePosition::new
                );
        final List<List<StrictModifierParticleCenterRotatePosition>> strictModifierParticleCenterRotatePositionList =
                StrictModifier.of(
                        macro.level,
                        firstFloor,
                        modifierCreator.getModifierSet(ModifierParticleCenterRotatePosition.class),
                        i -> macro.particles.get(i).state().centerRotatePosition(),
                        StrictModifierParticleCenterRotatePosition::new
                );
        final List<List<StrictModifierParticleRotation>> strictModifierParticleRotationList =
                StrictModifier.of(
                        macro.level,
                        firstFloor,
                        modifierCreator.getModifierSet(ModifierParticleRotation.class),
                        i -> macro.particles.get(i).state().rotation(),
                        StrictModifierParticleRotation::new
                );
        final List<List<StrictModifierParticleScale>> strictModifierParticleScaleList =
                StrictModifier.of(
                        macro.level,
                        firstFloor,
                        modifierCreator.getModifierSet(ModifierParticleScale.class),
                        i -> macro.particles.get(i).state().scale(),
                        StrictModifierParticleScale::new
                );
        final List<List<StrictModifierParticleColor>> strictModifierParticleColorList =
                StrictModifier.of(
                        macro.level,
                        firstFloor,
                        modifierCreator.getModifierSet(ModifierParticleColor.class),
                        i -> macro.particles.get(i).state().color(),
                        StrictModifierParticleColor::new
                );

        FxUtils.fxFrame(macro.level, (floor, attempts, changeDuration, curTimeSec, angleOffset) -> {

            final double finalChangeDuration = macro.attributeAsset.build().smoothTransition() ? changeDuration : 0;

            macro.refresh3DLight(floor, finalChangeDuration, angleOffset);

            Modifier.fxFrameRender(strictModifierMoveLightList, curTimeSec,
                    (groupID, ratio, startState, endState) -> {
                        Point3D lightFrame = Point3D.ratioDivide(startState, endState, ratio);
                        macro.attributeLight.setPosition(lightFrame);
                    });

            Modifier.fxFrameRender(strictModifierPositionCameraList, curTimeSec, (groupID, ratio, startState, endState) -> macro.attributeCamera.setCameraPosition(Point3D.ratioDivide(startState, endState, ratio)));
            Modifier.fxFrameRender(strictModifierRotationCameraList, curTimeSec, (groupID, ratio, startState, endState) -> macro.attributeCamera.setCameraRotation(Point3D.ratioDivide(startState, endState, ratio)));
            Modifier.fxFrameRender(strictModifierPositionViewList, curTimeSec, (groupID, ratio, startState, endState) -> macro.attributeCamera.setViewPosition(Point3D.ratioDivide(startState, endState, ratio)));
            Modifier.fxFrameRender(strictModifierRotationViewList, curTimeSec, (groupID, ratio, startState, endState) -> macro.attributeCamera.setViewRotation(Point3D.ratioDivide(startState, endState, ratio)));

            Modifier.fxFrameRender(strictModifierMoveSolidList, curTimeSec,
                    (groupID, ratio, startState, endState) -> {
                        SolidState solidState = startState.edit()
                                .setCenterPosition(Point3D.ratioDivide(startState.centerPosition(), endState.centerPosition(), ratio))
                                .setCenterRotatePosition(Point3D.ratioDivide(startState.centerRotatePosition(), endState.centerRotatePosition(), ratio))
                                .setRotation(Point3D.ratioDivide(startState.rotation(), endState.rotation(), ratio))
                                .setScale(Point3D.ratioDivide(startState.scale(), endState.scale(), ratio))
                                .setColor(HexColor.ratioDivide(startState.color(), endState.color(), ratio))
                                .build();
                        Solid solid = macro.getSolid(groupID)
                                .edit()
                                .setSolidState(solidState)
                                .build();

                        macro.refresh3DVertices(floor, groupID, finalChangeDuration, solid, angleOffset);
                    });
            Modifier.fxFrameRender(strictModifierParticleImageList, curTimeSec,
                    (groupID, ratio, startState, endState) -> {
                        macro.setParticle(groupID, macro.getParticle(groupID)
                                .edit()
                                .setImage(endState)
                                .build());
                    });
            Modifier.fxFrameRender(strictModifierParticlePositionList, curTimeSec,
                    (groupID, ratio, startState, endState) -> {
                        DotState state = macro.getParticle(groupID).state();
                        macro.setParticle(groupID, macro.getParticle(groupID)
                                .edit()
                                .setState(state.edit().setCenterPosition(Point3D.ratioDivide(startState, endState, ratio)).build())
                                .build());
                    });
            Modifier.fxFrameRender(strictModifierParticleCenterRotatePositionList, curTimeSec,
                    (groupID, ratio, startState, endState) -> {
                        DotState state = macro.getParticle(groupID).state();
                        macro.setParticle(groupID, macro.getParticle(groupID)
                                .edit()
                                .setState(state.edit().setCenterRotatePosition(Point3D.ratioDivide(startState, endState, ratio)).build())
                                .build());
                    });
            Modifier.fxFrameRender(strictModifierParticleRotationList, curTimeSec,
                    (groupID, ratio, startState, endState) -> {
                        DotState state = macro.getParticle(groupID).state();
                        macro.setParticle(groupID, macro.getParticle(groupID)
                                .edit()
                                .setState(state.edit().setRotation(Point3D.ratioDivide(startState, endState, ratio)).build())
                                .build());
                    });
            Modifier.fxFrameRender(strictModifierParticleScaleList, curTimeSec,
                    (groupID, ratio, startState, endState) -> {
                        DotState state = macro.getParticle(groupID).state();
                        macro.setParticle(groupID, macro.getParticle(groupID)
                                .edit()
                                .setState(state.edit().setScale(AdvancedMath.ratioDivide(startState, endState, ratio)).build())
                                .build());
                    });

            Modifier.fxFrameRender(strictModifierParticleColorList, curTimeSec,
                    (groupID, ratio, startState, endState) -> {
                        DotState state = macro.getParticle(groupID).state();
                        macro.setParticle(groupID, macro.getParticle(groupID)
                                .edit()
                                .setState(state.edit().setColor(HexColor.ratioDivide(startState, endState, ratio)).build())
                                .build());
                    });

            macro.refresh3DParticle(floor, finalChangeDuration, angleOffset);

        }, firstFloor, curAttributeAsset.renderDuration(), curAttributeAsset.fps());
    }
}

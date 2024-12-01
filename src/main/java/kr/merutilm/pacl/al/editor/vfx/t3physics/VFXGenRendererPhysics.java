package kr.merutilm.pacl.al.editor.vfx.t3physics;

import kr.merutilm.base.struct.PolarPoint;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.pacl.al.editor.fx.FxUtils;
import kr.merutilm.pacl.al.editor.vfx.VFXGenRenderer;
import kr.merutilm.pacl.al.editor.vfx.initmodifier.Modifier;
import kr.merutilm.pacl.al.editor.vfx.initmodifier.ModifierCollection;
import kr.merutilm.pacl.al.editor.vfx.initmodifier.ModifierCreator;
import kr.merutilm.pacl.al.editor.vfx.initmodifier.StrictModifier;
import kr.merutilm.pacl.al.editor.vfx.inittrigger.StrictTrigger;
import kr.merutilm.pacl.al.editor.vfx.inittrigger.Trigger;
import kr.merutilm.pacl.al.editor.vfx.inittrigger.TriggerCollection;
import kr.merutilm.pacl.al.editor.vfx.inittrigger.TriggerCreator;
import kr.merutilm.pacl.al.editor.vfx.t3physics.attribute.AttributeAsset;
import kr.merutilm.pacl.al.editor.vfx.t3physics.attribute.AttributeWorld;
import kr.merutilm.pacl.al.editor.vfx.t3physics.modifiers.ModifierOpacity;
import kr.merutilm.pacl.al.editor.vfx.t3physics.modifiers.StrictModifierOpacity;
import kr.merutilm.pacl.al.editor.vfx.t3physics.triggers.StrictTriggerForce;
import kr.merutilm.pacl.al.editor.vfx.t3physics.triggers.TriggerForce;
import kr.merutilm.pacl.al.editor.vfx.t3physics.variable.PhysicsObject;
import kr.merutilm.pacl.data.CustomLevel;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;

public final class VFXGenRendererPhysics extends VFXGenRenderer<VFXGeneratorPhysics> {

    public VFXGenRendererPhysics(CustomLevel level, int renderGroupID) {
        macro = new VFXGeneratorPhysics(level, renderGroupID);
    }

    private final VFXGeneratorPhysics macro;

    private final ModifierCreator modifierCreator = new ModifierCreator(c -> {
        c.addCreator(new ModifierCollection<>(ModifierOpacity.class) {
            @Override
            public void create(@Nonnull ModifierCreator creator, @Nonnull ModifierOpacity modifier) {
                int address = modifier.groupID() - 1;
                if (modifierSet.size() <= address) {
                    modifierSet.add(new HashSet<>());
                }
                modifierSet.get(address).add(modifier);
            }
        });
    });

    private final TriggerCreator triggerCreator = new TriggerCreator(c -> {
        c.addCreator(new TriggerCollection<>(TriggerForce.class) {
            @Override
            public void create(@Nonnull TriggerCreator creator, @Nonnull TriggerForce trigger) {
                int address = trigger.groupID() - 1;
                if (triggerSet.size() <= address) {
                    triggerSet.add(new HashSet<>());
                }
                triggerSet.get(address).add(trigger);
            }
        });
    });

    @Override
    public VFXGeneratorPhysics getMacro() {
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

    public void createObject(PhysicsObject physicsObject) {
        macro.physicsObjects.add(physicsObject);
        triggerCreator.getTriggerSet(TriggerForce.class).add(new HashSet<>());
    }

    @Override
    protected void invokeRender() {
        AttributeWorld curAttributeWorld = macro.attributeWorld.build();
        AttributeAsset curAttributeAsset = macro.attributeAsset.build();
        int firstFloor = curAttributeAsset.renderStartFloor();
        double g = curAttributeWorld.gravity();

        for (int i = 1; i <= macro.physicsObjects.size(); i++) {
            createTrigger(new TriggerForce(firstFloor, i, curAttributeAsset.renderDuration(), new PolarPoint(g, -90), 0));
        }//중력 가속도 트리거 추가

        List<List<StrictTrigger<PolarPoint>>> strictTriggerForceList =
                StrictTrigger.of(
                        macro.level,
                        firstFloor,
                        triggerCreator.getTriggerSet(TriggerForce.class),
                        StrictTriggerForce::new
                );
        List<List<StrictModifier<Double>>> strictModifierOpacityList =
                StrictModifier.of(
                        macro.level,
                        firstFloor,
                        modifierCreator.getModifierSet(ModifierOpacity.class),
                        e -> 100d,
                        StrictModifierOpacity::new
                );

        FxUtils.fxFrame(macro.level, (floor, attempts, changeDuration, curTimeSec, angleOffset) -> {
            Trigger.fxFrameRender(strictTriggerForceList,
                    (groupID, currentGroupTriggers) -> macro.refreshPhysicsObject(floor, groupID, currentGroupTriggers, curTimeSec, changeDuration, angleOffset)
            );
            Modifier.fxFrameRender(strictModifierOpacityList,
                    curTimeSec, (groupID, ratio, startState, endState) -> {
                        PhysicsObject physicsObject = macro.getPhysicsObject(groupID);
                        macro.physicsObjects.set(groupID - 1,
                                physicsObject.edit()
                                        .setOpacity(AdvancedMath.ratioDivide(startState, endState, ratio))
                                        .build()
                        );
                    });
        }, curAttributeAsset.renderStartFloor(), curAttributeAsset.renderDuration(), curAttributeAsset.fps());

    }
}

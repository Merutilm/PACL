package kr.merutilm.pacl.al.editor.vfx.t3physics;

import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.struct.PolarPoint;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.pacl.al.editor.vfx.VFXGenerator;
import kr.merutilm.pacl.al.editor.vfx.inittrigger.StrictTrigger;
import kr.merutilm.pacl.al.editor.vfx.t3physics.attribute.AttributeAsset;
import kr.merutilm.pacl.al.editor.vfx.t3physics.attribute.AttributeWorld;
import kr.merutilm.pacl.al.editor.vfx.t3physics.variable.PhysicsObject;
import kr.merutilm.pacl.al.editor.vfx.t3physics.variable.PhysicsObjectState;
import kr.merutilm.pacl.al.event.events.action.MoveDecorations;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;
import kr.merutilm.pacl.al.event.struct.Tag;
import kr.merutilm.pacl.data.Assets;
import kr.merutilm.pacl.data.CustomLevel;

import java.util.*;

public final class VFXGeneratorPhysics implements VFXGenerator {
    final CustomLevel level;

    /**
     * 해당 매크로로 생성된 모든 장식은 이 태그를 참조합니다.
     */
    public final String tagPhysicsAll;

    /**
     * 장식 ID
     */
    public final String tagPhysicsID;
    public final AttributeWorld.Builder attributeWorld = new AttributeWorld.Builder(
            9.80665
    );
    public final AttributeAsset.Builder attributeAsset = new AttributeAsset.Builder(
            99.99,
            10,
            1,
            10,
            true
    );
    final List<PhysicsObject> physicsObjects = new ArrayList<>();

    private final Set<String> addDecorationTagSet = new HashSet<>();
    private final Map<String, MoveDecorations> lastCreatedVisibleMoveDecorationTagMap = new HashMap<>();
    private final Set<String> invisibleDecorationTagSet = new HashSet<>();

    VFXGeneratorPhysics(CustomLevel level, int renderGroupID) {
        this.level = level;
        tagPhysicsAll = "nevMPS" + renderGroupID;
        tagPhysicsID = "nevMPS-PI" + renderGroupID + ".";
    }

    public PhysicsObject getPhysicsObject(int groupID) {
        return physicsObjects.get(groupID - 1);
    }

    public void setPhysicsObject(int groupID, PhysicsObject o) {
        physicsObjects.set(groupID - 1, o);
    }

    void refreshPhysicsObject(int floor, int groupID, List<StrictTrigger<PolarPoint>> currentGroupTriggers, double curTimeSec, double changeDuration, double angleOffset) {
        PhysicsObject currentObject = getPhysicsObject(groupID);
        AttributeAsset curAttributeAsset = attributeAsset.build();
        double parallax = curAttributeAsset.defaultParallax();


        String tag = tagPhysicsID + groupID;

        if (!addDecorationTagSet.contains(tag)) {

            Assets.copyAsset(currentObject.image());

            level.createEvent(floor, new AddDecoration.Builder()
                    .setDecorationImage(currentObject.image())
                    .setOpacity(0d)
                    .setScale(new Point2D(0, 0))
                    .setDepth(currentObject.depth())
                    .setRotation(AdvancedMath.random(0, 360)) //TODO : ROTATION
                    .setParallax(new Point2D(parallax, parallax))
                    .setTag(Tag.of(tagPhysicsAll, tag))
                    .build()
            );

            addDecorationTagSet.add(tag);
        }

        double appearingAngle = Math.max(currentObject.appearingAngle(), 0);
        double opacity = currentObject.opacity();
        Point2D scale = currentObject.state().scale();
        double parallaxMultiplier = 100 / (100 - parallax);
        double renderEndSec = level.getTimeSecByDuration(curAttributeAsset.renderStartFloor(), curAttributeAsset.renderDuration());
        double appearingSec = level.getTimeSecByAngleOffset(curAttributeAsset.renderStartFloor(), appearingAngle);

        List<PolarPoint> currentForces = new ArrayList<>();

        for (StrictTrigger<PolarPoint> currentGroupTrigger : currentGroupTriggers) {
            if (currentGroupTrigger.endSec() > curTimeSec) {
                currentForces = currentGroupTrigger.currentValue();
                break;
            }
        }

        PhysicsObjectState state = nextState(currentObject.state(), currentForces, changeDuration);
        setPhysicsObject(groupID, currentObject.edit().setState(state).build());


        Point2D positionOffset = state.coordinate().multiply(parallaxMultiplier);
        // 최종 위치 오프셋

        MoveDecorations lastAdded = lastCreatedVisibleMoveDecorationTagMap.get(tag);
        if (opacity != 0 && appearingSec < curTimeSec && curTimeSec < renderEndSec && (lastAdded == null || lastAdded.opacity() != opacity || !Objects.equals(lastAdded.positionOffset(), positionOffset) || !Objects.equals(lastAdded.scale(), scale))) {
            MoveDecorations event = new MoveDecorations.Builder()
                    .setTag(Tag.of(tag))
                    .setPositionOffset(positionOffset)
                    .setDuration(curAttributeAsset.smoothTransition() && lastAdded != null ? changeDuration : 0.0)
                    .setOpacity(opacity)
                    .setScale(scale)
                    .setAngleOffset(lastAdded == null ? angleOffset + changeDuration * 180 : angleOffset)
                    .build();
            level.createEvent(floor, event);
            lastCreatedVisibleMoveDecorationTagMap.put(tag, event);
            invisibleDecorationTagSet.remove(tag);
        } else if (!invisibleDecorationTagSet.contains(tag) && lastAdded != null && lastAdded.opacity() != 0) {
            level.createEvent(floor, new MoveDecorations.Builder()
                    .setTag(Tag.of(tag))
                    .setPositionOffset(new Point2D(0, 0))
                    .setDuration(0d)
                    .setOpacity(0d)
                    .setScale(new Point2D(0, 0))
                    .setAngleOffset(angleOffset)
                    .build()
            );
            invisibleDecorationTagSet.add(tag);
        }
    }

    private PhysicsObjectState nextState(PhysicsObjectState state, List<PolarPoint> forces, double durationSec) {
        if (durationSec <= 0) {
            return state;
        }
        PolarPoint force = new PolarPoint(0, 0);
        for (PolarPoint f : forces) {
            force = force.add(f);
        }
        PolarPoint startVelocity = state.velocity();
        Point2D startVelocityPoint = startVelocity.coordinate();

        //극좌표 속도를 일반 좌표계로 변형

        Point2D scale = state.scale();
        double density = state.mass() / (scale.x() * scale.y());
        double a = scale.distance(Point2D.ORIGIN); // 부정확
        double airResistance = 0.5 * Math.pow(startVelocity.radius(), 2) * density * a * state.dragCoefficient();
        double angle = startVelocity.angle();

        PolarPoint finalForce = force.add(new PolarPoint(-airResistance, angle));


        Point2D acceleration = finalForce.coordinate().multiply(durationSec / state.mass());
        //속도 변화량
        Point2D nextVelocity = startVelocityPoint.add(acceleration);
        //나중 속도
        Point2D averageVelocity = startVelocityPoint.add(nextVelocity).multiply(0.5);
        //평균 속도
        Point2D positionChanges = averageVelocity.multiply(durationSec);
        //위치 변화량
        Point2D nextCoordinate = state.coordinate().add(positionChanges);
        //나중 위치
        return new PhysicsObjectState(nextCoordinate, nextVelocity.toPolar(), state.mass(), state.dragCoefficient(), state.scale());
    }

}

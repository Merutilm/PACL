package kr.merutilm.pacl.al.editor.vfx.t1m3ddeco;

import kr.merutilm.base.exception.VertexOverflowException;
import kr.merutilm.base.functions.LoopedList;
import kr.merutilm.base.struct.*;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.pacl.al.editor.vfx.VFXGenerator;
import kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.attribute.*;
import kr.merutilm.pacl.al.editor.vfx.t1m3ddeco.variable.*;
import kr.merutilm.pacl.al.event.events.action.Bloom;
import kr.merutilm.pacl.al.event.events.action.MoveDecorations;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;
import kr.merutilm.pacl.al.event.selectable.BlendMode;
import kr.merutilm.pacl.al.event.struct.Tag;
import kr.merutilm.pacl.data.Assets;
import kr.merutilm.pacl.data.CustomLevel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static kr.merutilm.base.struct.Point2D.isClockwise;
import static kr.merutilm.base.struct.Point3D.LIMIT_Z;
import static kr.merutilm.base.struct.Point3D.zCorrectionLine;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 3D 개체 매크로
 */
public final class VFXGenerator3DDecoration implements VFXGenerator {
    final CustomLevel level;

    /**
     * 모든 3D 장식은 해당 태그를 참조합니다.
     */
    public final String tag3DCubesAll;
    /**
     * 해당 3D 장식 개체입니다. |
     * name + [그룹]
     */
    public final String tag3DCubeGroup;
    /**
     * 모든 3D 장식의 모서리는 해당 태그를 참조합니다.
     */
    public final String tag3DCubeEdgeAll;

    /**
     * 해당 3D 장식의 모서리입니다. 서로 다른 두 꼭짓점을 잇는 기능을 합니다.
     * name + [그룹]
     */
    public final String tag3DCubeEdge;
    /**
     * 해당 3D 장식의 모서리입니다. 서로 다른 두 꼭짓점을 잇는 기능을 합니다. |
     * name + [그룹_모서리]
     */
    public final String tag3DCubeEdgeId;

    /**
     * 해당 3D 장식의 면을 구성하는 삼각형을 구성하는 이등변 삼각형의 고유 아이디입니다. |
     * name + [그룹_면 아이디_삼각 분할 그룹_(1~3)]
     */
    public final String tag3DCubePlaneId;
    /**
     * 해당 3D 장식의 면을 구성하는 삼각형입니다. |
     * name + [그룹_면 아이디_삼각 분할 그룹]
     */
    public final String tag3DCubePlaneT;
    /**
     * 해당 3D 장식의 면입니다. |
     * name + [그룹_면 아이디]
     */
    public final String tag3DCubePlane;

    /**
     * 해당 3D 장식의 그림자를 구성하는 삼각형을 구성하는 이등변 삼각형의 고유 아이디입니다. |
     * name + [그룹_면 아이디_스크린 그룹_스크린 면 아이디_삼각 분할 그룹_(1~3)]
     */
    public final String tag3DCubeShadowId;
    /**
     * 해당 3D 장식의 그림자를 구성하는 삼각형입니다. |
     * name + [그룹_면 아이디_스크린 그룹_스크린 면 아이디_삼각 분할 그룹]
     */
    public final String tag3DCubeShadowT;

    /**
     * 해당 3D 장식의 그림자입니다. |
     * name + [개체 그룹_개체 면 아이디_스크린 그룹_스크린 면 아이디]
     */
    public final String tag3DCubeShadow;

    /**
     * 광원 장식은 해당 태그를 참조합니다.
     */
    public final String tag3DLight;
    /**
     * 모든 3D 파티클은 해당 태그를 참조합니다.
     */
    public final String tag3DParticleAll;
    /**
     * 해당 3D 파티클이 속한 그룹입니다.
     */
    public final String tag3DParticleGroup;
    /**
     * 해당 3D 파티클의 고유 아이디입니다.
     */
    public final String tag3DParticleID;
    /**
     * 빛 속성 (불변객체 아님)
     */
    public final AttributeLight.Builder attributeLight = new AttributeLight.Builder(
            new ImageFile("nev_fog.png"),
            HexColor.get(255, 185, 0),
            100,
            true,
            true,
            new Point3D(0, 0, 1000),
            0.55965,
            0.00215,
            0.48325,
            1505,
            3365,
            false,
            (short) 0,
            HexColor.get(22, 22, 22, 255)
    );
    /**
     * 모서리 속성 (불변객체 아님)
     */
    public final AttributeEdge.Builder attributeEdge = new AttributeEdge.Builder(
            true,
            100,
            0.418625,
            356);
    /**
     * 면 속성 (불변객체 아님)
     */
    public final AttributePlane.Builder attributePlane = new AttributePlane.Builder(
            true,
            5,
            1,
            0.775
    );
    /**
     * 카메라 속성 (불변객체 아님)
     */
    public final AttributeCamera.Builder attributeCamera = new AttributeCamera.Builder(
            new Point3D(0, 0, 0),
            new Point3D(0, 0, 0),
            new Point3D(0, 0, 0),
            new Point3D(0, 0, 0)
    );
    /**
     * 장식 속성 (불변객체 아님)
     */
    public final AttributeAsset.Builder attributeAsset = new AttributeAsset.Builder(
            99.99,
            15,
            15,
            10,
            1,
            24,
            true
    );
    /**
     * 그림자 속성 (불변객체 아님)
     */
    public final AttributeShadow.Builder attributeShadow = new AttributeShadow.Builder(
            true,
            50,
            1
    );

    public final AttributeParticle.Builder attributeParticle = new AttributeParticle.Builder(
            BlendMode.NONE,
            2,
            77,
            5
    );

    final List<Solid> solids = new ArrayList<>();
    final List<Particle> particles = new ArrayList<>();

    /**
     * Group ID - Planes - Vertices
     */
    final List<List<Point3D[]>> solidPlaneVertices = new ArrayList<>();
    private final Map<String, MoveDecorations> lastCreatedMoveDecorationTagMap = new HashMap<>();
    private final Set<String> invisibleTagSet = new HashSet<>();
    private final Set<String> addDecorationTagSet = new HashSet<>();
    private static final short MAX_COLOR_VALUE = 255;
    public static final double SQRT_2 = Math.sqrt(2);

    /**
     * 3D 장식 매크로를 정의합니다.
     */
    VFXGenerator3DDecoration(CustomLevel level, int renderGroupID) {
        this.level = level;
        tag3DCubesAll = "nevM3C" + renderGroupID;
        tag3DCubeGroup = "nevM3C-G" + renderGroupID + ".";
        tag3DCubeEdgeAll = "nevM3C-ES" + renderGroupID;
        tag3DCubeEdge = "nevM3C-E" + renderGroupID + ".";
        tag3DCubeEdgeId = "nevM3C-EI" + renderGroupID + ".";
        tag3DCubePlaneId = "nevM3C-PI" + renderGroupID + ".";
        tag3DCubePlaneT = "nevM3C-PT" + renderGroupID + ".";
        tag3DCubePlane = "nevM3C-P" + renderGroupID + ".";
        tag3DCubeShadowId = "nevM3C-SI" + renderGroupID + ".";
        tag3DCubeShadowT = "nevM3C-ST" + renderGroupID + ".";
        tag3DCubeShadow = "nevM3C-S" + renderGroupID + ".";
        tag3DLight = "nevM3C-L" + renderGroupID;
        tag3DParticleAll = "nevM3C-PTA" + renderGroupID;
        tag3DParticleGroup = "nevM3C-PTG" + renderGroupID + ".";
        tag3DParticleID = "nevM3C-PTI" + renderGroupID + ".";
    }

    /**
     * 해당 번호의 개체를 얻습니다.
     *
     * @return 개체
     */
    Solid getSolid(int groupID) {
        return solids.get(groupID - 1);
    }

    Particle getParticle(int groupID) {
        return particles.get(groupID - 1);
    }

    void setParticle(int groupID, Particle particle) {
        particles.set(groupID - 1, particle);
    }

    void refresh3DVertices(int floor, int groupID, double changeDuration, Solid solid, double angleOffset) {
        switch (solid) {
            case Polyhedron v -> refresh3DPolyhedron(floor, groupID, changeDuration, v, angleOffset);
            case FreePrism v -> refresh3DPrism(floor, groupID, changeDuration, v, angleOffset);
            case Prism v -> refresh3DPrism(floor, groupID, changeDuration, v, angleOffset);
            case null, default -> throw new IllegalArgumentException("해당 아이디를 가진 물체가 존재하지 않습니다.");
        }
    }

    /**
     * 3DDecoration 정다면체 장식의 상태를 설정합니다. group ID가 존재할 경우 해당 장식을 움직이며,
     * 없을 경우 새로 생성합니다.
     *
     * @param floor          장식 설정을 변경할 타일
     * @param groupID        개체 그룹
     * @param changeDuration 상태 변경 기간
     * @param polyhedron     정다면체 속성
     */
    private void refresh3DPolyhedron(int floor, int groupID, double changeDuration, Polyhedron polyhedron, double angleOffset) {

        AttributeEdge curAttributeEdge = this.attributeEdge.build();
        final int index = groupID - 1;

        Point3D center = polyhedron.state().centerPosition();
        Point3D rotationCenter = polyhedron.state().centerRotatePosition();
        Point3D rotation = polyhedron.state().rotation();
        Point3D scale = polyhedron.state().scale();
        HexColor color = polyhedron.state().color();

        Point3D.Builder[] vb = polyhedron.solidType().getCreator().create(center, scale);
        int centerDepth = getDepth(applyCameraPosition(center));

        Point3D[] vertices = Arrays.stream(vb)
                .map(v -> applyCameraPosition(v.rotate(rotationCenter, rotation).build()))
                .toArray(Point3D[]::new);

        switch (polyhedron.solidType()) {
            case CUBE -> {

                Point3D[] frontPlane = new Point3D[]{vertices[0], vertices[2], vertices[3], vertices[1]};
                Point3D[] backPlane = new Point3D[]{vertices[6], vertices[4], vertices[5], vertices[7]};
                Point3D[] leftPlane = new Point3D[]{vertices[0], vertices[1], vertices[5], vertices[4]};
                Point3D[] rightPlane = new Point3D[]{vertices[6], vertices[7], vertices[3], vertices[2]};
                Point3D[] upPlane = new Point3D[]{vertices[4], vertices[6], vertices[2], vertices[0]};
                Point3D[] downPlane = new Point3D[]{vertices[1], vertices[3], vertices[7], vertices[5]};

                solidPlaneVertices.set(index, List.of(frontPlane, backPlane, leftPlane, rightPlane, upPlane, downPlane));

                HexColor frontC = refresh3DPlane(floor, groupID, 1, frontPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor backC = refresh3DPlane(floor, groupID, 2, backPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor leftC = refresh3DPlane(floor, groupID, 3, leftPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor rightC = refresh3DPlane(floor, groupID, 4, rightPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor upC = refresh3DPlane(floor, groupID, 5, upPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor downC = refresh3DPlane(floor, groupID, 6, downPlane, changeDuration, color, centerDepth, angleOffset);

                if (curAttributeEdge.thickness() > 0) {
                    refresh3DLine(floor, groupID, 1, vertices[0], vertices[1], getAvgEdgeBrightnessCol(frontC, leftC), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 2, vertices[0], vertices[2], getAvgEdgeBrightnessCol(frontC, upC), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 3, vertices[2], vertices[3], getAvgEdgeBrightnessCol(frontC, rightC), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 4, vertices[1], vertices[3], getAvgEdgeBrightnessCol(frontC, downC), changeDuration, centerDepth, angleOffset);

                    refresh3DLine(floor, groupID, 5, vertices[0], vertices[4], getAvgEdgeBrightnessCol(upC, leftC), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 6, vertices[1], vertices[5], getAvgEdgeBrightnessCol(downC, leftC), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 7, vertices[2], vertices[6], getAvgEdgeBrightnessCol(upC, rightC), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 8, vertices[3], vertices[7], getAvgEdgeBrightnessCol(downC, rightC), changeDuration, centerDepth, angleOffset);

                    refresh3DLine(floor, groupID, 9, vertices[4], vertices[5], getAvgEdgeBrightnessCol(backC, leftC), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 10, vertices[4], vertices[6], getAvgEdgeBrightnessCol(backC, upC), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 11, vertices[6], vertices[7], getAvgEdgeBrightnessCol(backC, rightC), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 12, vertices[5], vertices[7], getAvgEdgeBrightnessCol(backC, downC), changeDuration, centerDepth, angleOffset);
                }
            }
            case TETRAHEDRON -> {
                Point3D[] frontPlane = new Point3D[]{vertices[0], vertices[2], vertices[3]};
                Point3D[] leftPlane = new Point3D[]{vertices[0], vertices[1], vertices[2]};
                Point3D[] rightPlane = new Point3D[]{vertices[2], vertices[1], vertices[3]};
                Point3D[] downPlane = new Point3D[]{vertices[1], vertices[0], vertices[3]};

                solidPlaneVertices.set(index, List.of(frontPlane, leftPlane, rightPlane, downPlane));

                HexColor frontC = refresh3DPlane(floor, groupID, 1, frontPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor leftC = refresh3DPlane(floor, groupID, 2, leftPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor rightC = refresh3DPlane(floor, groupID, 3, rightPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor downC = refresh3DPlane(floor, groupID, 4, downPlane, changeDuration, color, centerDepth, angleOffset);


                if (curAttributeEdge.thickness() > 0) {
                    refresh3DLine(floor, groupID, 1, vertices[0], vertices[1], getAvgEdgeBrightnessCol(leftC, downC), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 2, vertices[0], vertices[2], getAvgEdgeBrightnessCol(leftC, frontC), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 3, vertices[0], vertices[3], getAvgEdgeBrightnessCol(frontC, downC), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 4, vertices[1], vertices[2], getAvgEdgeBrightnessCol(leftC, rightC), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 5, vertices[1], vertices[3], getAvgEdgeBrightnessCol(rightC, downC), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 6, vertices[2], vertices[3], getAvgEdgeBrightnessCol(frontC, rightC), changeDuration, centerDepth, angleOffset);
                }
            }
            case OCTAHEDRON -> {
                Point3D[] frontUPlane = new Point3D[]{vertices[0], vertices[5], vertices[2]};
                Point3D[] leftUPlane = new Point3D[]{vertices[1], vertices[2], vertices[5]};
                Point3D[] rightUPlane = new Point3D[]{vertices[0], vertices[2], vertices[4]};
                Point3D[] backUPlane = new Point3D[]{vertices[1], vertices[4], vertices[2]};
                Point3D[] frontDPlane = new Point3D[]{vertices[0], vertices[3], vertices[5]};
                Point3D[] leftDPlane = new Point3D[]{vertices[1], vertices[5], vertices[3]};
                Point3D[] rightDPlane = new Point3D[]{vertices[0], vertices[4], vertices[3]};
                Point3D[] backDPlane = new Point3D[]{vertices[1], vertices[3], vertices[4]};

                solidPlaneVertices.set(index, List.of(frontUPlane, leftUPlane, rightUPlane, backUPlane, frontDPlane, leftDPlane, rightDPlane, backDPlane));

                HexColor frontUB = refresh3DPlane(floor, groupID, 1, frontUPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor leftUB = refresh3DPlane(floor, groupID, 2, leftUPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor rightUB = refresh3DPlane(floor, groupID, 3, rightUPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor backUB = refresh3DPlane(floor, groupID, 4, backUPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor frontDB = refresh3DPlane(floor, groupID, 5, frontDPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor leftDB = refresh3DPlane(floor, groupID, 6, leftDPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor rightDB = refresh3DPlane(floor, groupID, 7, rightDPlane, changeDuration, color, centerDepth, angleOffset);
                HexColor backDB = refresh3DPlane(floor, groupID, 8, backDPlane, changeDuration, color, centerDepth, angleOffset);

                if (curAttributeEdge.thickness() > 0) {
                    refresh3DLine(floor, groupID, 1, vertices[0], vertices[2], getAvgEdgeBrightnessCol(frontUB, rightUB), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 2, vertices[0], vertices[3], getAvgEdgeBrightnessCol(frontDB, rightDB), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 3, vertices[0], vertices[4], getAvgEdgeBrightnessCol(rightUB, rightDB), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 4, vertices[0], vertices[5], getAvgEdgeBrightnessCol(frontUB, frontDB), changeDuration, centerDepth, angleOffset);

                    refresh3DLine(floor, groupID, 5, vertices[1], vertices[2], getAvgEdgeBrightnessCol(backUB, leftUB), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 6, vertices[1], vertices[3], getAvgEdgeBrightnessCol(backDB, leftDB), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 7, vertices[1], vertices[4], getAvgEdgeBrightnessCol(backUB, backDB), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 8, vertices[1], vertices[5], getAvgEdgeBrightnessCol(leftUB, leftDB), changeDuration, centerDepth, angleOffset);

                    refresh3DLine(floor, groupID, 9, vertices[2], vertices[4], getAvgEdgeBrightnessCol(rightUB, backUB), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 10, vertices[3], vertices[4], getAvgEdgeBrightnessCol(rightDB, backDB), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 11, vertices[2], vertices[5], getAvgEdgeBrightnessCol(frontUB, leftUB), changeDuration, centerDepth, angleOffset);
                    refresh3DLine(floor, groupID, 12, vertices[3], vertices[5], getAvgEdgeBrightnessCol(frontDB, leftDB), changeDuration, centerDepth, angleOffset);
                }
            }
            default -> {
                // noop
            }
        }
    }

    /**
     * 3DDecoration 각기둥 장식의 상태를 설정합니다. group ID가 존재할 경우 해당 장식을 움직이며,
     * 없을 경우 새로 생성합니다. 밑면이 정다각형입니다.
     *
     * @param floor          장식 설정을 변경할 타일
     * @param groupID        개체 그룹
     * @param changeDuration 상태 변경 기간
     * @param prism          밑면이 정다각형인 각기둥
     * @param angleOffset    각도 오프셋
     */
    private void refresh3DPrism(int floor, int groupID, double changeDuration, Prism prism, double angleOffset) {
        refresh3DPrism(floor, groupID, changeDuration, prism.convertToFreePrism(), angleOffset);
    }

    /**
     * 3DDecoration 각기둥 장식의 상태를 설정합니다. group ID가 존재할 경우 해당 장식을 움직이며,
     * 없을 경우 새로 생성합니다.
     * 점을 순회하면서 밑면을 그리기 때문에 점이 반시계방향으로 진행해야 합니다.
     *
     * @param floor          장식 설정을 변경할 타일
     * @param groupID        개체 그룹
     * @param changeDuration 상태 변경 기간
     * @param freePrism      자유 밑면 각기둥
     * @param angleOffset    각도 오프셋
     */
    private void refresh3DPrism(int floor, int groupID, double changeDuration, FreePrism freePrism, double angleOffset) {

        Shape undersideShape = freePrism.undersideShape();
        final int index = groupID - 1;

        double sx = freePrism.state().scale().x();
        double sz = freePrism.state().scale().z();


        double avgX = Arrays.stream(undersideShape.shape()).mapToDouble(Point2D::x).sum() / undersideShape.shape().length;
        double avgY = Arrays.stream(undersideShape.shape()).mapToDouble(Point2D::y).sum() / undersideShape.shape().length;
        int centerDepth = getDepth(applyCameraPosition(freePrism.state().centerPosition()));

        int angleAmount = undersideShape.shape().length;

        Point2D avg = new Point2D(avgX, avgY);

        List<Point3D> prism = new LoopedList<>(); // 점을 모아놓는 리스트
        List<Point3D[]> prismPlane = new ArrayList<>(); // 면을 모아놓는 리스트

        Point3D[] upside = new Point3D[angleAmount];
        Point3D[] underside = new Point3D[angleAmount];


        for (int i = 0; i < angleAmount; i++) {
            Point2D distance = undersideShape.shape()[i].edit()
                    .add(avg.edit()
                            .invert()
                            .build())
                    .build();

            Point2D scaledPosition = avg.edit()
                    .add(distance.edit()
                            .multiplyX(sx / 100)
                            .multiplyY(sz / 100) //x,z로 밑면 판별
                            .build())
                    .build();
            prism.add(new Point3D(scaledPosition.x(), sz / 200, scaledPosition.y()));
            prism.add(new Point3D(scaledPosition.x(), -sz / 200, scaledPosition.y())); //세로선 긋기(점 진행 방향)
        }

        prism = prism.stream()
                .map(v -> v.edit()
                        .add(freePrism.state().centerPosition())
                        .rotate(freePrism.state().centerRotatePosition(), freePrism.state().rotation())
                        .build())
                .map(this::applyCameraPosition)
                .collect(Collectors.toCollection(LoopedList::new));


        for (int i = 0; i < angleAmount; i++) {
            int k = i * 2;
            upside[angleAmount - i - 1] = prism.get(k);
            underside[i] = prism.get(k + 1); //점이 이루는 방향 반전
        }


        prismPlane.add(upside);
        prismPlane.add(underside);

        HexColor upC = refresh3DPlane(floor, groupID, angleAmount, upside, changeDuration, freePrism.state().color(), centerDepth, angleOffset);
        HexColor downC = refresh3DPlane(floor, groupID, angleAmount + 1, underside, changeDuration, freePrism.state().color(), centerDepth, angleOffset);

        LoopedList<HexColor> sideC = new LoopedList<>();

        for (int i = 0; i < angleAmount; i++) {
            int k = 2 * i;
            Point3D[] sidePlane = new Point3D[]{prism.get(k), prism.get(k + 2), prism.get(k + 3), prism.get(k + 1)};

            prismPlane.add(sidePlane);

            sideC.add(refresh3DPlane(floor, groupID, i, sidePlane, changeDuration, freePrism.state().color(), centerDepth, angleOffset));
        }

        solidPlaneVertices.set(index, Collections.unmodifiableList(prismPlane));

        for (int i = 0; i < angleAmount; i++) {
            int k = 2 * i;
            int t = 3 * i;

            refresh3DLine(floor, groupID, t, prism.get(k), prism.get(k + 1), getAvgEdgeBrightnessCol(sideC.get(i), sideC.get(i - 1)), changeDuration, centerDepth, angleOffset);
            refresh3DLine(floor, groupID, t + 1, prism.get(k), prism.get(k + 2), getAvgEdgeBrightnessCol(sideC.get(i), upC), changeDuration, centerDepth, angleOffset);
            refresh3DLine(floor, groupID, t + 2, prism.get(k + 1), prism.get(k + 3), getAvgEdgeBrightnessCol(sideC.get(i), downC), changeDuration, centerDepth, angleOffset);

        }
    }

    Map<Integer, LinkedHashMap<Double, DotState>> groupParticleTraceMap = new HashMap<>();

    private DotState getState(int groupID, double timeSec) {

        LinkedHashMap<Double, DotState> particleTrace = groupParticleTraceMap.get(groupID);
        assert particleTrace != null;
        List<Double> keyFrameData = particleTrace.keySet().stream().toList();
        List<DotState> particleStateData = particleTrace.values().stream().toList();
        assert !particleStateData.isEmpty();

        if (timeSec <= 0) {
            return particleStateData.get(0);
        }
        double lastTime = keyFrameData.get(keyFrameData.size() - 1);

        if (lastTime < timeSec) {
            throw new IllegalArgumentException("provided timeSec : " + timeSec + " has larger than last generated time : " + lastTime);
        }
        int i = 1;
        while (keyFrameData.get(i) < timeSec) {
            i++;
        }
        DotState current = particleStateData.get(i - 1);
        DotState next = particleStateData.get(i);

        double ratio = AdvancedMath.getRatio(keyFrameData.get(i - 1), keyFrameData.get(i), timeSec);

        Point3D position = Point3D.ratioDivide(current.position(), next.position(), ratio);
        Point3D centerRotatePosition = Point3D.ratioDivide(current.centerRotatePosition(), next.centerRotatePosition(), ratio);
        Point3D rotation = Point3D.ratioDivide(current.rotation(), next.rotation(), ratio);
        double scale = AdvancedMath.ratioDivide(current.scale(), next.scale(), ratio);

        return DotState.get(position, centerRotatePosition, rotation, scale);
    }

    void refresh3DParticle(int floor, double changeDuration, double angleOffset) {

        for (int groupID = 1; groupID <= particles.size(); groupID++) {

            DotState currentFirstParticleState = getParticle(groupID).state();

            AttributeAsset curAttributeAsset = this.attributeAsset.build();
            AttributeParticle curAttributeParticle = this.attributeParticle.build();

            double timeSec = level.getTimeSecByAngleOffset(floor, angleOffset);
            groupParticleTraceMap.computeIfAbsent(groupID, k -> new LinkedHashMap<>()).put(timeSec, currentFirstParticleState);

            String tagGroup = tag3DParticleGroup + groupID;
            Particle particle = getParticle(groupID);

            double parallax = curAttributeAsset.defaultParallax();

            for (int j = 0; j < particle.layers(); j++) {

                String tagID = tag3DParticleID + groupID + "_" + (j + 1);
                MoveDecorations prevEvent = lastCreatedMoveDecorationTagMap.get(tagID);

                if (!addDecorationTagSet.contains(tagID)) {

                    ImageFile imageFile = particle.image();
                    Assets.copyAsset(imageFile);

                    level.createEvent(curAttributeAsset.renderStartFloor(), new AddDecoration.Builder()
                            .setDecorationImage(imageFile)
                            .setTag(Tag.of(tag3DParticleAll, tagGroup, tagID))
                            .setScale(new Point2D(0, 0))
                            .setParallax(new Point2D(parallax, parallax))
                            .setOpacity(0d)
                            .setBlendMode(curAttributeParticle.particleBlendMode())
                            .setColor(particle.state().color())
                            .build()
                    );

                    changeDuration = 0;

                    addDecorationTagSet.add(tagID);
                }

                DotState finalState = getState(groupID, timeSec - particle.delaySec() * j);

                Point3D finalParticlePosition3D = applyCameraPosition(
                        finalState.position().edit()
                                .rotate(finalState.centerRotatePosition(), finalState.rotation())
                                .build()
                );

                double scale = Point3D.zCorrection(finalState.scale(), finalParticlePosition3D.z()) * Math.pow(particle.scaleMultiplierPerLayer(), j);

                Point2D position = finalParticlePosition3D.convertTo2D().multiply(getParallaxPositionMultiplier());

                double opacity;

                if (finalState.scale() == 0) {
                    opacity = 0;
                } else {
                    double limitZ = Point3D.getZ(finalState.scale(), curAttributeParticle.minScale());
                    double zRatio = AdvancedMath.getRatio(0, limitZ, finalParticlePosition3D.z());
                    opacity = curAttributeParticle.opacity() * (1 - Math.pow(zRatio, curAttributeParticle.opacitySquaring())) * Math.pow(particle.opacityMultiplierPerLayer(), j);
                }

                if (finalParticlePosition3D.z() < LIMIT_Z || scale < curAttributeParticle.minScale()) {
                    createInvisibleDecoration(floor, angleOffset, tagID, prevEvent);
                } else {

                    invisibleTagSet.remove(tagID);
                    int finalDepth = getDepth(finalParticlePosition3D) - getDepth(curAttributeAsset.tileZ());
                    MoveDecorations.Builder eventBuilder = new MoveDecorations.Builder();

                    if (prevEvent == null || (prevEvent.opacity() != null && AdvancedMath.fixDouble(prevEvent.opacity()) != AdvancedMath.fixDouble(opacity))) {
                        eventBuilder.setOpacity(opacity);
                    }

                    MoveDecorations data = eventBuilder
                            .setImage(particle.image())
                            .setAngleOffset(angleOffset)
                            .setTag(Tag.of(tagID))
                            .setPositionOffset(position)
                            .setDuration(changeDuration)
                            .setScale(new Point2D(scale, scale))
                            .setColor(particle.state().color())
                            .setDepth((short) finalDepth)
                            .build();
                    createIfNotSame(floor, tagID, prevEvent, data);

                }
            }
        }
    }

    private void createInvisibleDecoration(int floor, double angleOffset, String tag, MoveDecorations prevEvent) {
        if (!invisibleTagSet.contains(tag)) {
            invisibleTagSet.add(tag);
            MoveDecorations data = new MoveDecorations.Builder()
                    .setAngleOffset(angleOffset)
                    .setTag(Tag.of(tag))
                    .setDuration(0d)
                    .setDepth(Short.MAX_VALUE)
                    .setOpacity(-1000d)
                    .setScale(new Point2D(0, 0))
                    .setPositionOffset(new Point2D(0, 0))
                    .setRotationOffset(0d)
                    .build();

            createIfNotSame(floor, tag, prevEvent, data);
        }
    }

    private void createIfNotSame(int floor, String tag, @Nullable MoveDecorations prevEvent, @Nonnull MoveDecorations data) {
        boolean isSame = prevEvent != null
                         && Objects.equals(data.positionOffset(), prevEvent.positionOffset())
                         && Objects.equals(data.image(), prevEvent.image())
                         && Objects.equals(data.rotationOffset(), prevEvent.rotationOffset())
                         && Objects.equals(data.scale(), prevEvent.scale())
                         && Objects.equals(data.color(), prevEvent.color());

        if (!isSame) {
            level.createEvent(floor, data);
            lastCreatedMoveDecorationTagMap.put(tag, data);
        }
    }

    /**
     * 3DDecoration 빛 속성을 기반으로 빛 장식의 상태를 설정합니다.
     *
     * @param floor          장식 설정을 변경할 타일
     * @param changeDuration 상태 변경 기간
     * @param angleOffset    각도 오프셋
     */
    void refresh3DLight(int floor, double changeDuration, double angleOffset) {

        AttributeAsset curAttributeAsset = this.attributeAsset.build();
        AttributeLight curAttributeLight = this.attributeLight.build();

        double parallax = curAttributeAsset.defaultParallax();
        MoveDecorations prevEvent = lastCreatedMoveDecorationTagMap.get(tag3DLight);
        Point3D finalLight3D = applyCameraPosition(curAttributeLight.position());
        double distance = finalLight3D.distance(new Point3D(0, 0, 0));

        if (curAttributeLight.spreading()) {
            double intensity = generateBloomIntensity(finalLight3D, distance, curAttributeLight);
            level.createEvent(floor, new Bloom.Builder()
                    .setColor(curAttributeLight.spreadColor())
                    .setIntensity(intensity)
                    .setDuration(changeDuration)
                    .setAngleOffset(angleOffset)
                    .setThreshold(0d)
                    .build()
            );
        }
        if (curAttributeLight.showing()) {
            if (!addDecorationTagSet.contains(tag3DLight)) {
                ImageFile imageFile = curAttributeLight.lightImage();
                Assets.copyAsset(imageFile);

                level.createEvent(curAttributeAsset.renderStartFloor(), new AddDecoration.Builder()
                        .setDecorationImage(imageFile)
                        .setTag(Tag.of(tag3DLight))
                        .setScale(new Point2D(curAttributeLight.scale(), curAttributeLight.scale()))
                        .setParallax(new Point2D(parallax, parallax))
                        .setColor(curAttributeLight.lightColor())
                        .setOpacity(0d)
                        .setBlendMode(BlendMode.LINEAR_DODGE)
                        .build()
                );
                addDecorationTagSet.add(tag3DLight);
                changeDuration = 0;
            }

            if (finalLight3D.z() < LIMIT_Z) {

                createInvisibleDecoration(floor, angleOffset, tag3DLight, prevEvent);

            } else {
                invisibleTagSet.remove(tag3DLight);
                int finalDepth = getDepth(finalLight3D) - getDepth(curAttributeAsset.tileZ());

                MoveDecorations data = new MoveDecorations.Builder()
                        .setAngleOffset(angleOffset)
                        .setTag(Tag.of(tag3DLight))
                        .setDuration(changeDuration)
                        .setDepth(curAttributeLight.lockDepth() ? curAttributeLight.lockDepthValue() : (short) finalDepth)
                        .setOpacity(100d)
                        .setPositionOffset(finalLight3D.convertTo2D().multiply(getParallaxPositionMultiplier()))
                        .setScale(new Point2D(curAttributeLight.scale(), curAttributeLight.scale()).multiply(500 / distance))
                        .build();
                createIfNotSame(floor, tag3DLight, prevEvent, data);

            }
        }
    }

    private static double generateBloomIntensity(Point3D finalLight3D, double distance, AttributeLight curAttributeLight) {
        double shortestDistance = finalLight3D.distanceOfPlane(new Point3D(-1, 0, 0), new Point3D(1, 0, 0), new Point3D(0, 1, 0));
        double ratio;

        if (finalLight3D.z() > 0) {
            ratio = 0.5 + shortestDistance / (2 * distance);
        } else {
            ratio = 0.5 - shortestDistance / (2 * distance);
        }

        return curAttributeLight.minSpreadingIntensity() + ratio * (curAttributeLight.maxSpreadingIntensity() - curAttributeLight.minSpreadingIntensity());
    }


    /**
     * 서로 다른 두 점을 잇는 선분을 긋습니다.
     *
     * @param floor          장식 설정을 변경할 타입
     * @param groupID        개체 그룹
     * @param tagID          태그 아이디
     * @param v1             첫째 점
     * @param v2             둘째 점
     * @param color          밝기 (0~255)
     * @param changeDuration 변경 기간
     * @param angleOffset    각도 오프셋
     */
    private void refresh3DLine(int floor, int groupID, int tagID, Point3D v1, Point3D v2, HexColor color, double changeDuration, double centerDepth, double angleOffset) {

        AttributeAsset curAttributeAsset = attributeAsset.build();
        AttributeEdge curAttributeEdge = attributeEdge.build();
        AttributePlane curAttributePlane = attributePlane.build();

        double thickness = Point3D.zCorrection(attributeEdge.build().thickness(), v1.z() + v2.z());
        if (Double.isNaN(thickness)) {
            thickness = 0;
        }

        final String targetTagID = tag3DCubeEdgeId + groupID + "_" + tagID;


        Point3D[] c = zCorrectionLine(v1, v2);

        MoveDecorations.Builder eventBuilder = new MoveDecorations.Builder()
                .setTag(Tag.of(targetTagID))
                .setColor(color.opacity(curAttributeEdge.opacity() / 100));

        if (c[0] == null) {
            if (!invisibleTagSet.contains(targetTagID)) {
                invisibleTagSet.add(targetTagID);
                level.createEvent(floor, eventBuilder.setOpacity(-1000d)
                        .setDuration(0d)
                        .setPositionOffset(new Point2D(0, 0))
                        .setScale(new Point2D(0, 0))
                        .setDepth(Short.MAX_VALUE)
                        .build()
                );
            }
            return;
        }

        invisibleTagSet.remove(targetTagID);

        final Point2D c1 = c[0].convertTo2D();
        final Point2D c2 = c[1].convertTo2D();

        final double dx = c2.x() - c1.x();
        final double dy = c2.y() - c1.y();

        final double distance = Math.hypot(dy, dx);
        double angle = Math.toDegrees(Math.atan2(dy, dx));

        MoveDecorations prevEvent = lastCreatedMoveDecorationTagMap.get(targetTagID);
        double parallax = curAttributeAsset.defaultParallax();

        if (level.getDecorationEIDsByTag(curAttributeAsset.renderStartFloor(), targetTagID).isEmpty()) {

            ImageFile imageFile = new ImageFile("nev_3dLine.png");
            Assets.copyAsset(imageFile);

            level.createEvent(curAttributeAsset.renderStartFloor(), new AddDecoration.Builder()
                    .setDecorationImage(imageFile)
                    .setOpacity(0d)
                    .setScale(new Point2D(0, 0))
                    .setParallax(new Point2D(parallax, parallax))
                    .setTag(Tag.of(tag3DCubesAll,
                            tag3DCubeEdgeAll,
                            tag3DCubeEdge + groupID,
                            tag3DCubeGroup + groupID,
                            targetTagID))
                    .build()
            );
            changeDuration = 0;
        } else {
            Double prevAngle = prevEvent.rotationOffset();
            assert prevAngle != null;
            angle = AdvancedMath.angleCorrection(angle, prevAngle);
        }

        int finalDepth = getDepth(AdvancedMath.ratioDivide((c[1].z() + c[0].z()) / 2, centerDepth, curAttributePlane.depthDensity())) - getDepth(curAttributeAsset.tileZ());

        if (prevEvent == null || (prevEvent.opacity() != null && prevEvent.opacity() != 100)) {
            eventBuilder.setOpacity(100d);
        }


        MoveDecorations data = eventBuilder.setPositionOffset(c1.edit().multiply(getParallaxPositionMultiplier()).build())
                .setDuration(changeDuration)
                .setDepth((short) finalDepth)
                .setRotationOffset(angle)
                .setScale(new Point2D(distance * 10, thickness / 10))
                .setAngleOffset(angleOffset)
                .build();

        createIfNotSame(floor, targetTagID, prevEvent, data);
    }


    /**
     * 서로 다른 네 점으로 둘러싸인 면을 그립니다.<p>
     * d1,d2,d3,d4가 시계방향으로 진행되면 앞면입니다.<p>
     * 평면을 대상으로 작동하기 때문에 곡면에서는 제대로 작동이 되지 않을 수 있습니다,<p>
     * <p>
     * 1. z 경계 <p>
     * z 경계는 해당 값을 넘겼을때 좌표가 급격하게 커져 오버플로우가 일어나는 현상을 방지하기 위해 임의로 설정한 값으로, 표현 가능한 z의 최솟값이다.<p>
     * <p>
     * 2. 경계 밖 좌표 처리 <p>
     * 해당 좌표와 연결된 두 점을 기준으로 경계 밖 좌표를 각각 보정한다. <p>
     * 이 작업을 <strong>z-Correction</strong>이라 하고 보정된 좌표, 해당 좌표와 연결된 두 선분을 각각 <p>
     * <strong>z-CRD</strong>, <strong>z-CRL</strong>이라 칭한다.<p>
     * 상황에 따라 한 점이 <strong>z-Correction</strong>으로 인해 두 점으로 분열할 수 있다.<p>
     * <p>
     * 2-1. 네 점 중 한 점이 경계 밖인 경우 <p>
     * 한 점이 <strong>z-Correction</strong>으로 인해 두 점으로 분열하는 유일한 경우로, 오각형으로 간주한다.<p>
     * <p>
     * 2-2. 네 점 중 두 점이 경계 밖인 경우<p>
     * <strong>z-CRL</strong> 2개, <strong>z-CRD</strong>를 연결한 선분, 경계 안쪽 점 두 개를 연결한 선분으로 둘러싸인 새로운 사각형을 정의한다.<p>
     * <p>
     * 2-3. 네 점 중 세 점이 경계 밖인 경우 <p>
     * <strong>z-CRL</strong> 2개가 이루는 각이 꼭지각인 충분히 큰 이등변삼각형으로 간주한다.<p>
     * <p>
     * 2-4. 네 점이 모두 경계 밖인 경우 <p>
     * 장식을 투명하게 바꾼다.<p>
     * <p>
     * 3. 도형 채우기<p>
     * 사각형은 두 개의 삼각형으로 분열이 가능하므로 분열한다.<p>
     * 한 각이 우각인 경우는 생각하지 않는다. <p>
     * <p>
     * 4. 삼각형 <p>
     * 이등변삼각형의 밑각의 대변 r이 일정한 값이 유지되도록 x,y 크기를 조절하여 각도 범위만을 바꾼다.<p>
     * 동시에 해당 각도의 절반만큼 회전 오프셋을 더해 회전하기 편하게 한다. <p>
     * 이 시행의 결과는 반지름이 r인 원에서 원 위의 임의의 점,원점,원과 x축이 만나는 점이 이루는 삼각형과 같다.<p>
     * r의 기본값은 10이다.<p>
     * <p>
     * 5. 삼각형 이동 <p>
     * 분할한 삼각형의 한 각을 꼭지각, 그 각을 끼인각으로 하는 삼각형의 두 변중 짧은 것을 r로 정한다.<p>
     * 반시계 방향을 감지하기 위해, scale, angle 값을 각각 반전시킨 데이터와 비교하여 angle 값 변화가 작은 것을 택한다.<p>
     * <p>
     * 6. 사용된 장식의 개수 <p>
     * 모든 삼각형은 세 개의 이등변삼각형을 겹쳐 표현할 수 있다.
     *
     * @param floor          기준 타일
     * @param groupID        개체 그룹 아이디
     * @param planeID        개체 그룹 내 아이디
     * @param plane          채울 도형
     * @param changeDuration 변경 기간
     * @param centerDepth    중심점 깊이
     * @param angleOffset    각도 오프셋
     * @return 마지막으로 설정된 면의 밝기 (0 ~ 255) (단, 지정되지 않으면 255)
     */
    private HexColor refresh3DPlane(int floor, int groupID, int planeID, @Nonnull Point3D[] plane,
                                    double changeDuration, HexColor color, int centerDepth,
                                    double angleOffset) {
        //카메라가 조정된 좌표로 면을 계산하므로 빛 좌표도 카메라 조정!!
        Point3D lightPosition = applyCameraPosition(attributeLight.build().position());
        if (plane.length < 3) {
            throw new VertexOverflowException("Vertex Amount must more than 3!\nprovided : " + plane.length);
        }
        HexColor finalColor = getBrightnessColor(plane[0], plane[1], plane[2], color, lightPosition);

        return refresh3DPlane(
                floor, groupID, planeID, plane, changeDuration,
                centerDepth, lightPosition, finalColor, angleOffset,
                tag3DCubePlane, tag3DCubePlaneT, tag3DCubePlaneId,
                false);
    }

    private HexColor refresh3DPlane(int floor, int groupID, int planeID, @Nonnull Point3D[] plane,
                                    double changeDuration, int centerDepth, Point3D lightPosition,
                                    HexColor color, double angleOffset, String tagType, String tagT,
                                    String tagTID, boolean isShadow) {

        AttributeAsset curAttributeAsset = attributeAsset.build();
        AttributePlane curAttributePlane = attributePlane.build();

        if (!curAttributePlane.showing()) {
            return HexColor.WHITE;
        }

        LoopedList<Point3D> correctionLine = new LoopedList<>();
        List<Point3D> finalShape3D = new ArrayList<>();
        final double defSize = 10;

        for (int i = 0; i < plane.length; i++) { //2번 내용

            Point3D l1 = plane[i];
            Point3D l2 = plane[(i + 1) % plane.length];
            Point3D[] c = zCorrectionLine(l1, l2);

            correctionLine.add(c[0]);
            correctionLine.add(c[1]);
        }
        for (int i = 0; i < correctionLine.size(); i++) {
            if (!Objects.equals(correctionLine.get(i), correctionLine.get(i + 1)) && correctionLine.get(i) != null) {
                finalShape3D.add(correctionLine.get(i));
            }
        }

        int vertexAmount = finalShape3D.size(); //각 개수
        String targetTagPlane = tagType + groupID + "_" + planeID;


        int maxSeparateTAmount = curAttributePlane.maxSeparateTAmount();
        double parallax = curAttributeAsset.defaultParallax();

        if (vertexAmount < 3) {

            for (int normalT = 0; normalT < maxSeparateTAmount; normalT++) {

                String targetTagT = tagT + groupID + "_" + planeID + "_" + (normalT + 1);

                if (!invisibleTagSet.contains(targetTagT)) {
                    invisibleTagSet.add(targetTagT);
                    level.createEvent(floor, new MoveDecorations.Builder()
                            .setTag(Tag.of(targetTagT))
                            .setScale(new Point2D(0.0, 0.0))
                            .setPositionOffset(new Point2D(0, 0))
                            .setRotationOffset(0d)
                            .setOpacity(0d)
                            .setAngleOffset(angleOffset)
                            .setDuration(changeDuration)
                            .build()
                    );
                }
            }
            return HexColor.WHITE;
        }

        Point3D[][] allT3D = new Point3D[vertexAmount - 2][3]; // 3번 내용, [분할 수][삼각형의 꼭짓점 개수], 면을 구성하는 삼각형 여러 개

        for (int i = 0; i < vertexAmount - 2; i++) { //분할 삼각형 지정
            allT3D[i] = new Point3D[]{finalShape3D.get(0), finalShape3D.get(i + 1), finalShape3D.get(i + 2)};
        }

        if (allT3D.length > maxSeparateTAmount) {
            throw new IndexOutOfBoundsException("Max triangles in a Shape : " + maxSeparateTAmount + "\nprovided : " + allT3D.length);
        }

        for (int normalT = 0; normalT < maxSeparateTAmount; normalT++) { //4번 내용

            String targetTagT = tagT + groupID + "_" + planeID + "_" + (normalT + 1);

            if (normalT < allT3D.length) { //분할된 삼각형이 유효한 삼각형인가??
                Point3D[] curT3D = allT3D[normalT]; //삼각형 한 개
                Point2D[] curT = Arrays.stream(curT3D).map(Point3D::convertTo2D).toArray(Point2D[]::new);

                if (curAttributePlane.showing()) {

                    for (int detailT = 0; detailT < 3; detailT++) { //삼각형을 세 개의 이등변삼각형으로 분할

                        String targetTagID = tagTID + groupID + "_" + planeID + "_" + (normalT + 1) + "_" + (detailT + 1);

                        if (!addDecorationTagSet.contains(targetTagID)) {

                            ImageFile imageFile = new ImageFile("nev_3dPlane.png");
                            Assets.copyAsset(imageFile);

                            level.createEvent(curAttributeAsset.renderStartFloor(), new AddDecoration.Builder()
                                    .setDecorationImage(imageFile)
                                    .setOpacity(0d)
                                    .setScale(new Point2D(0, 0))
                                    .setParallax(new Point2D(parallax, parallax))
                                    .setTag(Tag.of(
                                            tag3DCubesAll,
                                            tag3DCubeGroup + groupID,
                                            targetTagPlane,
                                            targetTagT,
                                            targetTagID)
                                    ).build()
                            );
                            changeDuration = 0;
                            addDecorationTagSet.add(targetTagID);
                        }

                        int next = (detailT + 1) % 3;
                        int prev = (detailT + 2) % 3;

                        double degAngleRange = (curT[detailT].angle(curT[prev], curT[next]) + 180) % 360 - 180; // 180도가 넘으면 음수로 하여 다른 두 점과 이루는 각 구하기
                        double shorterLength = Math.min(curT[detailT].distance(curT[prev]), curT[detailT].distance(curT[next])); // 나머지 두 점과 이루는 점 중 짧은 것 구하기
                        double halfRadAngleRange = Math.toRadians(degAngleRange / 2);
                        double degAngle = Math.toDegrees(Math.atan2(curT[prev].y() - curT[detailT].y(), curT[prev].x() - curT[detailT].x()));

                        double sx = Math.cos(halfRadAngleRange) * SQRT_2 * defSize * shorterLength; //degAngleRange 를 scale 로 표현
                        double sy = Math.sin(halfRadAngleRange) * SQRT_2 * defSize * shorterLength;
                        double angle = degAngleRange / 2 + degAngle;

                        MoveDecorations prevEvent = lastCreatedMoveDecorationTagMap.get(targetTagID);

                        if (changeDuration > 0) {
                            double sx2 = -Math.abs(sx); //degAngleRange 를 scale 로 표현(반전 좌표)
                            double sy2 = -Math.abs(sy);
                            double angle2 = angle + 180;


                            if (prevEvent != null) { //기존에 설치된 이력이 있으므로 상대 각도를 구할 수 있음.
                                Double prevAngle = prevEvent.rotationOffset();
                                assert prevAngle != null;

                                angle = AdvancedMath.angleCorrection(angle, prevAngle);
                                angle2 = AdvancedMath.angleCorrection(angle2, prevAngle);
                                double ad = Math.abs(angle - prevAngle);
                                double ad2 = Math.abs(angle2 - prevAngle);

                                if (ad2 < ad) {
                                    angle = angle2;
                                    sx = sx2;
                                    sy = sy2;
                                }
                            }
                        }
                        short depth = (short) (((curT3D[detailT].z() * curAttributePlane.separateTDepthDispersion() + curT3D[next].z() + curT3D[prev].z()) * curAttributeAsset.zDepthMultiplier()) / (curAttributePlane.separateTDepthDispersion() + 2));

                        MoveDecorations data;
                        int finalDepth = (int) AdvancedMath.ratioDivide(depth, centerDepth, curAttributePlane.depthDensity()) - getDepth(curAttributeAsset.tileZ());
                        //장식 설치 조건 : 시계 방향, 크기 0.05 이상
                        if (isClockwise(curT) && Math.min(Math.abs(sx), Math.abs(sy)) > 0.05) {

                            data = new MoveDecorations.Builder()
                                    .setTag(Tag.of(targetTagID))
                                    .setPositionOffset(curT[detailT].edit().multiply(getParallaxPositionMultiplier()).build())
                                    .setRotationOffset(angle)
                                    .setScale(new Point2D(sx, sy))
                                    .setOpacity(100d)
                                    .setColor(color)
                                    .setAngleOffset(angleOffset)
                                    .setDuration(changeDuration)
                                    .setDepth((short) finalDepth)
                                    .build();
                            boolean isSame = prevEvent != null && Objects.equals(data.positionOffset(), prevEvent.positionOffset())
                                             && Objects.equals(data.rotationOffset(), prevEvent.rotationOffset())
                                             && Objects.equals(data.scale(), prevEvent.scale())
                                             && Objects.equals(data.color(), prevEvent.color());
                            if (!isSame) {
                                level.createEvent(floor, data);
                                invisibleTagSet.remove(targetTagT); // 투명도가 0이 아닌 장식이 추가되므로 제거할 것 !!!
                                lastCreatedMoveDecorationTagMap.put(targetTagID, data);
                            }

                        } else {

                            data = new MoveDecorations.Builder()
                                    .setTag(Tag.of(targetTagID))
                                    .setPositionOffset(new Point2D(0, 0))
                                    .setRotationOffset(0d)
                                    .setScale(new Point2D(0, 0))
                                    .setOpacity(-1000d)
                                    .setColor(color)
                                    .setAngleOffset(angleOffset)
                                    .setDuration(0d)
                                    .setDepth(Short.MAX_VALUE)
                                    .build();

                            //이전에 추가된 장식의 투명도가 0 이하일경우 추가 억제
                            if (prevEvent == null || Objects.requireNonNull(prevEvent.opacity()) > 0) {
                                level.createEvent(floor, data);
                                lastCreatedMoveDecorationTagMap.put(targetTagID, data);
                            }
                        }
                    }

                }
            } else {

                if (!invisibleTagSet.contains(targetTagT)) {
                    invisibleTagSet.add(targetTagT);
                    level.createEvent(floor, new MoveDecorations.Builder()
                            .setTag(Tag.of(targetTagT))
                            .setScale(new Point2D(0.0, 0.0))
                            .setPositionOffset(new Point2D(0, 0))
                            .setRotationOffset(0d)
                            .setOpacity(-1000.0)
                            .setAngleOffset(angleOffset)
                            .setDuration(0d)
                            .setDepth(Short.MAX_VALUE)
                            .build()
                    );
                    // 이 장식 이동은 이등변 삼각형이 아닌 분할된 일부 삼각형 기반이기 때문에
                    // 위에 있는 createInvisible 메서드와 통합할 수 없습니다 !!!!!
                }
            }
            if (!invisibleTagSet.contains(targetTagT) && !isShadow && attributeShadow.build().strength() > 0) {
                refreshShadow(floor, groupID, planeID, plane, lightPosition, color, changeDuration, angleOffset);
            }
        }
        return color;
    }

    /**
     * 그림자를 생성합니다.
     *
     * @param screenPlane 가려질 면
     */
    private void refreshShadow(int floor, int groupID, int planeID, Point3D[] screenPlane, Point3D lightPosition, HexColor screenColor, double changeDuration, double angleOffset) {
        int index = groupID - 1;
        AttributeShadow curAttributeShadow = attributeShadow.build();

        final Point3D average = Point3D.average(screenPlane[0], screenPlane[1], screenPlane[2]);
        final double lightDistance = lightPosition.distance(average);

        final HexColor sideColor = getSideColor(screenColor, lightDistance);
        final double ratioStr = curAttributeShadow.strength() / 100.0;

        HexColor shadowColor = HexColor.get(
                (short) AdvancedMath.ratioDivide(screenColor.r(), sideColor.r(), ratioStr),
                (short) AdvancedMath.ratioDivide(screenColor.g(), sideColor.g(), ratioStr),
                (short) AdvancedMath.ratioDivide(screenColor.b(), sideColor.b(), ratioStr),
                255
        );

        for (int targetIndex = 0; targetIndex < solidPlaneVertices.size(); targetIndex++) {
            for (int targetPlaneIndex = 0; targetPlaneIndex < solidPlaneVertices.get(targetIndex).size(); targetPlaneIndex++) {
                String additionalTagStr = (targetIndex + 1) + "_" + (targetPlaneIndex + 1) + "_";
                if (targetIndex != index) {
                    //가릴 면
                    Point3D[] projection =
                            Point3D.getProjection(
                                    screenPlane,
                                    solidPlaneVertices.get(targetIndex).get(targetPlaneIndex),
                                    lightPosition
                            );

                    int depth = getDepth(Point3D.average(screenPlane)) - curAttributeShadow.depthSubtract();
                    refresh3DPlane(floor, groupID, planeID, projection, changeDuration, depth, lightPosition, shadowColor, angleOffset, tag3DCubeShadow + additionalTagStr, tag3DCubeShadowT + additionalTagStr, tag3DCubeShadowId + additionalTagStr, true);

                } else {
                    refresh3DPlane(floor, groupID, planeID, new Point3D[0], changeDuration, 0, lightPosition, HexColor.WHITE, angleOffset, tag3DCubeShadow + additionalTagStr, tag3DCubeShadowT + additionalTagStr, tag3DCubeShadowId + additionalTagStr, true);

                }
            }
        }
    }

    /**
     * 설정된 좌표에서 카메라 위치를 반영한 새로운 좌표를 얻습니다.
     *
     * @param position 대상 좌표
     * @return 카메라 위치가 반영된 좌표
     */
    private Point3D applyCameraPosition(Point3D position) {
        AttributeCamera curAttributeCamera = attributeCamera.build();

        double crx = curAttributeCamera.cameraRotation().x();
        double cry = curAttributeCamera.cameraRotation().y();
        double crz = curAttributeCamera.cameraRotation().z();
        double vx = curAttributeCamera.viewPosition().x();
        double vy = curAttributeCamera.viewPosition().y();
        double vz = curAttributeCamera.viewPosition().z();
        double vrx = curAttributeCamera.viewRotation().x();
        double vry = curAttributeCamera.viewRotation().y();
        double vrz = curAttributeCamera.viewRotation().z();

        return position.edit()
                .add(curAttributeCamera.cameraPosition().edit().invert().build())
                .rotate(0, 0, 0, -crx, -cry, crz)
                .add(curAttributeCamera.viewPosition().edit().invert().build())
                .rotate(vx, vy, vz, -vrx, -vry, -vrz)
                .build();
    }


    /**
     * 시차에 따른 장식 위치의 배율을 얻습니다.
     *
     * @return 배율 parallax position multiplier
     */
    private double getParallaxPositionMultiplier() {
        return 100 / (100 - attributeAsset.build().defaultParallax());
    }

    /**
     * 해당 점의 깊이를 수치화합니다.
     */
    private int getDepth(Point3D p) {
        return getDepth(p.z());
    }

    /**
     * 해당 점의 깊이를 수치화합니다.
     */
    private int getDepth(double z) {
        int depth = (int) (z * attributeAsset.build().zDepthMultiplier());
        if (depth >= Short.MAX_VALUE) {
            System.err.println("Provided depth : " + depth + " is larger than depth value limit " + Short.MAX_VALUE + "!!");
            depth = Short.MAX_VALUE;
        }
        return depth;
    }

    /**
     * 두 색상의 평균으로 모서리 색상을 구합니다.
     */
    private HexColor getAvgEdgeBrightnessCol(HexColor b1, HexColor b2) {
        return HexColor.average(b1, b2).functionExceptAlpha(e -> (int) (e * attributeEdge.build().brightnessMultiplier()));
    }

    private HexColor getSideColor(HexColor color, double lightDistance) {
        AttributeLight curAttributeLight = attributeLight.build();
        final double maxR = curAttributeLight.intensity() * color.r();
        final double maxG = curAttributeLight.intensity() * color.g();
        final double maxB = curAttributeLight.intensity() * color.b();
        final double min = maxR / (1 + lightDistance * curAttributeLight.shadowIntensity());
        return HexColor.get(
                (short) AdvancedMath.ratioDivide(min, maxR, curAttributeLight.sideReflectivity()),
                (short) AdvancedMath.ratioDivide(min, maxG, curAttributeLight.sideReflectivity()),
                (short) AdvancedMath.ratioDivide(min, maxB, curAttributeLight.sideReflectivity()),
                MAX_COLOR_VALUE
        );
    }

    /**
     * lightPosition 에서 삼각형 d1,d2,d3을 포함하는 평면으로의 최대 입사각을 이용하여 색상을 구합니다.
     * 기준점은 삼각형의 무게중심입니다.
     *
     * @param d1 첫째 점
     * @param d2 둘째 점
     * @param d3 셋째 점
     * @return 색상
     */
    private HexColor getBrightnessColor(Point3D d1, Point3D d2, Point3D d3, HexColor color, Point3D lightPosition) {

        AttributeLight curAttributeLight = attributeLight.build(); //카메라가 조정된 좌표로 면을 계산하므로 빛 좌표도 카메라 조정


        final Point3D average = Point3D.average(d1, d2, d3);

        final double lightDistance = lightPosition.distance(average);
        final HexColor maxColor = color.functionExceptAlpha(e -> (int) (e * curAttributeLight.intensity()));
        final HexColor minColor = maxColor.functionExceptAlpha(e -> (int) (e / (1 + lightDistance * curAttributeLight.shadowIntensity())));

        final HexColor sideColor = getSideColor(color, lightDistance);


        double lightPlaneDistance = lightPosition.distanceOfPlane(d1, d2, d3);
        double reflectivity = lightPlaneDistance / lightDistance; //sin value

        Point3D[] detDots = Arrays.stream(new Point3D[]{d1, d2, d3}).map(v -> v
                .edit()
                .rotateToCenter(lightPosition, average)
                .build()).toArray(Point3D[]::new);

        Point2D[] lightViewPosition = Arrays.stream(detDots).map(Point3D::convertTo2DIgnoredCr).toArray(Point2D[]::new);

        //광원의 시점에서 삼각형을 보았을때, 그 삼각형이 시계 방향인가?
        final HexColor finalColor;


        if (isClockwise(lightViewPosition)) {
            finalColor = maxColor.function(sideColor, (e, t) -> (int) ((e - t) * reflectivity + t));
        } else {
            finalColor = minColor.function(sideColor, (e, t) -> (int) ((e - t) * reflectivity + t));
        }
        return finalColor;
    }
}
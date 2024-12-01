package kr.merutilm.pacl.al.editor.vfx.t2manimtile;

import kr.merutilm.base.struct.*;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.pacl.al.editor.fx.FxUtils;
import kr.merutilm.pacl.al.editor.vfx.VFXGenerator;
import kr.merutilm.pacl.al.editor.vfx.t2manimtile.attribute.AttributeAnimation;
import kr.merutilm.pacl.al.editor.vfx.t2manimtile.attribute.AttributeMoveTrack;
import kr.merutilm.pacl.al.editor.vfx.t5mrays.VFXGeneratorGodRays;
import kr.merutilm.pacl.al.event.events.action.MoveDecorations;
import kr.merutilm.pacl.al.event.events.action.MoveTrack;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;
import kr.merutilm.pacl.al.event.selectable.RelativeTo;
import kr.merutilm.pacl.al.event.struct.EID;
import kr.merutilm.pacl.al.event.struct.RelativeTile;
import kr.merutilm.pacl.al.event.struct.Tag;
import kr.merutilm.pacl.data.CustomLevel;
import kr.merutilm.pacl.data.EIN;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 길 이동 애니메이션을 다루는 매크로입니다.
 */
public final class VFXGeneratorAnimateTile implements VFXGenerator {
    private final CustomLevel level;

    public final AttributeMoveTrack.Builder attributeMoveTrack = new AttributeMoveTrack.Builder(
            0,
            -10000,
            false);

    public VFXGeneratorAnimateTile(CustomLevel level) {
        this.level = level;
    }

    /**
     * 길 등장/소멸 애니메이션을 추가합니다.
     * 해당 타일에 GodRays 효과가 추가된 경우 함께 영향을 받습니다.
     * 비트 수는 시작 타일의 bpm을 따라갑니다.
     *
     * @param startTile             애니메이션을 넣을 시작 타일
     * @param endTile               애니메이션을 넣을 마지막 타일
     * @param normalScale           기본 타일 크기
     * @param normalOpacity         기본 타일 투명도
     * @param onTileOpacity         공이 해당 타일을 밟았을 때 투명도
     * @param onTileOpacityDuration 공이 해당 타일을 밟은 직후 투명도 설정까지의 딜레이(비트)
     * @param percentage            타일당 애니메이션 발동 확률(%)
     */
    public void addAdvancedTileAnimation(int startTile, int endTile,
                                         @Nullable AttributeAnimation aheadAnimation,
                                         double normalScale, double normalOpacity, double onTileOpacity, double onTileOpacityDuration, double percentage,
                                         @Nullable AttributeAnimation behindAnimation) {
        addAdvancedTileAnimation(startTile, endTile, aheadAnimation, normalScale, normalOpacity, onTileOpacity, onTileOpacityDuration, percentage, behindAnimation, null);
    }

    /**
     * 길 등장/소멸 애니메이션을 추가합니다.
     * 해당 타일에 GodRays 효과가 추가된 경우 함께 영향을 받습니다.
     * 비트 수는 시작 타일의 bpm을 따라갑니다.
     *
     * @param startTile             애니메이션을 넣을 시작 타일
     * @param endTile               애니메이션을 넣을 마지막 타일
     * @param normalScale           기본 타일 크기
     * @param normalOpacity         기본 타일 투명도
     * @param onTileOpacity         공이 해당 타일을 밟았을 때 투명도
     * @param percentage            타일당 애니메이션 발동 확률(%)
     * @param onTileOpacityDuration 공이 해당 타일을 밟은 직후 투명도 설정까지의 딜레이(비트)
     * @param targetGodRays         감지할 GodRays 장식
     */
    public void addAdvancedTileAnimation(int startTile, int endTile,
                                         @Nullable AttributeAnimation aheadAnimation,
                                         double normalScale, double normalOpacity, double onTileOpacity, double onTileOpacityDuration, double percentage,
                                         @Nullable AttributeAnimation behindAnimation,
                                         @Nullable VFXGeneratorGodRays targetGodRays) {
        level.createEvent(getAdvancedTileAnimation(startTile, endTile, aheadAnimation, normalScale, normalOpacity, onTileOpacity, onTileOpacityDuration, percentage, behindAnimation, targetGodRays));
    }

    public List<EIN.ABSEventData> getAdvancedTileAnimation(int startTile, int endTile,
                                                           @Nullable AttributeAnimation aheadAnimation,
                                                           double normalScale, double normalOpacity, double onTileOpacity, double onTileOpacityDuration, double percentage,
                                                           @Nullable AttributeAnimation behindAnimation,
                                                           @Nullable VFXGeneratorGodRays targetGodRays) {
        List<EIN.ABSEventData> actions = new ArrayList<>();

        AttributeMoveTrack curAttributeMoveTrack = attributeMoveTrack.build();

        if (aheadAnimation != null) {

            actions.add(new EIN.ABSEventData(curAttributeMoveTrack.floor(), new MoveTrack.Builder()
                    .setDuration(0d)
                    .setStartTile(new RelativeTile(startTile, RelativeTo.Tile.START))
                    .setEndTile(new RelativeTile(endTile, RelativeTo.Tile.START))
                    .setOpacity(0d)
                    .setAngleOffset(curAttributeMoveTrack.angleOffset())
                    .build()
            ));

            if (targetGodRays != null) {
                actions.add(new EIN.ABSEventData(curAttributeMoveTrack.floor(), new MoveDecorations.Builder()
                        .setPositionOffset(Point2D.ORIGIN)
                        .setDuration(0d)
                        .setColor(HexColor.get(0, 0, 0, 0))
                        .setTag(Tag.of(targetGodRays.tagRayShadowPartRay + true))
                        .setAngleOffset(curAttributeMoveTrack.angleOffset())
                        .build()
                ));

                actions.add(new EIN.ABSEventData(curAttributeMoveTrack.floor(), new MoveDecorations.Builder()
                        .setPositionOffset(Point2D.ORIGIN)
                        .setDuration(0d)
                        .setColor(HexColor.get(255, 255, 255, 0))
                        .setTag(Tag.of(targetGodRays.tagRayShadowPartRay + false))
                        .setAngleOffset(curAttributeMoveTrack.angleOffset())
                        .build()
                ));
            }
        }

        double bpm = level.getBPM(startTile);
        double opacityHex = HexColor.convertOpacityToHex(normalOpacity);
        double onTileOpacityHex = HexColor.convertOpacityToHex(onTileOpacity);

        FxUtils.fxFloors(floor -> {

            if (!AdvancedMath.isExecuted(percentage)) {
                return;
            }

            boolean canMoveRays = targetGodRays != null && level.getDecorationTags(floor).contains(targetGodRays.tagRayGroups + floor);
            double finalNormalScale = normalScale * level.getRadius(floor) / 100;
            double finalNormalOpacity = curAttributeMoveTrack.hide() ? 0 : normalOpacity;
            double finalOnTileOpacity = curAttributeMoveTrack.hide() ? 0 : onTileOpacity;
            String tagShadowGroup;
            String tagLightGroup;
            if (canMoveRays) {
                tagShadowGroup = targetGodRays.tagRayShadowPartGroups + floor + "_" + true;
                tagLightGroup = targetGodRays.tagRayShadowPartGroups + floor + "_" + false;
            } else {
                tagShadowGroup = "";
                tagLightGroup = "";
            }
            if (aheadAnimation == null) {
                if (floor == startTile) {
                    actions.add(new EIN.ABSEventData(curAttributeMoveTrack.floor(), new MoveTrack.Builder() //null일 경우, normalScale 반영
                            .setStartTile(new RelativeTile(startTile, RelativeTo.Tile.START))
                            .setEndTile(new RelativeTile(endTile, RelativeTo.Tile.START))
                            .setDuration(0d)
                            .setScale(new Point2D(finalNormalScale, finalNormalScale))
                            .setOpacity(finalNormalOpacity)
                            .setAngleOffset(curAttributeMoveTrack.angleOffset())
                            .build()
                    ));
                }
            } else {

                double convertedAheadAngleOffset = aheadAnimation.animationStartBeats() * -180 * level.getBPM(floor) / bpm;
                double convertedAheadDuration = getFinalDuration(aheadAnimation, floor, bpm);
                double convertedRandomAheadScale = getFinalRandomScale(aheadAnimation);
                Point2D convertedRandomAheadPos = getFinalRandomPosition(aheadAnimation);
                double convertedRandomAheadRot = getFinalRandomRotation(aheadAnimation);

                actions.add(new EIN.ABSEventData(floor, new MoveTrack.Builder() //타일 등장 직전 설정
                        .setAngleOffset(convertedAheadAngleOffset)
                        .setDuration(0d)
                        .setScale(new Point2D(convertedRandomAheadScale, convertedRandomAheadScale))
                        .setPositionOffset(convertedRandomAheadPos)
                        .setRotationOffset(convertedRandomAheadRot)
                        .build()
                ));


                actions.add(new EIN.ABSEventData(floor, new MoveTrack.Builder() //타일 등장
                        .setAngleOffset(convertedAheadAngleOffset)
                        .setDuration(convertedAheadDuration)
                        .setScale(new Point2D(finalNormalScale, finalNormalScale))
                        .setPositionOffset(new Point2D(0.0, 0.0))
                        .setRotationOffset(0d)
                        .setEase(aheadAnimation.ease())
                        .setOpacity(finalNormalOpacity)
                        .build()
                ));


                if (canMoveRays) {

                    List<EID> address = level.getDecorationEIDsByTag(floor, targetGodRays.tagRayGroups + floor);


                    for (int i = 1; i <= address.size(); i++) { //크기설정

                        double animationScale = Objects.requireNonNull((AddDecoration) (level.getDecoration(floor, address.get(i - 1).address()))).scale().y(); // 음수가 적용된 값은 Y임

                        double convertedAheadScale = animationScale * convertedRandomAheadScale / 100;
                        actions.add(new EIN.ABSEventData(floor, new MoveDecorations.Builder() //이동 초기 위치 설정
                                .setAngleOffset(convertedAheadAngleOffset)
                                .setDuration(0d)
                                .setScale(new Point2D(Math.abs(convertedAheadScale), convertedAheadScale))
                                .setPositionOffset(convertedRandomAheadPos)
                                .setRotationOffset(convertedRandomAheadRot)
                                .setTag(Tag.of(targetGodRays.tagRayID + floor + "_" + i))
                                .build()
                        ));

                        actions.add(new EIN.ABSEventData(floor, new MoveDecorations.Builder() //이동
                                .setAngleOffset(convertedAheadAngleOffset)
                                .setDuration(convertedAheadDuration)
                                .setEase(aheadAnimation.ease())
                                .setScale(new Point2D(Math.abs(animationScale * finalNormalScale / 100), animationScale * finalNormalScale / 100))
                                .setPositionOffset(new Point2D(0.0, 0.0))
                                .setRotationOffset(0d)
                                .setTag(Tag.of(targetGodRays.tagRayID + floor + "_" + i))
                                .build()
                        ));
                    }

                    actions.add(new EIN.ABSEventData(floor, new MoveDecorations.Builder() //그림자 영역 생성 투명도 0 설정
                            .setAngleOffset(convertedAheadAngleOffset)
                            .setDuration(0d)
                            .setColor(HexColor.get(0, 0, 0, 0))
                            .setTag(Tag.of(tagShadowGroup))
                            .build()
                    ));


                    actions.add(new EIN.ABSEventData(floor, new MoveDecorations.Builder() //빛 영역 생성 투명도 0 설정
                            .setAngleOffset(convertedAheadAngleOffset)
                            .setDuration(0d)
                            .setColor(HexColor.get(255, 255, 255, 0))
                            .setTag(Tag.of(tagLightGroup))
                            .build()
                    ));

                }
            }

            if (canMoveRays) {
                actions.add(new EIN.ABSEventData(curAttributeMoveTrack.floor(), new MoveDecorations.Builder() //그림자 영역 생성 투명도 변화
                        .setAngleOffset(curAttributeMoveTrack.angleOffset())
                        .setDuration(0d)
                        .setColor(HexColor.get(0, 0, 0, (int)opacityHex))
                        .setTag(Tag.of(tagShadowGroup))
                        .build()
                ));


                actions.add(new EIN.ABSEventData(curAttributeMoveTrack.floor(), new MoveDecorations.Builder() //빛 영역 생성 투명도 변화
                        .setAngleOffset(curAttributeMoveTrack.angleOffset())
                        .setDuration(0d)
                        .setColor(HexColor.get(255, 255, 255, (int)opacityHex))
                        .setTag(Tag.of(tagLightGroup))
                        .build()
                ));
            }

            if (finalOnTileOpacity != finalNormalOpacity) {
                actions.add(new EIN.ABSEventData(floor, new MoveTrack.Builder() //타일 밟을 시 투명도 조정
                        .setDuration(onTileOpacityDuration)
                        .setPositionOffset(null)
                        .setOpacity(finalOnTileOpacity)
                        .build()
                ));
            }

            if (canMoveRays) {

                actions.add(new EIN.ABSEventData(floor, new MoveDecorations.Builder() //공이 타일을 지날 때 그림자 영역 투명도 변화
                        .setDuration(onTileOpacityDuration)
                        .setColor(HexColor.get(0, 0, 0, (int)onTileOpacityHex))
                        .setTag(Tag.of(tagShadowGroup))
                        .build()
                ));


                actions.add(new EIN.ABSEventData(floor, new MoveDecorations.Builder() //공이 타일을 지날 때 빛 영역 투명도 변화
                        .setDuration(onTileOpacityDuration)
                        .setColor(HexColor.get(255, 255, 255, (int)onTileOpacityHex))
                        .setTag(Tag.of(tagLightGroup))
                        .build()
                ));
            }


            if (behindAnimation != null) {

                double convertedBehindAngleOffset = level.isMidSpinTile(floor) ? 0 : level.getTravelAngle(floor);
                double convertedBehindDuration = getFinalDuration(behindAnimation, floor, bpm);
                double convertedRandomBehindScale = getFinalRandomScale(behindAnimation);
                Point2D convertedRandomBehindPos = getFinalRandomPosition(behindAnimation);
                double convertedRandomBehindRot = getFinalRandomRotation(behindAnimation);

                actions.add(new EIN.ABSEventData(floor, new MoveTrack.Builder() //타일 소멸
                        .setAngleOffset(convertedBehindAngleOffset)
                        .setDuration(convertedBehindDuration)
                        .setScale(new Point2D(convertedRandomBehindScale, convertedRandomBehindScale))
                        .setPositionOffset(convertedRandomBehindPos)
                        .setRotationOffset(convertedRandomBehindRot)
                        .setEase(behindAnimation.ease())
                        .setOpacity(0d)
                        .build()
                ));

                if (canMoveRays) {

                    List<EID> address = level.getDecorationEIDsByTag(floor, targetGodRays.tagRayGroups + floor);

                    for (int i = 1; i <= address.size(); i++) { //크기설정

                        double animationScale = Objects.requireNonNull((AddDecoration) (level.getDecoration(floor, address.get(i - 1).address()))).scale().y(); // 음수가 적용된 값은 Y임

                        double convertedBehindScale = animationScale * convertedRandomBehindScale / 100;


                        actions.add(new EIN.ABSEventData(floor, new MoveDecorations.Builder() //소멸 좌표로 이동
                                .setAngleOffset(convertedBehindAngleOffset)
                                .setDuration(convertedBehindDuration)
                                .setEase(behindAnimation.ease())
                                .setScale(new Point2D(Math.abs(convertedBehindScale), convertedBehindScale))
                                .setPositionOffset(convertedRandomBehindPos)
                                .setRotationOffset(convertedRandomBehindRot)
                                .setTag(Tag.of(targetGodRays.tagRayID + floor + "_" + i))
                                .build()
                        ));
                    }

                    actions.add(new EIN.ABSEventData(floor, new MoveDecorations.Builder() //그림자 영역 소멸 투명도 0 설정
                            .setAngleOffset(convertedBehindAngleOffset)
                            .setDuration(convertedBehindDuration)
                            .setColor(HexColor.get(0, 0, 0, 0))
                            .setTag(Tag.of(tagShadowGroup))
                            .build()
                    ));

                    actions.add(new EIN.ABSEventData(floor, new MoveDecorations.Builder() //빛 영역 소멸 투명도 0 설정
                            .setAngleOffset(convertedBehindAngleOffset)
                            .setDuration(convertedBehindDuration)
                            .setColor(HexColor.get(255, 255, 255, 0))
                            .setTag(Tag.of(tagLightGroup))
                            .build()
                    ));

                }
            }

        }, startTile, endTile);
        return actions;
    }

    private double getFinalDuration(AttributeAnimation animation, int floor, double bpm) {
        return animation.duration() * AdvancedMath.random(1 - animation.randomDurationP() / 100, 1 + animation.randomDurationP() / 100) * level.getBPM(floor) / bpm;
    }

    private double getFinalRandomScale(AttributeAnimation animation) {
        return animation.scale() * AdvancedMath.random(1 - animation.randomScaleP() / 100, 1 + animation.randomScaleP() / 100);
    }

    private Point2D getFinalRandomPosition(AttributeAnimation animation) {
        return animation.position().getInCircleRandomPoint(animation.randomRadius());
    }

    private double getFinalRandomRotation(AttributeAnimation animation) {
        return animation.rotation() + AdvancedMath.random(-animation.randomRotation(), animation.randomRotation());
    }

}

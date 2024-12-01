package kr.merutilm.pacl.al.editor.vfx.t5mrays;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.editor.vfx.VFXGenerator;
import kr.merutilm.pacl.al.editor.vfx.t5mrays.variable.RayType;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;
import kr.merutilm.pacl.al.event.selectable.BlendMode;
import kr.merutilm.pacl.al.event.struct.Tag;
import kr.merutilm.pacl.data.Assets;
import kr.merutilm.pacl.data.CustomLevel;

import javax.annotation.Nonnull;
import java.util.function.IntPredicate;

/**
 * The type VFXGenerator god rays.
 */
public final class VFXGeneratorGodRays implements VFXGenerator {
    private final CustomLevel level;
    /**
     * 모든 godRays 장식은 해당 태그를 참조합니다.
     */
    public final String tagAllRays;
    /**
     * 해당 godRays 장식이 포함된 타일을 확인합니다. |
     * name + [타일 번호]
     */
    public final String tagRayGroups;

    /**
     * 모든 그림자를 표현하는 godRays 장식은 해당 태그를 참조합니다. |
     * name + [true/false]
     */
    public final String tagRayShadowPartRay;

    /**
     * 해당 godRays 장식이 그림자를 표현하는지 확인합니다. |
     * name + [타일 번호_true/false]
     */
    public final String tagRayShadowPartGroups;

    /**
     * 해당 godRays 장식이 해당 타일에서 몇 번째로 설치되었는지 확인합니다. |
     * name + [레이어]
     */
    public final String tagRayLayers;

    /**
     * 해당 godRays 장식의 고유 아이디를 확인합니다. |
     * name + [타일 번호_레이어]
     */
    public final String tagRayID;

    public VFXGeneratorGodRays(CustomLevel level, int renderGroupID) {
        this.level = level;
        tagAllRays = "nevMGR" + renderGroupID;
        tagRayGroups = "nevMGR-G" + renderGroupID + ".";
        tagRayShadowPartRay = "nevMGR-SPR" + renderGroupID + ".";
        tagRayShadowPartGroups = "nevMGR-SPG" + renderGroupID + ".";
        tagRayLayers = "nevMGR-L" + renderGroupID + ".";
        tagRayID = "nevMGR-I" + renderGroupID + ".";
    }

    /**
     * GodRays 장식을 추가합니다. 타일 크기를 기준으로 빛 방향을 결정합니다.
     *
     * @param startTile         장식을 넣을 시작 타일
     * @param endTile           장식을 넣을 끝 타일
     * @param center            소실점
     * @param minSize           최소 장식 크기
     * @param multiplier        장식 크기 배율
     * @param layers            장식 양
     * @param lightRayType      빛 장식 타입
     * @param lightOpacity      최대 빛 불투명도
     * @param shadowRayType     그림자 장식 타입
     * @param shadowOpacity     최대 그림자 불투명도
     * @param opacityMultiplier 레이어간 불투명도 배율
     * @param reversed          빛의 방향을 반전합니다.
     */
    public void addGodRays(int startTile, int endTile, Point2D center, double minSize, double multiplier, int layers, @Nonnull RayType lightRayType, BlendMode blendMode, double lightOpacity, @Nonnull RayType shadowRayType, double shadowOpacity, double opacityMultiplier, boolean reversed, IntPredicate floorToConditionFunction) {
        for (int floor = startTile; floor <= endTile; floor++) {

            if (!floorToConditionFunction.test(floor)) {
                continue;
            }


            final int absTravelAngle = (int) Math.round(level.getAbsoluteIncludedAngle(floor));
            double firstScale = minSize;
            double scale;
            double rotation = level.getAngleExceptMidSpin(floor);
            if (!level.isMidSpinTile(floor) && absTravelAngle > 180 && absTravelAngle != 360) {
                firstScale *= -1;
            }
            scale = firstScale;
            int boundaryLightShadowLayer = 0;
            for (int i = 1; i <= layers; i++) {
                if (Math.abs(scale) < 100) {
                    boundaryLightShadowLayer = i;
                }
                scale *= multiplier;
            }

            scale = firstScale;


            for (int i = 1; i <= layers; i++) {
                Point2D parallax = new Point2D(100 - Math.abs(scale), 100 - Math.abs(scale));

                AddDecoration.Builder event = new AddDecoration.Builder()
                        .setPosition(new Point2D(center.x() * parallax.x() / (100 - parallax.x()), center.y() * parallax.y() / (100 - parallax.y())))
                        .setScale(new Point2D(Math.abs(scale), scale))
                        .setRotation(rotation)
                        .setParallax(parallax)
                        .setDepth((short) (boundaryLightShadowLayer - i))
                        .setBlendMode(blendMode)
                        .setTag(Tag.of(tagAllRays,
                                tagRayGroups + floor,
                                tagRayShadowPartRay + (Math.abs(scale) >= 100 == reversed),
                                tagRayShadowPartGroups + floor + "_" + (Math.abs(scale) >= 100 == reversed),
                                tagRayLayers + i + " " + tagRayID + floor + "_" + i));

                if (i <= boundaryLightShadowLayer) {
                    if (reversed) {
                        ImageFile imageFile = createImage(lightRayType, absTravelAngle);
                        level.createEvent(floor, event.setDecorationImage(imageFile)
                                .setColor(HexColor.get(255, 255, 255, 0))
                                .setOpacity(lightOpacity * Math.pow(opacityMultiplier, boundaryLightShadowLayer - i))
                                .build()
                        );


                    } else {
                        ImageFile imageFile = createImage(shadowRayType, absTravelAngle);
                        level.createEvent(floor, event.setDecorationImage(imageFile)
                                .setColor(HexColor.get(0, 0, 0, 0))
                                .setOpacity(shadowOpacity * Math.pow(opacityMultiplier, boundaryLightShadowLayer - i))
                                .build()
                        );

                    }
                } else {
                    if (reversed) {
                        ImageFile imageFile = createImage(shadowRayType, absTravelAngle);
                        level.createEvent(floor, event.setDecorationImage(imageFile)
                                .setColor(HexColor.get(0, 0, 0, 0))
                                .setOpacity(shadowOpacity * Math.pow(opacityMultiplier, i - boundaryLightShadowLayer))
                                .build()
                        );

                    } else {
                        ImageFile imageFile = createImage(lightRayType, absTravelAngle);
                        level.createEvent(floor, event.setDecorationImage(imageFile)
                                .setColor(HexColor.get(255, 255, 255, 0))
                                .setOpacity(lightOpacity * Math.pow(opacityMultiplier, i - boundaryLightShadowLayer))
                                .build()
                        );
                    }
                }
                scale *= multiplier;
            }
        }
    }

    private ImageFile createImage(RayType type, int absRelative) {
        String imageName = "nev_travel_" + type.toString();
        if (absRelative == 0) {
            imageName += "0";
        } else {
            if (absRelative > 180 && absRelative != 360) {
                absRelative = 360 - absRelative;
            }
            imageName += absRelative;
        }
        ImageFile result = new ImageFile(imageName + ".png");
        Assets.copyAsset(result);
        return result;
    }
}

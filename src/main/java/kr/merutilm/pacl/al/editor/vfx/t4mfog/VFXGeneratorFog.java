package kr.merutilm.pacl.al.editor.vfx.t4mfog;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.pacl.al.editor.fx.FxUtils;
import kr.merutilm.pacl.al.editor.vfx.VFXGenerator;
import kr.merutilm.pacl.al.editor.vfx.t5mrays.attribute.AttributeAsset;
import kr.merutilm.pacl.al.event.events.action.MoveDecorations;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;
import kr.merutilm.pacl.al.event.struct.Tag;
import kr.merutilm.pacl.data.Assets;
import kr.merutilm.pacl.data.CustomLevel;

/**
 * The type VFXGenerator fog.
 */
public final class VFXGeneratorFog implements VFXGenerator {
    private final CustomLevel level;

    /**
     * 모든 MovingFog 장식은 해당 태그를 참조합니다.
     */
    public final String tagAllFogs;

    /**
     * 해당 MovingFog 장식이 속한 개체를 확인합니다. |
     * name + [아이디]
     */
    public final String tagFogGroups;

    /**
     * 해당 MovingFog 장식의 고유 아이디를 확인합니다. |
     * name + [아이디_레이어]
     */
    public final String tagFogID;

    public final AttributeAsset.Builder attributeAsset = new AttributeAsset.Builder(
            new ImageFile("nev_fog.png")
    );

    public VFXGeneratorFog(CustomLevel level, int renderGroupID) {
        this.level = level;
        tagAllFogs = "nevMFG" + renderGroupID;
        tagFogGroups = "nevMFG-G" + renderGroupID + ".";
        tagFogID = "nevMFG-I" + renderGroupID + ".";
    }

    /**
     * MovingFog 장식을 추가합니다.
     * 투명도는 최종 결정된 시차와 최소 시차의 관계에 영향을 받습니다.
     *
     * @param floor              장식을 넣을 타일
     * @param layers             장식 개수
     * @param depth              장식 깊이
     * @param minRadius          생성 중심점을 해당 타일 기준 몇 타일 떨어진 곳에 둘 것인지 설정합니다. 소멸 중심점은 생성 중심점에 원점 대칭입니다.
     * @param maxRadius          중심점 기준 생성/소멸 반경
     * @param duration           이동 기간, {fadeDuration}의 2배보다 커야 합니다.
     * @param fadeDuration       생성/소멸 페이드 기간
     * @param minRotation        이동 중 최소 회전량
     * @param maxRotation        이동 중 최대 회전량
     * @param minRotationRadius  최소 회전 반경
     * @param maxRotationRadius  최대 회전 반경
     * @param minScale           최소 크기
     * @param maxScale           최대 크기
     * @param scaleMultiplier    추가 생성 시 크기 배율
     * @param color              장식 색상
     * @param minParallax        최소 시차
     * @param maxParallax        최대 시차
     * @param minParallaxOpacity 최소 시차에서의 투명도
     * @param intervalBeats      잔상 이동 간격
     * @param opacityMultiplier  추가 생성 시 잔상 투명도 배율
     */
    public void addMovingFogs(int floor, int layers, short depth, double minRadius,
                              double maxRadius, double duration, double fadeDuration,
                              double minRotation, double maxRotation, double minRotationRadius,
                              double maxRotationRadius, double minScale, double maxScale, double scaleMultiplier,
                              HexColor color, double minParallax, double maxParallax, double minParallaxOpacity,
                              double intervalBeats, double opacityMultiplier) {

        double baseBPM = level.getBPM(floor);
        double distance = (maxRadius + minRadius) / 2;
        double radius = Math.abs(maxRadius - minRadius) / 2;

        if (duration < fadeDuration * 2) {
            throw new IllegalArgumentException("trackColor : " + duration + " must be at a least twice as large as A : " + fadeDuration + "!");
        }


        if (level.getActionIDsByTag(0, tagAllFogs).isEmpty()) {
            level.createEvent(0, new MoveDecorations.Builder()
                    .setDuration(0d)
                    .setOpacity(100d)
                    .setTag(Tag.of(tagAllFogs))
                    .build()
            );
        }

        final double parallax = AdvancedMath.random(minParallax, maxParallax);
        final int finalGroup = floor;
        final Point2D.Builder selectedPosition = Point2D.getOnCircleRandomPoint(distance).edit();

        final Point2D startPosition = selectedPosition
                .build()
                .getInCircleRandomPoint(radius);

        final Point2D endPosition = selectedPosition
                .invert()
                .build()
                .getInCircleRandomPoint(radius);

        final double finalRotation = AdvancedMath.random(minRotation, maxRotation);
        final Point2D finalRotateRadius = Point2D.ORIGIN.getInCircleRangeRandomPoint(minRotationRadius, maxRotationRadius);
        final double scale = AdvancedMath.random(minScale, maxScale);
        final Point2D finalScale = new Point2D(scale, scale);
        final double finalFadeDuration = fadeDuration * level.getBPM(floor) / baseBPM;
        final double finalDuration = duration * level.getBPM(floor) / baseBPM;
        final double finalIntervalBeats = intervalBeats * level.getBPM(floor) / baseBPM;

        ImageFile imageFile = attributeAsset.build().imageFile();
        Assets.copyAsset(imageFile);

        FxUtils.fxOneTile((f, attempts) -> {

            final double opacityHex = HexColor.convertOpacityToHex(minParallaxOpacity * Math.pow(opacityMultiplier, attempts - 1.0) * (100 - parallax) / (100 - minParallax));
            final String tagGroup = tagFogID + finalGroup + "_" + attempts;

            level.createEvent(f, new AddDecoration.Builder()//장식 생성
                    .setDecorationImage(imageFile)
                    .setScale(finalScale.multiply(Math.pow(scaleMultiplier, attempts)))
                    .setPosition(startPosition)
                    .setPivotOffset(finalRotateRadius)
                    .setDepth(depth)
                    .setOpacity(0d)
                    .setParallax(new Point2D(parallax, parallax))
                    .setColor(HexColor.get(color.r(), color.g(), color.b(), 0))
                    .setTag(Tag.of(tagAllFogs,
                            tagFogGroups + finalGroup,
                            tagGroup))
                    .build()
            );

            level.createEvent(f, new MoveDecorations.Builder()//설정
                    .setDuration(0d)
                    .setPositionOffset(new Point2D(0, 0))
                    .setRotationOffset(0d)
                    .setAngleOffset(finalIntervalBeats * 180 * (attempts - 1))
                    .setTag(Tag.of(tagGroup))
                    .build()
            );

            level.createEvent(f,  new MoveDecorations.Builder()//생성
                    .setDuration(finalFadeDuration)
                    .setColor(HexColor.get(color.r(), color.g(), color.b(), (int)opacityHex))
                    .setAngleOffset(finalIntervalBeats * 180 * (attempts - 1))
                    .setTag(Tag.of(tagGroup))
                    .setEase(Ease.OUT_QUAD)
                    .build()
            );

            level.createEvent(f, new MoveDecorations.Builder()//이동
                    .setDuration(finalDuration)
                    .setPositionOffset(endPosition)
                    .setRotationOffset(finalRotation)
                    .setAngleOffset(finalIntervalBeats * 180 * (attempts - 1))
                    .setTag(Tag.of(tagGroup))
                    .build()
            );

            level.createEvent(f, new MoveDecorations.Builder()//소멸
                    .setDuration(finalFadeDuration)
                    .setAngleOffset((finalIntervalBeats * (attempts - 1) + finalDuration - finalFadeDuration) * 180)
                    .setColor(HexColor.get(color.r(), color.g(), color.b(), 0))
                    .setTag(Tag.of(tagGroup))
                    .setEase(Ease.IN_QUAD)
                    .build()
            );
            
        }, floor, layers);
    }


}

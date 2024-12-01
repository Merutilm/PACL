package kr.merutilm.pacl.al.editor.vfx.t6typo;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.editor.vfx.VFXGenerator;
import kr.merutilm.pacl.al.editor.vfx.t6typo.attribute.AttributeAsset;
import kr.merutilm.pacl.al.editor.vfx.t6typo.variable.TextAppearance;
import kr.merutilm.pacl.al.editor.vfx.t6typo.variable.TextProperties;
import kr.merutilm.pacl.al.event.events.action.MoveDecorations;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;
import kr.merutilm.pacl.al.event.selectable.BlendMode;
import kr.merutilm.pacl.al.event.struct.Tag;
import kr.merutilm.pacl.data.CustomLevel;

import javax.annotation.Nonnull;
import java.util.*;

public class VFXGeneratorTypos implements VFXGenerator {
    private final CustomLevel level;
    /**
     * 모든 타이포 장식은 해당 태그를 참조합니다.
     */
    public final String tagAllTypos;
    /**
     * 해당 타이포 장식이 포함된 그룹을 확인합니다. |
     * name + [그룹 아이디]
     */
    public final String tagTypoGroups;
    /**
     * 타이포 장식을 구성하는 문장 중 글자 하나가 가지는 태그입니다. |
     * name + [그룹 아이디_글자 아이디]
     */
    public final String tagTypoChar;
    /**
     * 한 글자를 이루는 타이포 장식의 레이어입니다. (RGB 원색)
     * name + [그룹 아이디_글자 아이디_R or G or B]
     */
    public final String tagTypoLayer;
    public final AttributeAsset.Builder attributeAsset = new AttributeAsset.Builder(
            new Point2D(99.99, 99.99),
            (short) -1,
            false,
            "nev_text_");
    private final Set<String> addedDecorationTagSet = new HashSet<>();

    private final List<TextProperties> textProperties = new ArrayList<>();

    private final Map<Integer, Integer> groupIDPrevCharactersMap = new HashMap<>();
    private static final String VOID = "void";

    public VFXGeneratorTypos(CustomLevel level, int renderGroupID) {
        this.level = level;
        tagAllTypos = "nevMTP" + renderGroupID;
        tagTypoGroups = "nevMTP-G" + renderGroupID + ".";
        tagTypoChar = "nevMTP-TCT" + renderGroupID + ".";
        tagTypoLayer = "nevMTP-TL" + renderGroupID + ".";
    }

    public void in(int floor, @Nonnull String str) {
        in(floor, str, TextAppearance.nullAppearance());
    }

    public void in(int floor, @Nonnull String str, @Nonnull Point2D position) {
        in(floor, str, position, TextAppearance.nullAppearance());
    }

    public void in(int floor, @Nonnull String str, double angleOffset) {
        in(floor, str, TextAppearance.nullAppearance(), angleOffset);
    }

    public void in(int floor, @Nonnull String str, @Nonnull Point2D position, double angleOffset) {
        in(floor, str, position, TextAppearance.nullAppearance(), angleOffset);
    }

    public void in(int floor, @Nonnull String str, @Nonnull Point2D position, double rotation, double angleOffset) {
        in(floor, str, position, rotation, TextAppearance.nullAppearance(), angleOffset);
    }

    public void in(int floor, @Nonnull String str, @Nonnull Point2D position, double rotation, double scale, double angleOffset) {
        in(floor, str, position, rotation, scale, TextAppearance.nullAppearance(), angleOffset);
    }

    public void in(int floor, @Nonnull String str, @Nonnull TextAppearance appearance) {
        in(floor, str, Point2D.ORIGIN, appearance);
    }

    public void in(int floor, @Nonnull String str, @Nonnull Point2D position, @Nonnull TextAppearance appearance) {
        in(floor, str, position, appearance, 0);
    }

    public void in(int floor, @Nonnull String str, @Nonnull TextAppearance appearance, double angleOffset) {
        in(floor, str, Point2D.ORIGIN, appearance, angleOffset);
    }

    public void in(int floor, @Nonnull String str, @Nonnull Point2D position, @Nonnull TextAppearance appearance, double angleOffset) {
        in(floor, str, position, 0, appearance, angleOffset);
    }

    public void in(int floor, @Nonnull String str, @Nonnull Point2D position, double rotation, @Nonnull TextAppearance appearance, double angleOffset) {
        in(floor, str, position, rotation, 100, appearance, angleOffset);
    }

    public void in(int floor, @Nonnull String str, @Nonnull Point2D position, double rotation, double scale, @Nonnull TextAppearance appearance, double angleOffset) {
        in(floor, str, position, rotation, 0.4, scale, appearance, angleOffset);
    }

    public void in(int floor, @Nonnull String str, @Nonnull Point2D position, double rotation, double spacingX, double scale, @Nonnull TextAppearance appearance, double angleOffset) {
        in(floor, str, position, rotation, spacingX, scale, HexColor.WHITE, appearance, angleOffset);
    }

    public void in(int floor, @Nonnull String str, @Nonnull Point2D position, double rotation, double spacingX, double scale, HexColor color, @Nonnull TextAppearance appearance, double angleOffset) {

        textProperties.add(new TextProperties(str, position, rotation, spacingX, scale, color));
        groupIDPrevCharactersMap.put(textProperties.size(), str.length());
        checkDecorations(floor, textProperties.size(), str);
        in(floor, textProperties.size(), appearance, angleOffset);
    }


    private void in(int floor, int groupID, TextAppearance appearance, double angleOffset) {

        TextProperties properties = getTextProperties(groupID);
        changeText(floor, groupID, properties.text());

        for (int i = 0; i < properties.text().length(); i++) {

            appearance.in(level, floor,
                    getCharPositionOffset(groupID, i).edit()
                            .rotate(properties.position(), properties.rotation())
                            .build(),
                    new Point2D(properties.scale(), properties.scale()),
                    properties.color(),
                    Tag.of(getCharTag(groupID, i + 1)),
                    angleOffset + level.getAngleOffsetByTimeSec(floor, appearance.getDelaySec() * i));

        }

        refreshPosition(groupID, appearance.getDisplacement());
    }

    public void out(int floor, int groupID, TextAppearance appearance, double angleOffset) {

        TextProperties properties = getTextProperties(groupID);

        for (int i = 0; i < properties.text().length(); i++) {

            appearance.out(level, floor,
                    getCharPositionOffset(groupID, i).edit()
                            .rotate(properties.position(), properties.rotation())
                            .build(),
                    new Point2D(properties.scale(), properties.scale()),
                    properties.color(),
                    Tag.of(getCharTag(groupID, i + 1)),
                    angleOffset + level.getAngleOffsetByTimeSec(floor, appearance.getDelaySec() * i)
            );
        }
        refreshPosition(groupID, appearance.getDisplacement());

    }

    private void refreshPosition(int groupID, Point2D displacement) {
        TextProperties properties = getTextProperties(groupID);
        setTextProperties(groupID,
                properties.edit()
                        .setPosition(properties.position().add(displacement))
                        .build()
        );
    }


    private void setTextProperties(int groupID, TextProperties properties) {
        textProperties.set(groupID - 1, properties);
    }

    private void setTextString(int groupID, String str) {
        TextProperties properties = getTextProperties(groupID);
        int prevCharacters = groupIDPrevCharactersMap.getOrDefault(groupID, 0);
        if (prevCharacters != str.length()) {
            throw new IllegalArgumentException("text length not same");
        }
        setTextProperties(groupID, properties.edit().setText(str).build());
    }

    public TextProperties getTextProperties(int groupID) {
        return textProperties.get(groupID - 1);
    }

    public void changeText(int floor, int groupID, String str) {
        changeText(floor, groupID, str, 0);
    }

    public void changeText(int floor, int groupID, String str, double angleOffset) {
        AttributeAsset curAttributeAsset = attributeAsset.build();

        checkDecorations(floor, groupID, str);

        setTextString(groupID, str);

        for (int i = 0; i < str.length(); i++) {
            String characterTag = getCharTag(groupID, i + 1);
            char character = str.charAt(i);

            String imageName = curAttributeAsset.decorationImagePrefix();

            if (Character.isLowerCase(character)) {
                imageName += "lower_";
            }
            if (Character.isUpperCase(character)) {
                imageName += "upper_";
            }

            level.createEvent(floor, new MoveDecorations.Builder()
                    .setTag(Tag.of(characterTag))
                    .setDuration(0d)
                    .setImage(new ImageFile(imageName + (character == ' ' ? VOID : character) + ".png"))
                    .setAngleOffset(angleOffset)
                    .build()
            );
        }
    }

    public void moveText(int floor, int groupID, double duration, Point2D position, double scale, double spacingX, Ease ease, double angleOffset) {
        TextProperties properties = getTextProperties(groupID);
        setTextProperties(groupID, properties.edit()
                .setPosition(position == null ? properties.position() : position)
                .setSpacingX(Double.isNaN(spacingX) ? properties.spacingX() : spacingX)
                .setScale(Double.isNaN(scale) ? properties.scale() : scale)
                .build()
        );
        for (int i = 0; i < properties.text().length(); i++) {
            level.createEvent(floor, new MoveDecorations.Builder()
                    .setTag(Tag.of(getCharTag(groupID, i + 1)))
                    .setDuration(duration)
                    .setAngleOffset(angleOffset)
                    .setPivotOffset(getCharPositionOffset(groupID, i))
                    .setEase(ease)
                    .build()
            );
        }
    }

    /**
     * 그룹별 태그 정의
     */
    private String getGroupTag(int groupID) {
        return tagTypoGroups + groupID;
    }

    /**
     * 그룹 내 글자별 태그 정의
     */
    private String getCharTag(int groupID, int charID) {
        return tagTypoChar + groupID + "_" + charID;
    }


    /**
     * 글자 하나의 위치 오프셋을 얻습니다
     */
    private Point2D getCharPositionOffset(int groupID, int index) {
        TextProperties properties = getTextProperties(groupID);
        String text = properties.text();
        double finalSpacing = properties.spacingX();
        double startX = -finalSpacing * (text.length() - 1) / 2.0;
        return new Point2D(startX + finalSpacing * index + properties.position().x(), properties.position().y());
    }

    /**
     * 장식이 가지는 태그를 확인하고 없을경우 장식을 추가합니다
     */
    private void checkDecorations(int floor, int groupID, String text) {
        AttributeAsset curAttributeAsset = attributeAsset.build();
        Point2D parallax = curAttributeAsset.defaultParallax();
        String groupTag = getGroupTag(groupID);

        for (int i = 1; i <= text.length(); i++) {

            String characterTag = getCharTag(groupID, i);
            short depth = curAttributeAsset.defaultDepth();


            if (!addedDecorationTagSet.contains(characterTag)) {

                level.createEvent(floor, new AddDecoration.Builder()
                        .setDecorationImage(new ImageFile(curAttributeAsset.decorationImagePrefix() + VOID + ".png"))
                        .setTag(Tag.of(tagAllTypos, groupTag, characterTag))
                        .setOpacity(0d)
                        .setColor(HexColor.WHITE)
                        .setParallax(parallax)
                        .setBlendMode(BlendMode.LINEAR_DODGE)
                        .setScale(Point2D.ORIGIN)
                        .setDepth(depth)
                        .setLockRotation(curAttributeAsset.lockToCamera())
                        .setLockScale(curAttributeAsset.lockToCamera())
                        .build()
                );

                addedDecorationTagSet.add(characterTag);
            }

        }
    }


}

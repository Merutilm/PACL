package kr.merutilm.pacl.data;

import kr.merutilm.base.exception.UnsupportedVersionException;
import kr.merutilm.base.functions.StringContentsReader;
import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.selectable.Selectable;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.base.util.ConsoleUtils;
import kr.merutilm.base.util.TaskManager;
import kr.merutilm.pacl.al.event.events.Event;
import kr.merutilm.pacl.al.event.events.action.*;
import kr.merutilm.pacl.al.event.events.decoration.*;
import kr.merutilm.pacl.al.event.selectable.*;
import kr.merutilm.pacl.al.event.selectable.Font;
import kr.merutilm.pacl.al.event.struct.RelativeTile;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import static kr.merutilm.pacl.data.FileIO.StrData.Options.*;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.*;

final class FileIO {
    private FileIO() {

    }

    static final String EXTENSION_STR = ".adofai";
    public static final int VERSION = 13;
    private static final int LOAD = 0;
    private static final int SAVE = 1;

    /**
     * 파일 선택
     *
     * @return 선택한 파일 / 미선택시 null
     */
    @Nullable
    private static File chooseFile(String path, int type) {

        File file = new File("");
        JFrame frame = new JFrame();
        Image image = Assets.getIcon();
        frame.setIconImage(image);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        if (path != null && path.endsWith(EXTENSION_STR)) {
            return new File(path);
        }


        JFileChooser fc = initFileChooser(path);

        if (type == LOAD && JFileChooser.APPROVE_OPTION == fc.showOpenDialog(frame)) {
            file = fc.getSelectedFile();

            if (!file.getPath().endsWith(EXTENSION_STR)) {
                file = new File(file.getPath() + EXTENSION_STR);
            }

            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, "The file doesn't exist. Try Again.");
                file = chooseFile(file.getParentFile().getAbsolutePath(), type);
            }
        }
        if (type == SAVE && JFileChooser.APPROVE_OPTION == fc.showSaveDialog(frame)) {

            file = fc.getSelectedFile();

            if (!file.getPath().endsWith(EXTENSION_STR)) {
                file = new File(file.getPath() + EXTENSION_STR);
            }

            if (file.exists()) {
                int r = JOptionPane.showConfirmDialog(null,
                        "The file already exists. Do you want to overwrite it?",
                        "Overwrite File?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );
                if (r == JOptionPane.CLOSED_OPTION || r == JOptionPane.NO_OPTION) {
                    file = chooseFile(file.getParentFile().getAbsolutePath(), type);
                }
            }
        }

        if (file == null || file.getPath().isEmpty()) {
            return null;
        }

        return file;
    }

    @Nonnull
    private static JFileChooser initFileChooser(String path) {
        JFileChooser fc = new JFileChooser(path);
        fc.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (pathname.isDirectory()) {
                    return true;
                }
                return pathname.getName().endsWith(EXTENSION_STR);
            }

            @Override
            public String getDescription() {
                return EXTENSION_STR;
            }
        });

        fc.setAcceptAllFileFilterUsed(false);
        fc.setMultiSelectionEnabled(false);
        return fc;
    }

    /**
     * 문자열 기반 데이터와 Event 간 상호 변환 시 사용하는 클래스
     */
    record StrData(String eventType,
                   LinkedHashMap<String, String> options) {

        private static final class Builder {

            private final String eventType;
            private LinkedHashMap<String, String> options = new LinkedHashMap<>();

            private Builder(String eventType) {
                this.eventType = eventType;
            }

            private Builder replaceOption(@Nonnull Map<String, String> options) {
                this.options = new LinkedHashMap<>(options);
                return this;
            }


            private Builder setOption(@Nonnull Options optionName, @Nullable Object optionValue) {
                return setOption(optionName.name, optionValue);
            }

            private Builder setOption(@Nonnull String optionName, @Nullable Object optionValue) {
                if (optionValue == null) {
                    options.remove(optionName);
                } else {
                    options.put(optionName, optionValue.toString());
                }
                return this;
            }

            private StrData build() {
                return new StrData(eventType, options);
            }
        }

        @Nullable
        private String getValue(@Nonnull String optionName) {
            return options.get(optionName);
        }

        @Nullable
        private Double getDoubleValue(@Nonnull String optionName) {
            String v = getValue(optionName);
            if (v == null) {
                return null;
            } else {
                return Double.parseDouble(v);
            }
        }

        @Nullable
        private Integer getIntValue(@Nonnull String optionName) {
            String v = getValue(optionName);
            if (v == null) {
                return null;
            } else {
                return (int) Double.parseDouble(v);
            }
        }

        @Nullable
        private Short getShortValue(@Nonnull String optionName) {
            String v = getValue(optionName);
            if (v == null) {
                return null;
            } else {
                return (short) Double.parseDouble(v);
            }
        }

        @Nullable
        private Boolean getBooleanValue(@Nonnull String optionName) {
            String v = getValue(optionName);
            if (v == null) {
                return null;
            } else {
                return parseToBool(v);
            }
        }

        private Point2D getPointValue(@Nonnull String optionName) {
            String v = getValue(optionName);
            if (v == null) {
                return null;
            } else {
                return Point2D.convert(v);
            }
        }

        private HexColor getColorValue(@Nonnull String optionName) {
            String v = getValue(optionName);
            if (v == null) {
                return null;
            } else {
                return HexColor.convert(v);
            }
        }

        @Nullable
        private String getValue(@Nonnull Options optionName) {
            return getValue(optionName.toString());
        }

        @Nullable
        private Double getDoubleValue(@Nonnull Options optionName) {
            return getDoubleValue(optionName.toString());
        }

        @Nullable
        private Integer getIntValue(@Nonnull Options optionName) {
            return getIntValue(optionName.toString());
        }

        @Nullable
        private Short getShortValue(@Nonnull Options optionName) {
            return getShortValue(optionName.toString());
        }

        @Nullable
        private Boolean getBooleanValue(@Nonnull Options optionName) {
            return getBooleanValue(optionName.toString());
        }

        @Nullable
        private Point2D getPointValue(@Nonnull Options optionName) {
            return getPointValue(optionName.toString());
        }

        @Nullable
        private HexColor getColorValue(@Nonnull Options optionName) {
            return getColorValue(optionName.toString());
        }

        @Nullable
        private Double getDuration() {
            return this.getDoubleValue(DURATION.name);
        }

        @Nullable
        private Ease getEase() {
            String s = this.getValue(EASE.name);
            return Ease.typeOf(s);
        }

        @Nullable
        private Double getAngleOffset() {
            return this.getDoubleValue(ANGLE_OFFSET.name);
        }

        @Nonnull
        private Tag getEventTag() {
            return Tag.convert(getValue(EVENT_TAG.name));
        }

        @Nonnull
        private Tag getTag() {
            return Tag.convert(getValue(TAG.name));
        }

        private boolean equalsType(Class<? extends Event> type) {
            return eventType.equals(type.getSimpleName());
        }

        private static StrData from(Event event) {

            return switch (event) {
                case AddDecoration e -> new Builder(e.eventType())
                        .setOption(VISIBLE, e.visible())
                        .setOption(LOCKED, e.locked())
                        .setOption(DECORATION_IMAGE, e.image())
                        .setOption(POSITION, e.position())
                        .setOption(RELATIVE_TO, e.relativeTo())
                        .setOption(PIVOT_OFFSET, e.pivotOffset())
                        .setOption(ROTATION, e.rotation())
                        .setOption(LOCK_ROTATION, e.lockRotation())
                        .setOption(SCALE, e.scale())
                        .setOption(LOCK_SCALE, e.lockScale())
                        .setOption(TILE, e.tile())
                        .setOption(COLOR, e.color())
                        .setOption(OPACITY, e.opacity())
                        .setOption(DEPTH, e.depth())
                        .setOption(PARALLAX, e.parallax())
                        .setOption(PARALLAX_OFFSET, e.parallaxOffset())
                        .setOption(TAG, e.tag())
                        .setOption(SMOOTHING, e.smoothing())
                        .setOption(BLEND_MODE, e.blendMode())
                        .setOption(MASKING_TYPE, e.maskingType())
                        .setOption(USE_MASKING_DEPTH, e.useMaskingDepth())
                        .setOption(MASKING_FRONT_DEPTH, e.maskingFrontDepth())
                        .setOption(MASKING_BACK_DEPTH, e.maskingBackDepth())
                        .setOption(FAIL_BOX, e.failBox())
                        .setOption(FAIL_TYPE, e.failType())
                        .setOption(FAIL_SCALE, e.failScale())
                        .setOption(FAIL_POSITION, e.failPosition())
                        .setOption(FAIL_ROTATION, e.failRotation())
                        .build();
                case AddText e -> new Builder(e.eventType())
                        .setOption(VISIBLE, e.visible())
                        .setOption(LOCKED, e.locked())
                        .setOption(DEC_TEXT, e.decText())
                        .setOption(FONT, e.font())
                        .setOption(POSITION, e.position())
                        .setOption(RELATIVE_TO, e.relativeTo())
                        .setOption(PIVOT_OFFSET, e.pivotOffset())
                        .setOption(ROTATION, e.rotation())
                        .setOption(LOCK_ROTATION, e.lockRotation())
                        .setOption(SCALE, e.scale())
                        .setOption(LOCK_SCALE, e.lockScale())
                        .setOption(COLOR, e.color())
                        .setOption(OPACITY, e.opacity())
                        .setOption(DEPTH, e.depth())
                        .setOption(PARALLAX, e.parallax())
                        .setOption(PARALLAX_OFFSET, e.parallaxOffset())
                        .setOption(TAG, e.tag())
                        .build();
                case AddObject e -> new Builder(e.eventType())
                        .setOption(VISIBLE, e.visible())
                        .setOption(LOCKED, e.locked())
                        .setOption(OBJECT_TYPE, e.objectType())
                        .setOption(PLANET_COLOR_TYPE, e.planetColorType())
                        .setOption(PLANET_COLOR, e.planetColor())
                        .setOption(PLANET_TAIL_COLOR, e.planetTailColor())
                        .setOption(TRACK_TYPE, e.trackType())
                        .setOption(TRACK_ANGLE, e.trackAngle())
                        .setOption(TRACK_COLOR, e.trackColor())
                        .setOption(TRACK_OPACITY, e.trackOpacity())
                        .setOption(TRACK_STYLE, e.trackStyle())
                        .setOption(TRACK_ICON, e.trackIcon())
                        .setOption(TRACK_ICON_ANGLE, e.trackIconAngle())
                        .setOption(TRACK_RED_SWIRL, e.trackRedSwirl())
                        .setOption(TRACK_GRAY_SET_SPEED_ICON, e.trackGraySetSpeedIcon())
                        .setOption(TRACK_SET_SPEED_ICON_BPM, e.trackSetSpeedIconBpm())
                        .setOption(TRACK_GLOW_ENABLED, e.trackGlowEnabled())
                        .setOption(TRACK_GLOW_COLOR, e.trackGlowColor())
                        .setOption(POSITION, e.position())
                        .setOption(RELATIVE_TO, e.relativeTo())
                        .setOption(PIVOT_OFFSET, e.pivotOffset())
                        .setOption(ROTATION, e.rotation())
                        .setOption(LOCK_ROTATION, e.lockRotation())
                        .setOption(SCALE, e.scale())
                        .setOption(LOCK_SCALE, e.lockScale())
                        .setOption(DEPTH, e.depth())
                        .setOption(PARALLAX, e.parallax())
                        .setOption(PARALLAX_OFFSET, e.parallaxOffset())
                        .setOption(TAG, e.tag())
                        .build();
                case UnknownDecorations e -> new Builder(e.eventType())
                        .replaceOption(e.options())
                        .setOption(VISIBLE, e.visible())
                        .setOption(LOCKED, e.locked())
                        .setOption(RELATIVE_TO, e.relativeTo())
                        .setOption(TAG, e.tag())
                        .build();
                case AnimateTrack e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(APPEAR_ANIMATION, e.appearAnimation())
                        .setOption(BEATS_AHEAD, e.beatsAhead())
                        .setOption(DISAPPEAR_ANIMATION, e.disappearAnimation())
                        .setOption(BEATS_BEHIND, e.beatsBehind())
                        .build();
                case AutoPlayTiles e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(ENABLED, e.enabled())
                        .setOption(SAFETY_TILES, e.safetyTiles())
                        .build();
                case Bloom e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(ENABLED, e.enabled())
                        .setOption(THRESHOLD, e.threshold())
                        .setOption(INTENSITY, e.intensity())
                        .setOption(COLOR, e.color())
                        .setOption(DURATION, e.duration())
                        .setOption(EASE, e.ease())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EVENT_TAG, e.eventTag())
                        .build();
                case Bookmark e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .build();
                case CheckPoint e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(TILE_OFFSET, e.tileOffset())
                        .build();
                case ColorTrack e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(TRACK_COLOR_TYPE, e.trackColorType())
                        .setOption(TRACK_COLOR, e.trackColor())
                        .setOption(SECONDARY_TRACK_COLOR, e.secondaryTrackColor())
                        .setOption(TRACK_COLOR_ANIM_DURATION, e.trackColorAnimDuration())
                        .setOption(TRACK_COLOR_PULSE, e.trackColorPulse())
                        .setOption(TRACK_PULSE_LENGTH, e.trackPulseLength())
                        .setOption(TRACK_STYLE, e.trackStyle())
                        .setOption(TRACK_TEXTURE, e.trackTexture())
                        .setOption(TRACK_TEXTURE_SCALE, e.trackTextureScale())
                        .setOption(TRACK_GLOW_INTENSITY, e.trackGlowIntensity())
                        .build();
                case CustomBackground e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(COLOR, e.backgroundColor())
                        .setOption(BG_IMAGE, e.image())
                        .setOption(IMAGE_COLOR, e.imageColor())
                        .setOption(PARALLAX, e.parallax())
                        .setOption(BG_DISPLAY_MODE, e.bgDisplayMode())
                        .setOption(SMOOTHING, e.imageSmoothing())
                        .setOption(LOCK_ROT, e.lockRot())
                        .setOption(LOOP_BG, e.loop())
                        .setOption(SCALING_RATIO, e.scalingRatio())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EVENT_TAG, e.eventTag())
                        .build();
                case EditorComment e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(COMMENT, e.comment())
                        .build();
                case Flash e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(DURATION, e.duration())
                        .setOption(PLANE, e.plane())
                        .setOption(START_COLOR, e.startColor())
                        .setOption(START_OPACITY, e.startOpacity())
                        .setOption(END_COLOR, e.endColor())
                        .setOption(END_OPACITY, e.endOpacity())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EASE, e.ease())
                        .setOption(EVENT_TAG, e.eventTag())
                        .build();
                case HallOfMirrors e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(ENABLED, e.enabled())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EVENT_TAG, e.eventTag())
                        .build();
                case Hide e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(HIDE_JUDGMENT, e.hideJudgment())
                        .setOption(HIDE_ICON, e.hideTileIcon())
                        .build();
                case Hold e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(DURATION, e.duration())
                        .setOption(DISTANCE_MULTIPLIER, e.distanceMultiplier())
                        .setOption(LANDING_ANIMATION, e.landingAnimation())
                        .build();
                case MoveCamera e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(DURATION, e.duration())
                        .setOption(RELATIVE_TO, e.relativeTo())
                        .setOption(POSITION, e.position())
                        .setOption(ROTATION, e.rotation())
                        .setOption(ZOOM, e.zoom())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EASE, e.ease())
                        .setOption(DO_NOT_DISABLE, e.doNotDisable())
                        .setOption(MIN_VFX_ONLY, e.minVfxOnly())
                        .setOption(EVENT_TAG, e.eventTag())
                        .build();
                case MoveDecorations e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(DURATION, e.duration())
                        .setOption(TAG, e.tag())
                        .setOption(VISIBLE, e.visible())
                        .setOption(RELATIVE_TO, e.relativeTo())
                        .setOption(DECORATION_IMAGE, e.image())
                        .setOption(POSITION_OFFSET, e.positionOffset())
                        .setOption(PIVOT_OFFSET, e.pivotOffset())
                        .setOption(ROTATION_OFFSET, e.rotationOffset())
                        .setOption(SCALE, e.scale())
                        .setOption(COLOR, e.color())
                        .setOption(OPACITY, e.opacity())
                        .setOption(DEPTH, e.depth())
                        .setOption(PARALLAX, e.parallax())
                        .setOption(PARALLAX_OFFSET, e.parallaxOffset())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EASE, e.ease())
                        .setOption(EVENT_TAG, e.eventTag())
                        .setOption(MASKING_TYPE, e.maskingType())
                        .setOption(USE_MASKING_DEPTH, e.useMaskingDepth())
                        .setOption(MASKING_FRONT_DEPTH, e.maskingFrontDepth())
                        .setOption(MASKING_BACK_DEPTH, e.maskingBackDepth())
                        .build();
                case MoveTrack e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(START_TILE, e.startTile())
                        .setOption(END_TILE, e.endTile())
                        .setOption(GAP, e.gap())
                        .setOption(DURATION, e.duration())
                        .setOption(POSITION_OFFSET, e.positionOffset())
                        .setOption(ROTATION_OFFSET, e.rotationOffset())
                        .setOption(SCALE, e.scale())
                        .setOption(OPACITY, e.opacity())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EASE, e.ease())
                        .setOption(MAX_VFX_ONLY, e.maxVfxOnly())
                        .setOption(EVENT_TAG, e.eventTag())
                        .build();
                case MultiPlanet e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(PLANETS, e.planets())
                        .build();
                case Pause e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(DURATION, e.duration())
                        .setOption(COUNTDOWN_TICKS, e.countdownTicks())
                        .setOption(ANGLE_CORRECTION_DIR, e.angleCorrectionDir())
                        .build();
                case PlaySound e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(HIT_SOUND, e.hitSound())
                        .setOption(HIT_SOUND_VOLUME, e.volume())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EVENT_TAG, e.eventTag())
                        .build();
                case PositionTrack e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(POSITION_OFFSET, e.positionOffset())
                        .setOption(RELATIVE_TO, e.relativeTo())
                        .setOption(ROTATION, e.rotation())
                        .setOption(SCALE, e.scale())
                        .setOption(OPACITY, e.opacity())
                        .setOption(JUST_THIS_TILE, e.justThisTile())
                        .setOption(EDITOR_ONLY, e.editorOnly())
                        .setOption(STICK_TO_FLOORS, e.stickToFloors())
                        .build();
                case RecolorTrack e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(START_TILE, e.startTile())
                        .setOption(END_TILE, e.endTile())
                        .setOption(GAP, e.gap())
                        .setOption(DURATION, e.duration())
                        .setOption(TRACK_COLOR_TYPE, e.trackColorType())
                        .setOption(TRACK_COLOR, e.trackColor())
                        .setOption(SECONDARY_TRACK_COLOR, e.secondaryTrackColor())
                        .setOption(TRACK_COLOR_ANIM_DURATION, e.trackColorAnimDuration())
                        .setOption(TRACK_COLOR_PULSE, e.trackColorPulse())
                        .setOption(TRACK_PULSE_LENGTH, e.trackPulseLength())
                        .setOption(TRACK_STYLE, e.trackStyle())
                        .setOption(TRACK_GLOW_INTENSITY, e.trackGlowIntensity())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EASE, e.ease())
                        .setOption(EVENT_TAG, e.eventTag())
                        .build();
                case RepeatEvents e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(REPETITION, e.repetitions())
                        .setOption(INTERVAL, e.interval())
                        .setOption(TAG, e.tag())
                        .build();
                case ScaleMargin e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(SCALE, e.scale())
                        .build();
                case ScalePlanets e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(DURATION, e.duration())
                        .setOption(TARGET, e.targetPlanet())
                        .setOption(SCALE, e.scale())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EASE, e.ease())
                        .setOption(EVENT_TAG, e.eventTag())
                        .build();
                case ScaleRadius e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(SCALE, e.scale())
                        .build();
                case ScreenScroll e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(SCROLL, e.scroll())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EVENT_TAG, e.eventTag())
                        .build();
                case ScreenTile e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(TILE, e.tile())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EVENT_TAG, e.eventTag())
                        .build();
                case SetConditionalEvents e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(PERFECT_TAG, e.perfectTag())
                        .setOption(EARLY_PERFECT_TAG, e.earlyPerfectTag())
                        .setOption(LATE_PERFECT_TAG, e.latePerfectTag())
                        .setOption(VERY_EARLY_TAG, e.veryEarlyTag())
                        .setOption(VERY_LATE_TAG, e.veryLateTag())
                        .setOption(TOO_EARLY_TAG, e.tooEarlyTag())
                        .setOption(TOO_LATE_TAG, e.tooLateTag())
                        .setOption(LOSS_TAG, e.lossTag())
                        .build();
                case SetFilter e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(ENABLED, e.enabled())
                        .setOption(FILTER, e.filter())
                        .setOption(INTENSITY, e.intensity())
                        .setOption(DURATION, e.duration())
                        .setOption(EASE, e.ease())
                        .setOption(DISABLE_OTHERS, e.disableOthers())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EVENT_TAG, e.eventTag())
                        .build();
                case SetHitsound e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(GAME_SOUND, e.gameSound())
                        .setOption(HIT_SOUND, e.hitSound())
                        .setOption(HIT_SOUND_VOLUME, e.volume())
                        .build();
                case SetPlanetRotation e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(EASE, e.ease())
                        .setOption(EASE_PARTS, e.easeParts())
                        .setOption(EASE_PART_BEHAVIOR, e.easePartBehavior())
                        .build();
                case SetSpeed e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(SPEED_TYPE, e.speedType())
                        .setOption(BEATS_PER_MINUTE, e.bpm())
                        .setOption(MULTIPLIER, e.multiplier())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .build();
                case SetText e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(DEC_TEXT, e.decText())
                        .setOption(TAG, e.tag())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EVENT_TAG, e.eventTag())
                        .build();
                case ShakeScreen e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .setOption(DURATION, e.duration())
                        .setOption(STRENGTH, e.strength())
                        .setOption(INTENSITY, e.intensity())
                        .setOption(FADE_OUT, e.fadeOut())
                        .setOption(ANGLE_OFFSET, e.angleOffset())
                        .setOption(EVENT_TAG, e.eventTag())
                        .build();
                case Twirl e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .build();
                case UnknownActions e -> new Builder(e.eventType())
                        .setOption(ACTIVE, e.active())
                        .replaceOption(e.options())
                        .build();
                case null, default -> throw new IllegalArgumentException(event == null ? null : event.eventType());
            };
        }

        private Actions toEvent() {
            if (equalsType(AnimateTrack.class)) {
                return new AnimateTrack.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setAppearAnimation(TrackAppearAnimation.typeOf(getValue(APPEAR_ANIMATION)))
                        .setBeatsAhead(getDoubleValue(BEATS_AHEAD))
                        .setDisappearAnimation(TrackDisappearAnimation.typeOf(getValue(DISAPPEAR_ANIMATION)))
                        .setBeatsBehind(getDoubleValue(BEATS_BEHIND))
                        .build();
            } else if (equalsType(AutoPlayTiles.class)) {
                return new AutoPlayTiles.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setEnabled(getBooleanValue(ENABLED))
                        .setSafetyTiles(getBooleanValue(SAFETY_TILES))
                        .build();
            } else if (equalsType(Bloom.class)) {
                return new Bloom.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setEnabled(getBooleanValue(ENABLED))
                        .setThreshold(getDoubleValue(THRESHOLD))
                        .setIntensity(getDoubleValue(INTENSITY))
                        .setColor(getColorValue(COLOR))
                        .setDuration(getDuration())
                        .setEase(getEase())
                        .setAngleOffset(getAngleOffset())
                        .setEventTag(getEventTag())
                        .build();
            } else if (equalsType(Bookmark.class)) {
                return new Bookmark.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .build();
            } else if (equalsType(CheckPoint.class)) {
                return new CheckPoint.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setTileOffset(getIntValue(TILE_OFFSET))
                        .build();
            } else if (equalsType(ColorTrack.class)) {
                return new ColorTrack.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setTrackColorType(TrackColorType.typeOf(getValue(TRACK_COLOR_TYPE)))
                        .setTrackColor(getColorValue(TRACK_COLOR))
                        .setSecondaryTrackColor(getColorValue(SECONDARY_TRACK_COLOR))
                        .setTrackColorAnimDuration(getDoubleValue(TRACK_COLOR_ANIM_DURATION))
                        .setTrackColorPulse(TrackColorPulse.typeOf(getValue(TRACK_COLOR_PULSE)))
                        .setTrackPulseLength(getIntValue(TRACK_PULSE_LENGTH))
                        .setTrackStyle(TrackStyle.typeOf(getValue(TRACK_STYLE)))
                        .setTrackTexture(ImageFile.convert(getValue(TRACK_TEXTURE)))
                        .setTrackTextureScale(getDoubleValue(TRACK_TEXTURE_SCALE))
                        .setTrackGlowIntensity(getIntValue(TRACK_GLOW_INTENSITY))
                        .build();
            } else if (equalsType(CustomBackground.class)) {
                return new CustomBackground.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setBackgroundColor(getColorValue(COLOR))
                        .setImage(ImageFile.convert(getValue(BG_IMAGE)))
                        .setImageColor(getColorValue(IMAGE_COLOR))
                        .setParallax(getPointValue(PARALLAX))
                        .setBgDisplayMode(BGDisplayMode.typeOf(getValue(BG_DISPLAY_MODE)))
                        .setImageSmoothing(getBooleanValue(SMOOTHING))
                        .setLockRot(getBooleanValue(LOCK_ROT))
                        .setLoop(getBooleanValue(LOOP_BG))
                        .setScalingRatio(getIntValue(SCALING_RATIO))
                        .setAngleOffset(getDoubleValue(ANGLE_OFFSET))
                        .setEventTag(getEventTag())
                        .build();
            } else if (equalsType(EditorComment.class)) {
                return new EditorComment.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setComment(getValue(COMMENT))
                        .build();
            } else if (equalsType(Flash.class)) {
                return new Flash.Builder()
                        .setDuration(getDuration())
                        .setActive(getBooleanValue(ACTIVE))
                        .setPlane(Plane.typeOf(getValue(PLANE)))
                        .setStartColor(getColorValue(START_COLOR))
                        .setStartOpacity(getDoubleValue(START_OPACITY))
                        .setEndColor(getColorValue(END_COLOR))
                        .setEndOpacity(getDoubleValue(END_OPACITY))
                        .setAngleOffset(getAngleOffset())
                        .setEase(getEase())
                        .setEventTag(getEventTag())
                        .build();
            } else if (equalsType(HallOfMirrors.class)) {
                return new HallOfMirrors.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setEnabled(getBooleanValue(ENABLED))
                        .setAngleOffset(getAngleOffset())
                        .setEventTag(getEventTag())
                        .build();
            } else if (equalsType(Hide.class)) {
                return new Hide.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setHideJudgment(getBooleanValue(HIDE_JUDGMENT))
                        .setHideTileIcon(getBooleanValue(HIDE_ICON))
                        .build();
            } else if (equalsType(Hold.class)) {
                return new Hold.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setDuration(getIntValue(DURATION))
                        .setDistanceMultiplier(getDoubleValue(DISTANCE_MULTIPLIER))
                        .setLandingAnimation(getBooleanValue(LANDING_ANIMATION))
                        .build();
            } else if (equalsType(MoveCamera.class)) {
                return new MoveCamera.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setDuration(getDuration())
                        .setRelativeTo(RelativeTo.Camera.typeOf(getValue(RELATIVE_TO)))
                        .setPosition(getPointValue(POSITION))
                        .setRotation(getDoubleValue(ROTATION))
                        .setZoom(getDoubleValue(ZOOM))
                        .setAngleOffset(getAngleOffset())
                        .setEase(getEase())
                        .setDoNotDisable(getBooleanValue(DO_NOT_DISABLE))
                        .setMinVfxOnly(getBooleanValue(MIN_VFX_ONLY))
                        .setEventTag(getEventTag())
                        .build();
            } else if (equalsType(MoveDecorations.class)) {
                return new MoveDecorations.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setDuration(getDuration())
                        .setTag(getTag())
                        .setVisible(getBooleanValue(VISIBLE))
                        .setRelativeTo(RelativeTo.MoveDecoration.typeOf(getValue(RELATIVE_TO)))
                        .setImage(ImageFile.convert(getValue(DECORATION_IMAGE)))
                        .setPositionOffset(getPointValue(POSITION_OFFSET))
                        .setPivotOffset(getPointValue(PIVOT_OFFSET))
                        .setRotationOffset(getDoubleValue(ROTATION_OFFSET))
                        .setScale(getPointValue(SCALE))
                        .setColor(getColorValue(COLOR))
                        .setOpacity(getDoubleValue(OPACITY))
                        .setDepth(getShortValue(DEPTH))
                        .setParallax(getPointValue(PARALLAX))
                        .setParallaxOffset(getPointValue(PARALLAX_OFFSET))
                        .setAngleOffset(getAngleOffset())
                        .setEase(getEase())
                        .setEventTag(getEventTag())
                        .setMaskingType(MaskingType.typeOf(getValue(MASKING_TYPE)))
                        .setUseMaskingDepth(getBooleanValue(USE_MASKING_DEPTH))
                        .setMaskingFrontDepth(getShortValue(MASKING_FRONT_DEPTH))
                        .setMaskingBackDepth(getShortValue(MASKING_BACK_DEPTH))
                        .build();
            } else if (equalsType(MoveTrack.class)) {
                return new MoveTrack.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setStartTile(RelativeTile.convert(getValue(START_TILE)))
                        .setEndTile(RelativeTile.convert(getValue(END_TILE)))
                        .setGap(getIntValue(GAP))
                        .setDuration(getDuration())
                        .setPositionOffset(getPointValue(POSITION_OFFSET))
                        .setRotationOffset(getDoubleValue(ROTATION_OFFSET))
                        .setScale(getPointValue(SCALE))
                        .setOpacity(getDoubleValue(OPACITY))
                        .setAngleOffset(getAngleOffset())
                        .setEase(getEase())
                        .setMaxVfxOnly(getBooleanValue(MAX_VFX_ONLY))
                        .setEventTag(getEventTag())
                        .build();
            } else if (equalsType(MultiPlanet.class)) {
                return new MultiPlanet.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setPlanets(Planets.typeOf(getValue(PLANETS)))
                        .build();
            } else if (equalsType(Pause.class)) {
                return new Pause.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setDuration(getDuration())
                        .setCountdownTicks(getIntValue(COUNTDOWN_TICKS))
                        .setAngleCorrectionDir(getIntValue(ANGLE_CORRECTION_DIR))
                        .build();
            } else if (equalsType(PlaySound.class)) {
                return new PlaySound.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setHitSound(HitSound.typeOf(getValue(HIT_SOUND)))
                        .setVolume(getIntValue(Options.HIT_SOUND_VOLUME))
                        .setAngleOffset(getAngleOffset())
                        .setEventTag(getEventTag())
                        .build();
            } else if (equalsType(PositionTrack.class)) {
                return new PositionTrack.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setPositionOffset(getPointValue(POSITION_OFFSET))
                        .setRelativeTo(RelativeTile.convert(getValue(RELATIVE_TO)))
                        .setRotation(getDoubleValue(ROTATION))
                        .setScale(getDoubleValue(SCALE))
                        .setOpacity(getDoubleValue(OPACITY))
                        .setJustThisTile(getBooleanValue(JUST_THIS_TILE))
                        .setEditorOnly(getBooleanValue(EDITOR_ONLY))
                        .setStickToFloors(getBooleanValue(Options.STICK_TO_FLOORS))
                        .build();
            } else if (equalsType(RecolorTrack.class)) {
                return new RecolorTrack.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setStartTile(RelativeTile.convert(getValue(START_TILE)))
                        .setEndTile(RelativeTile.convert(getValue(END_TILE)))
                        .setGap(getIntValue(GAP))
                        .setDuration(getDuration())
                        .setTrackColorType(TrackColorType.typeOf(getValue(TRACK_COLOR_TYPE)))
                        .setTrackColor(getColorValue(TRACK_COLOR))
                        .setSecondaryTrackColor(getColorValue(SECONDARY_TRACK_COLOR))
                        .setTrackColorAnimDuration(getDoubleValue(TRACK_COLOR_ANIM_DURATION))
                        .setTrackColorPulse(TrackColorPulse.typeOf(getValue(TRACK_COLOR_PULSE)))
                        .setTrackPulseLength(getIntValue(TRACK_PULSE_LENGTH))
                        .setTrackStyle(TrackStyle.typeOf(getValue(TRACK_STYLE)))
                        .setTrackGlowIntensity(getIntValue(TRACK_GLOW_INTENSITY))
                        .setAngleOffset(getAngleOffset())
                        .setEase(getEase())
                        .setEventTag(getEventTag())
                        .build();
            } else if (equalsType(RepeatEvents.class)) {
                return new RepeatEvents.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setRepeatType(RepeatType.typeOf(getValue(REPEAT_TYPE)))
                        .setRepetitions(getIntValue(REPETITION))
                        .setFloorCount(getIntValue(FLOOR_COUNT))
                        .setInterval(getDoubleValue(INTERVAL))
                        .setExecuteOnCurrentFloor(getBooleanValue(EXECUTE_ON_CURRENT_FLOOR))
                        .setTag(getTag())
                        .build();
            } else if (equalsType(ScaleMargin.class)) {
                return new ScaleMargin.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setScale(getDoubleValue(SCALE))
                        .build();
            } else if (equalsType(ScalePlanets.class)) {
                return new ScalePlanets.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setDuration(getDuration())
                        .setTargetPlanet(TargetPlanet.typeOf(getValue(TARGET)))
                        .setScale(getDoubleValue(SCALE))
                        .setAngleOffset(getAngleOffset())
                        .setEase(getEase())
                        .setEventTag(getEventTag())
                        .build();
            } else if (equalsType(ScaleRadius.class)) {
                return new ScaleRadius.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setScale(getDoubleValue(SCALE))
                        .build();
            } else if (equalsType(ScreenScroll.class)) {
                return new ScreenScroll.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setScroll(getPointValue(SCROLL))
                        .setAngleOffset(getAngleOffset())
                        .setEventTag(getEventTag())
                        .build();
            } else if (equalsType(ScreenTile.class)) {
                return new ScreenTile.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setTile(getPointValue(TILE))
                        .setAngleOffset(getAngleOffset())
                        .setEventTag(getEventTag())
                        .build();
            } else if (equalsType(SetConditionalEvents.class)) {
                return new SetConditionalEvents.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setPerfectTag(getValue(PERFECT_TAG))
                        .setEarlyPerfectTag(getValue(EARLY_PERFECT_TAG))
                        .setLatePerfectTag(getValue(LATE_PERFECT_TAG))
                        .setVeryEarlyTag(getValue(VERY_EARLY_TAG))
                        .setVeryLateTag(getValue(VERY_LATE_TAG))
                        .setTooEarlyTag(getValue(TOO_EARLY_TAG))
                        .setTooLateTag(getValue(TOO_LATE_TAG))
                        .setLossTag(getValue(LOSS_TAG))
                        .build();
            } else if (equalsType(SetFilter.class)) {
                return new SetFilter.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setEnabled(getBooleanValue(ENABLED))
                        .setFilter(Filter.typeOf(getValue(FILTER)))
                        .setIntensity(getDoubleValue(INTENSITY))
                        .setDisableOthers(getBooleanValue(DISABLE_OTHERS))
                        .setDuration(getDuration())
                        .setEase(getEase())
                        .setAngleOffset(getAngleOffset())
                        .setEventTag(getEventTag())
                        .build();
            } else if (equalsType(SetHitsound.class)) {
                return new SetHitsound.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setGameSound(GameSound.typeOf(getValue(GAME_SOUND)))
                        .setHitSound(HitSound.typeOf(getValue(HIT_SOUND)))
                        .setVolume(getIntValue(Options.HIT_SOUND_VOLUME))
                        .build();
            } else if (equalsType(SetPlanetRotation.class)) {
                return new SetPlanetRotation.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setEase(getEase())
                        .setEaseParts(getIntValue(EASE_PARTS))
                        .setEasePartBehavior(EasePartBehavior.typeOf(getValue(EASE_PART_BEHAVIOR)))
                        .build();
            } else if (equalsType(SetSpeed.class)) {
                return new SetSpeed.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setSpeedType(SpeedType.typeOf(getValue(SPEED_TYPE)))
                        .setBpm(getDoubleValue(BEATS_PER_MINUTE))
                        .setMultiplier(getDoubleValue(MULTIPLIER))
                        .setAngleOffset(getDoubleValue(ANGLE_OFFSET))
                        .build();
            } else if (equalsType(SetText.class)) {
                return new SetText.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setDecText(getValue(DEC_TEXT))
                        .setTag(Tag.convert(getValue(TAG)))
                        .setAngleOffset(getAngleOffset())
                        .setEventTag(getEventTag())
                        .build();
            } else if (equalsType(ShakeScreen.class)) {
                return new ShakeScreen.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .setDuration(getDuration())
                        .setStrength(getDoubleValue(STRENGTH))
                        .setIntensity(getDoubleValue(INTENSITY))
                        .setFadeOut(getBooleanValue(FADE_OUT))
                        .setAngleOffset(getAngleOffset())
                        .setEventTag(getEventTag())
                        .build();
            } else if (equalsType(Twirl.class)) {
                return new Twirl.Builder()
                        .setActive(getBooleanValue(ACTIVE))
                        .build();
            } else return new UnknownActions.Builder(eventType())
                    .setActive(getBooleanValue(ACTIVE))
                    .replaceOptions(options())
                    .build();
        }

        private Decorations toDecorations() {
            if (equalsType(AddDecoration.class)) {
                return new AddDecoration.Builder()
                        .setVisible(getBooleanValue(VISIBLE))
                        .setLocked(getBooleanValue(LOCKED))
                        .setDecorationImage(ImageFile.convert(getValue(DECORATION_IMAGE)))
                        .setPosition(getPointValue(POSITION))
                        .setRelativeTo(RelativeTo.AddDecoration.typeOf(getValue(RELATIVE_TO)))
                        .setPivotOffset(getPointValue(PIVOT_OFFSET))
                        .setRotation(getDoubleValue(ROTATION))
                        .setLockRotation(getBooleanValue(LOCK_ROTATION))
                        .setScale(getPointValue(SCALE))
                        .setLockScale(getBooleanValue(LOCK_SCALE))
                        .setTile(getPointValue(TILE))
                        .setColor(getColorValue(COLOR))
                        .setOpacity(getDoubleValue(OPACITY))
                        .setDepth(getShortValue(DEPTH))
                        .setParallax(getPointValue(PARALLAX))
                        .setParallaxOffset(getPointValue(PARALLAX_OFFSET))
                        .setTag(getTag())
                        .setSmoothing(getBooleanValue(SMOOTHING))
                        .setBlendMode(BlendMode.typeOf(getValue(BLEND_MODE)))
                        .setMaskingType(MaskingType.typeOf(getValue(MASKING_TYPE)))
                        .setUseMaskingDepth(getBooleanValue(USE_MASKING_DEPTH))
                        .setMaskingFrontDepth(getShortValue(MASKING_FRONT_DEPTH))
                        .setMaskingBackDepth(getShortValue(MASKING_BACK_DEPTH))
                        .setFailBox(getBooleanValue(FAIL_BOX))
                        .setFailType(FailHitBox.typeOf(getValue(FAIL_TYPE)))
                        .setFailScale(getPointValue(FAIL_SCALE))
                        .setFailPosition(getPointValue(FAIL_POSITION))
                        .setFailRotation(getDoubleValue(FAIL_ROTATION))
                        .build();
            } else if (equalsType(AddText.class)) {
                return new AddText.Builder()
                        .setVisible(getBooleanValue(VISIBLE))
                        .setLocked(getBooleanValue(LOCKED))
                        .setDecText(getValue(DEC_TEXT))
                        .setFont(Font.typeOf(getValue(FONT)))
                        .setPosition(getPointValue(POSITION))
                        .setRelativeTo(RelativeTo.AddDecoration.typeOf(getValue(RELATIVE_TO)))
                        .setPivotOffset(getPointValue(PIVOT_OFFSET))
                        .setRotation(getDoubleValue(ROTATION))
                        .setLockRotation(getBooleanValue(LOCK_ROTATION))
                        .setScale(getPointValue(SCALE))
                        .setLockScale(getBooleanValue(LOCK_SCALE))
                        .setColor(getColorValue(COLOR))
                        .setOpacity(getDoubleValue(OPACITY))
                        .setDepth(getShortValue(DEPTH))
                        .setParallax(getPointValue(PARALLAX))
                        .setParallaxOffset(getPointValue(PARALLAX_OFFSET))
                        .setTag(getTag())
                        .build();
            } else if (equalsType(AddObject.class)) {
                return new AddObject.Builder()
                        .setVisible(getBooleanValue(VISIBLE))
                        .setLocked(getBooleanValue(LOCKED))
                        .setObjectType(ObjectType.typeOf(getValue(OBJECT_TYPE)))
                        .setPlanetColorType(PlanetColorType.typeOf(getValue(PLANET_COLOR_TYPE)))
                        .setPlanetColor(getColorValue(PLANET_COLOR))
                        .setPlanetTailColor(getColorValue(PLANET_TAIL_COLOR))
                        .setTrackType(TrackType.typeOf(getValue(TRACK_TYPE)))
                        .setTrackAngle(getDoubleValue(TRACK_ANGLE))
                        .setTrackColor(getColorValue(TRACK_COLOR))
                        .setTrackOpacity(getDoubleValue(TRACK_OPACITY))
                        .setTrackStyle(TrackStyle.typeOf(getValue(TRACK_STYLE)))
                        .setTrackIcon(TrackIcon.typeOf(getValue(TRACK_ICON)))
                        .setTrackIconAngle(getDoubleValue(TRACK_ICON_ANGLE))
                        .setTrackRedSwirl(getBooleanValue(TRACK_RED_SWIRL))
                        .setTrackGraySetSpeedIcon(getBooleanValue(TRACK_GRAY_SET_SPEED_ICON))
                        .setTrackSetSpeedIconBpm(getDoubleValue(TRACK_SET_SPEED_ICON_BPM))
                        .setTrackGlowEnabled(getBooleanValue(TRACK_GLOW_ENABLED))
                        .setTrackGlowColor(getColorValue(TRACK_GLOW_COLOR))
                        .setPosition(getPointValue(POSITION))
                        .setRelativeTo(RelativeTo.AddDecoration.typeOf(getValue(RELATIVE_TO)))
                        .setPivotOffset(getPointValue(PIVOT_OFFSET))
                        .setRotation(getDoubleValue(ROTATION))
                        .setLockRotation(getBooleanValue(LOCK_ROTATION))
                        .setScale(getPointValue(SCALE))
                        .setLockScale(getBooleanValue(LOCK_SCALE))
                        .setDepth(getShortValue(DEPTH))
                        .setParallax(getPointValue(PARALLAX))
                        .setParallaxOffset(getPointValue(PARALLAX_OFFSET))
                        .setTag(getTag())
                        .build();
            }
            return new UnknownDecorations.Builder(eventType())
                    .replaceOptions(options())
                    .setVisible(getBooleanValue(VISIBLE))
                    .setLocked(getBooleanValue(LOCKED))
                    .setRelativeTo(RelativeTo.AddDecoration.typeOf(getValue(RELATIVE_TO)))
                    .setTag(Tag.convert(getValue(TAG)))
                    .build();
        }

        public enum SettingName implements Selectable {
            VERSION("version"),
            ARTIST("artist"),
            SPECIAL_ARTIST_TYPE("specialArtistType"),
            ARTIST_PERMISSION("artistPermission"),
            SONG("song"),
            AUTHOR("author"),
            SEPARATE_COUNTDOWN_TIME("separateCountdownTime"),
            PREVIEW_IMAGE("previewImage"),
            PREVIEW_ICON("previewIcon"),
            PREVIEW_ICON_COLOR("previewIconColor"),
            PREVIEW_SONG_START("previewSongStart"),
            PREVIEW_SONG_DURATION("previewSongDuration"),
            SEIZURE("seizureWarning"),
            LEVEL_DESC("levelDesc"),
            LEVEL_TAGS("levelTags"),
            ARTIST_LINKS("artistLinks"),
            SPEED_TRIAL_AIM("speedTrialAim"),
            DIFF("difficulty"),
            REQUIRED_MODS("requiredMods"),
            SONG_FILE("songFilename"),
            BPM("bpm"),
            VOLUME("volume"),
            OFFSET("offset"),
            PITCH("pitch"),
            HIT_SOUND("hitsound"),
            HIT_SOUND_VOLUME("hitsoundVolume"),
            COUNTDOWN_TICKS("countdownTicks"),
            TRACK_COLOR_TYPE("trackColorType"),
            TRACK_COLOR("trackColor"),
            SECONDARY_COLOR("secondaryTrackColor"),
            TRACK_ANIM_DURATION("trackColorAnimDuration"),
            TRACK_PULSE("trackColorPulse"),
            TRACK_PULSE_LENGTH("trackPulseLength"),
            TRACK_STYLE("trackStyle"),
            TRACK_TEXTURE("trackTexture"),
            TRACK_TEXTURE_SCALE("trackTextureScale"),
            TRACK_GLOW_INTENSITY("trackGlowIntensity"),
            TRACK_ANIMATION("trackAnimation"),
            BEATS_AHEAD("beatsAhead"),
            TRACK_DIS_ANIMATION("trackDisappearAnimation"),
            BEATS_BEHIND("beatsBehind"),
            BACKGROUND_COLOR("backgroundColor"),
            SHOW_DEFAULT_BG("showDefaultBGIfNoImage"),
            SHOW_DEFAULT_BG_TILE("showDefaultBGTile"),
            DEFAULT_BG_TILE_COLOR("defaultBGTileColor"),
            DEFAULT_BG_SHAPE_TYPE("defaultBGShapeType"),
            DEFAULT_BG_SHAPE_COLOR("defaultBGShapeColor"),
            BG_IMAGE("bgImage"),
            BG_IMAGE_COLOR("bgImageColor"),
            PARALLAX("parallax"),
            BG_DISPLAY_MODE("bgDisplayMode"),
            IMAGE_SMOOTHING("imageSmoothing"),
            LOCK_ROTATION("lockRot"),
            LOOP_BG("loopBG"),
            SCALING_RATIO("scalingRatio"),
            RELATIVE_TO("relativeTo"),
            POSITION("position"),
            ROTATION("rotation"),
            ZOOM("zoom"),
            PULSE_ON_FLOOR("pulseOnFloor"),
            BG_VIDEO("bgVideo"),
            LOOP_VIDEO("loopVideo"),
            VIDEO_OFFSET("vidOffset"),
            ICON_OUTLINE("floorIconOutlines"),
            STICK_TO_FLOORS("stickToFloors"),
            PLANET_EASE("planetEase"),
            PLANET_EASE_PARTS("planetEaseParts"),
            PLANET_EASE_BEHAVIOUR("planetEasePartBehavior"),
            DEFAULT_TEXT_COLOR("defaultTextColor"),
            DEFAULT_TEXT_SHADOW_COLOR("defaultTextShadowColor"),
            CONGRATS_TEXT("congratsText"),
            PERFECT_TEXT("perfectText"),
            L_FLASH("legacyFlash"),
            L_CAMERA("legacyCamRelativeTo"),
            L_TILE("legacySpriteTiles");


            private final String name;

            @Override
            public String toString() {
                return name;
            }

            SettingName(String name) {
                this.name = name;
            }

            public static SettingName typeOf(String name) {
                return Arrays.stream(SettingName.values())
                        .filter(v -> v.name.equals(name))
                        .findAny()
                        .orElse(null);
            }
        }

        public enum Options implements Selectable {
            ACTIVE("active"),
            ANGLE_CORRECTION_DIR("angleCorrectionDir"),
            ANGLE_OFFSET("angleOffset"),
            APPEAR_ANIMATION("trackAnimation"),
            BEATS_AHEAD("beatsAhead"),
            BEATS_BEHIND("beatsBehind"),
            BEATS_PER_MINUTE("beatsPerMinute"),
            BG_DISPLAY_MODE("bgDisplayMode"),
            BG_IMAGE("bgImage"),
            BLEND_MODE("blendMode"),
            COLOR("color"),
            COMMENT("comment"),
            COUNTDOWN_TICKS("countdownTicks"),
            COMPONENTS("components"),
            DEC_TEXT("decText"),
            DECORATION_IMAGE("decorationImage"),
            DEPTH("depth"),
            DISABLE_OTHERS("disableOthers"),
            DISAPPEAR_ANIMATION("trackDisappearAnimation"),
            DISTANCE_MULTIPLIER("distanceMultiplier"),
            DO_NOT_DISABLE("dontDisable"),
            DURATION("duration"),
            EARLY_PERFECT_TAG("earlyPerfectTag"),
            EASE("ease"),
            EASE_PARTS("easeParts"),
            EASE_PART_BEHAVIOR("easePartBehavior"),
            EDITOR_ONLY("editorOnly"),
            ENABLED("enabled"),
            END_COLOR("endColor"),
            END_OPACITY("endOpacity"),
            END_TILE("endTile"),
            EVENT_TAG("eventTag"),
            EXECUTE_ON_CURRENT_FLOOR("executeOnCurrentFloor"),
            FADE_OUT("fadeOut"),
            FAIL_BOX("failHitbox"),
            FAIL_POSITION("failHitboxOffset"),
            FAIL_ROTATION("failHitboxRotation"),
            FAIL_SCALE("failHitboxScale"),
            FAIL_TYPE("failHitboxType"),
            FILTER("filter"),
            FLOOR_COUNT("floorCount"),
            FONT("font"),
            GAP("gapLength"),
            GAME_SOUND("gameSound"),
            HIDE_ICON("hideTileIcon"),
            HIDE_JUDGMENT("hideJudgment"),
            HIT_SOUND("hitsound"),
            HIT_SOUND_VOLUME("hitsoundVolume"),
            IMAGE_COLOR("imageColor"),
            INTERVAL("interval"),
            INTENSITY("intensity"),
            JUST_THIS_TILE("justThisTile"),
            LANDING_ANIMATION("landingAnimation"),
            LATE_PERFECT_TAG("latePerfectTag"),
            LOCK_ROT("lockRot"),
            LOCK_ROTATION("lockRotation"),
            LOCK_SCALE("lockScale"),
            LOCKED("locked"),
            LOOP_BG("loopBG"),
            LOSS_TAG("lossTag"),
            MASKING_BACK_DEPTH("maskingBackDepth"),
            MASKING_FRONT_DEPTH("maskingFrontDepth"),
            MASKING_TYPE("maskingType"),
            MAX_VFX_ONLY("maxVfxOnly"),
            MIN_VFX_ONLY("minVfxOnly"),
            MULTIPLIER("bpmMultiplier"),
            OPACITY("opacity"),
            PARALLAX("parallax"),
            PARALLAX_OFFSET("parallaxOffset"),
            PERFECT_TAG("perfectTag"),
            PIVOT_OFFSET("pivotOffset"),
            PLANE("plane"),
            PLANET_COLOR("planetColor"),
            PLANET_COLOR_TYPE("planetColorType"),
            PLANET_TAIL_COLOR("planetTailColor"),
            PLANETS("planets"),
            POSITION("position"),
            POSITION_OFFSET("positionOffset"),
            RELATIVE_TO("relativeTo"),
            REPEAT_TYPE("repeatType"),
            REPETITION("repetitions"),
            ROTATION("rotation"),
            ROTATION_OFFSET("rotationOffset"),
            SAFETY_TILES("safetyTiles"),
            SCALE("scale"),
            SCALING_RATIO("scalingRatio"),
            SCROLL("scroll"),
            SECONDARY_TRACK_COLOR("secondaryTrackColor"),
            SMOOTHING("imageSmoothing"),
            SPEED_TYPE("speedType"),
            START_COLOR("startColor"),
            START_OPACITY("startOpacity"),
            START_TILE("startTile"),
            STICK_TO_FLOORS("stickToFloors"),
            STRENGTH("strength"),
            TAG("tag"),
            TARGET("targetPlanet"),
            THRESHOLD("threshold"),
            TILE("tile"),
            TILE_OFFSET("tileOffset"),
            TOO_EARLY_TAG("tooEarlyTag"),
            TOO_LATE_TAG("tooLateTag"),

            TRACK_ANGLE("trackAngle"),
            TRACK_COLOR("trackColor"),
            TRACK_COLOR_ANIM_DURATION("trackColorAnimDuration"),
            TRACK_COLOR_PULSE("trackColorPulse"),
            TRACK_COLOR_TYPE("trackColorType"),
            TRACK_GLOW_COLOR("trackGlowColor"),
            TRACK_GLOW_ENABLED("trackGlowEnabled"),
            TRACK_GLOW_INTENSITY("trackGlowIntensity"),
            TRACK_GRAY_SET_SPEED_ICON("trackGraySetSpeedIcon"),
            TRACK_ICON("trackIcon"),
            TRACK_ICON_ANGLE("trackIconAngle"),
            TRACK_OPACITY("trackOpacity"),
            TRACK_PULSE_LENGTH("trackPulseLength"),
            TRACK_RED_SWIRL("trackRedSwirl"),
            TRACK_SET_SPEED_ICON_BPM("trackSetSpeedIconBpm"),
            TRACK_STYLE("trackStyle"),
            TRACK_TEXTURE("trackTexture"),
            TRACK_TEXTURE_SCALE("trackTextureScale"),
            TRACK_TYPE("trackType"),
            USE_MASKING_DEPTH("useMaskingDepth"),
            VERY_EARLY_TAG("veryEarlyTag"),
            VERY_LATE_TAG("veryLateTag"),
            VISIBLE("visible"),
            ZOOM("zoom"),
            OBJECT_TYPE("objectType");
            private final String name;

            @Override
            public String toString() {
                return name;
            }

            Options(String name) {
                this.name = name;
            }
        }
    }


    private static final String REGEX_COMMA = "[: ,]";
    private static final String REGEX_BRACE = "[: }]";
    private static final String DOUBLE_TAB = "\t\t";
    private static final String COMMA_DELIMITER = ", ";
    private static final String COMMA_LINE = ",\n";
    private static final int[] PARSED = new int[1];

    /**
     * <p>파일 읽기</p>
     * READ JSON FILE
     *
     * @param editor target Editor
     * @return Custom level in selected file.
     * Returns <strong>null</strong> if the error occurs while reading the file.
     */
    @Nullable
    static CustomLevel readFile(@Nullable PACL editor, @Nonnull File inputFile) {

        CustomLevel level = CustomLevel.emptyLevel();

        try (BufferedReader input = new BufferedReader(new FileReader(inputFile))) {

            //Read Path
            String lineStr;

            while (true) {
                lineStr = input.readLine();
                if (lineStr == null) {
                    if (editor != null) {
                        editor.setProcessType(Process.ERROR);
                    }
                    JOptionPane.showMessageDialog(null, "Invalid File! (is File Empty?)");
                    return null;
                }

                StringContentsReader scr = new StringContentsReader(lineStr);

                if (lineStr.contains("\"")) {

                    scr.getContentOfQuotes();
                    scr.skip();

                    String[] contents = scr.getContentOfBracketsToArray();
                    for (String s : contents) {
                        double angle = Double.parseDouble(s);
                        while (angle >= 360 && angle != 999) {
                            angle -= 360;
                        }
                        while (angle < 0) {
                            angle += 360;
                        }
                        level.createAngle(angle);
                    }

                    break;
                }
            }

            lineStr = readLine(input, 2);

            //Read Settings
            String content;

            while (!lineStr.contains("}")) {

                if (lineStr.contains("\"")) {
                    StringContentsReader scr = new StringContentsReader(lineStr);
                    String settingTypeName = scr.getContentOfQuotes();

                    StrData.SettingName settingType = StrData.SettingName.typeOf(settingTypeName);
                    if (settingType == null) {
                        System.err.println("Not found Setting name : " + settingTypeName);
                    } else {
                        scr.skip();
                        String settingValue;

                        if (scr.get().contains("\"")) {
                            content = scr.getContentOfQuotes();

                            if (content.isEmpty()) {
                                settingValue = "";
                            } else {
                                settingValue = content;
                            }
                        } else {
                            if (scr.get().contains("[")) {
                                settingValue = scr.getContentOfBrackets();
                            } else { // boolean or value
                                scr.skip();
                                settingValue = scr.get().replaceAll(REGEX_COMMA, "");
                            }
                        }
                        setSettingValue(level, settingType, settingValue);
                    }
                }
                lineStr = input.readLine();
            }
            //check Settings
            Settings currSettings = level.getSettings();

            if (VERSION > currSettings.version()) {
                if (editor != null) {
                    editor.setProcessType(Process.ERROR);
                }
                String message = "Current File version is not latest version!! (It can solve by re-saving this file)" +
                                 "\ncurrent version : " + currSettings.version() +
                                 "\nrequired version : " + VERSION;


                JOptionPane.showMessageDialog(null, message);
                throw new UnsupportedVersionException(message);
            }

            //Read Actions

            lineStr = readLine(input, 3);

            while (!lineStr.replace(" ", "").contains("]") || lineStr.replace(" ", "").length() > 10) {
                if (!lineStr.isBlank()) {
                    Actions actions = getEventDataFromString(lineStr).toEvent();
                    int floor = AdvancedMath.restrict(0, level.getLength(), getEventFloorFromString(lineStr));
                    level.createEvent(floor, actions);
                }
                lineStr = input.readLine();
            }

            //Decoration
            lineStr = readLine(input, 3);

            while (!lineStr.replace("\t", "").contains("]") || lineStr.replace("\t", "").length() > 10) {
                if (!lineStr.isBlank()) {
                    Decorations decorations = getEventDataFromString(lineStr).toDecorations();
                    int floor = AdvancedMath.restrict(0, level.getLength(), getEventFloorFromString(lineStr));
                    level.createEvent(floor, decorations);
                }
                lineStr = input.readLine();
            }

            if (editor != null) {
                editor.setProcessType(Process.LOAD);
            }
            return level;

        } catch (NumberFormatException e) {
            ConsoleUtils.logError(e);
            if (editor != null) {
                editor.setProcessType(Process.ERROR);
            }
            JOptionPane.showMessageDialog(null, "Change PathData to AngleData.");
        } catch (IOException | RuntimeException e) {
            ConsoleUtils.logError(e);
            if (editor != null) {
                editor.setProcessType(Process.ERROR);
            }
            JOptionPane.showMessageDialog(null, "An error occurred while opening the file.");
        }
        return null;
    }

    private static void setSettingValue(@Nonnull CustomLevel customLevel, @Nonnull StrData.SettingName settingType, @Nonnull String settingValue) {
        Settings.Builder settings = customLevel.getSettings().edit();
        switch (settingType) {
            case BPM -> settings.setBpm(Double.parseDouble(settingValue));
            case DIFF -> settings.setDifficulty((int) Double.parseDouble(settingValue));
            case SONG -> settings.setSong(settingValue);
            case ZOOM -> settings.setZoom(Double.parseDouble(settingValue));
            case PITCH -> settings.setPitch((int) Double.parseDouble(settingValue));
            case ARTIST -> settings.setArtist(settingValue);
            case AUTHOR -> settings.setAuthor(settingValue);
            case L_TILE -> settings.setLegacySpriteTiles(parseToBool(settingValue));
            case OFFSET -> settings.setOffset((int) Double.parseDouble(settingValue));
            case VOLUME -> settings.setVolume((int) Double.parseDouble(settingValue));
            case L_FLASH -> settings.setLegacyFlash(parseToBool(settingValue));
            case LOOP_BG -> settings.setLoopBG(parseToBool(settingValue));
            case SEIZURE -> settings.setSeizureWarning(parseToBool(settingValue));
            case VERSION -> settings.setVersion((int) Double.parseDouble(settingValue));
            case BG_IMAGE -> settings.setBgImage(ImageFile.convert(settingValue));
            case BG_VIDEO -> settings.setBgVideo(ImageFile.convert(settingValue));
            case L_CAMERA -> settings.setLegacyCamRelativeTo(parseToBool(settingValue));
            case PARALLAX -> settings.setParallax(Point2D.convert(settingValue));
            case POSITION -> settings.setPosition(Point2D.convert(settingValue));
            case ROTATION -> settings.setRotation(Double.parseDouble(settingValue));
            case HIT_SOUND -> settings.setHitSound(HitSound.typeOf(settingValue));
            case SONG_FILE -> settings.setSongFilename(settingValue);
            case LEVEL_DESC -> settings.setLevelDesc(settingValue);
            case LEVEL_TAGS -> settings.setLevelTags(settingValue);
            case LOOP_VIDEO -> settings.setLoopVideo(parseToBool(settingValue));
            case BEATS_AHEAD -> settings.setBeatsAhead(Double.parseDouble(settingValue));
            case PLANET_EASE -> settings.setPlanetEase(Ease.typeOf(settingValue));
            case RELATIVE_TO -> settings.setRelativeTo(RelativeTo.Camera.typeOf(settingValue));
            case TRACK_COLOR -> settings.setTrackColor(HexColor.convert(settingValue));
            case TRACK_PULSE -> settings.setTrackColorPulse(TrackColorPulse.typeOf(settingValue));
            case TRACK_STYLE -> settings.setTrackStyle(TrackStyle.typeOf(settingValue));
            case ARTIST_LINKS -> settings.setArtistLinks(settingValue);
            case SPEED_TRIAL_AIM -> settings.setSpeedTrialAim(Double.parseDouble(settingValue));
            case BEATS_BEHIND -> settings.setBeatsBehind(Double.parseDouble(settingValue));
            case ICON_OUTLINE -> settings.setIconOutLines(parseToBool(settingValue));
            case PREVIEW_ICON -> settings.setPreviewIcon(ImageFile.convert(settingValue));
            case VIDEO_OFFSET -> settings.setVideoOffset((int) Double.parseDouble(settingValue));
            case LOCK_ROTATION -> settings.setLockRot(parseToBool(settingValue));
            case SEPARATE_COUNTDOWN_TIME -> settings.setSeparateCountdownTime(parseToBool(settingValue));
            case PREVIEW_IMAGE -> settings.setPreviewImage(ImageFile.convert(settingValue));
            case REQUIRED_MODS -> settings.setRequireMods(settingValue);
            case BG_IMAGE_COLOR -> settings.setBgImageColor(HexColor.convert(settingValue));
            case PULSE_ON_FLOOR -> settings.setPulseOnFloor(parseToBool(settingValue));
            case STICK_TO_FLOORS -> settings.setStickToFloors(parseToBool(settingValue));
            case BG_DISPLAY_MODE -> settings.setBgDisplayMode(BGDisplayMode.typeOf(settingValue));
            case COUNTDOWN_TICKS -> settings.setCountdownTicks((int) Double.parseDouble(settingValue));
            case SECONDARY_COLOR -> settings.setSecondaryTrackColor(HexColor.convert(settingValue));
            case SHOW_DEFAULT_BG -> settings.setShowDefaultBGIfNoImage(parseToBool(settingValue));
            case TRACK_ANIMATION -> settings.setTrackAnimation(TrackAppearAnimation.typeOf(settingValue));
            case BACKGROUND_COLOR -> settings.setBackgroundColor(HexColor.convert(settingValue));
            case HIT_SOUND_VOLUME -> settings.setHitSoundVolume((int) Double.parseDouble(settingValue));
            case TRACK_COLOR_TYPE -> settings.setTrackColorType(TrackColorType.typeOf(settingValue));
            case ARTIST_PERMISSION -> settings.setArtistPermission(settingValue);
            case PLANET_EASE_PARTS -> settings.setPlanetEaseParts((int) Double.parseDouble(settingValue));
            case PREVIEW_ICON_COLOR -> settings.setPreviewIconColor(HexColor.convert(settingValue));
            case PREVIEW_SONG_START -> settings.setPreviewSongStart((int) Double.parseDouble(settingValue));
            case TRACK_PULSE_LENGTH -> settings.setTrackPulseLength((int) Double.parseDouble(settingValue));
            case SPECIAL_ARTIST_TYPE -> settings.setSpecialArtistType(settingValue);
            case TRACK_ANIM_DURATION -> settings.setTrackColorAnimDuration(Double.parseDouble(settingValue));
            case TRACK_DIS_ANIMATION ->
                    settings.setTrackDisappearAnimation(TrackDisappearAnimation.typeOf(settingValue));
            case TRACK_GLOW_INTENSITY -> settings.setTrackGlowIntensity((int) Double.parseDouble(settingValue));
            case PLANET_EASE_BEHAVIOUR -> settings.setPlanetEasePartBehavior(EasePartBehavior.typeOf(settingValue));
            case PREVIEW_SONG_DURATION -> settings.setPreviewSongDuration((int) Double.parseDouble(settingValue));
            case PERFECT_TEXT -> settings.setPerfectText(settingValue);
            case CONGRATS_TEXT -> settings.setCongratsText(settingValue);
            case SCALING_RATIO -> settings.setScalingRatio((int) Double.parseDouble(settingValue));
            case TRACK_TEXTURE -> settings.setTrackTexture(ImageFile.convert(settingValue));
            case IMAGE_SMOOTHING -> settings.setImageSmoothing(parseToBool(settingValue));
            case DEFAULT_TEXT_COLOR -> settings.setDefaultTextColor(HexColor.convert(settingValue));
            case TRACK_TEXTURE_SCALE -> settings.setTrackTextureScale(Double.parseDouble(settingValue));
            case SHOW_DEFAULT_BG_TILE -> settings.setShowDefaultBGTile(parseToBool(settingValue));
            case DEFAULT_BG_SHAPE_TYPE -> settings.setDefaultBGShapeType(BGShapeType.typeOf(settingValue));
            case DEFAULT_BG_TILE_COLOR -> settings.setDefaultBGTileColor(HexColor.convert(settingValue));
            case DEFAULT_BG_SHAPE_COLOR -> settings.setDefaultBGShapeColor(HexColor.convert(settingValue));
            case DEFAULT_TEXT_SHADOW_COLOR -> settings.setDefaultTextShadowColor(HexColor.convert(settingValue));
            default -> {
                //noop
            }
        }
        customLevel.setSettings(settings.build());
    }

    private static boolean parseToBool(String str) {
        if (str.equals("true")) {
            return true;
        } else if (str.equals("false")) {
            return false;
        } else throw new IllegalArgumentException(str);
    }

    private static String settingValueString(@Nonnull Settings settings, @Nonnull StrData.SettingName settingType) {
        switch (settingType) {
            case BPM -> {
                return Objects.toString(settings.bpm());
            }
            case DIFF -> {
                return Objects.toString(settings.difficulty());
            }
            case SONG -> {
                return Objects.toString(settings.song());
            }
            case ZOOM -> {
                return Objects.toString(settings.zoom());
            }
            case PITCH -> {
                return Objects.toString(settings.pitch());
            }
            case ARTIST -> {
                return Objects.toString(settings.artist());
            }
            case AUTHOR -> {
                return Objects.toString(settings.author());
            }
            case L_TILE -> {
                return Objects.toString(settings.legacySpriteTiles());
            }
            case OFFSET -> {
                return Objects.toString(settings.offset());
            }
            case VOLUME -> {
                return Objects.toString(settings.volume());
            }
            case L_FLASH -> {
                return Objects.toString(settings.legacyFlash());
            }
            case LOOP_BG -> {
                return Objects.toString(settings.loopBG());
            }
            case SEIZURE -> {
                return Objects.toString(settings.seizureWarning());
            }
            case VERSION -> {
                return Objects.toString(VERSION); //latest
            }
            case BG_IMAGE -> {
                return Objects.toString(settings.bgImage());
            }
            case BG_VIDEO -> {
                return Objects.toString(settings.bgVideo());
            }
            case L_CAMERA -> {
                return Objects.toString(settings.legacyCamRelativeTo());
            }
            case PARALLAX -> {
                return Objects.toString(settings.parallax());
            }
            case POSITION -> {
                return Objects.toString(settings.position());
            }
            case ROTATION -> {
                return Objects.toString(settings.rotation());
            }
            case HIT_SOUND -> {
                return Objects.toString(settings.hitSound());
            }
            case SONG_FILE -> {
                return Objects.toString(settings.songFilename());
            }
            case LEVEL_DESC -> {
                return Objects.toString(settings.levelDesc());
            }
            case LEVEL_TAGS -> {
                return Objects.toString(settings.levelTags());
            }
            case LOOP_VIDEO -> {
                return Objects.toString(settings.loopVideo());
            }
            case BEATS_AHEAD -> {
                return Objects.toString(settings.beatsAhead());
            }
            case PLANET_EASE -> {
                return Objects.toString(settings.planetEase());
            }
            case RELATIVE_TO -> {
                return Objects.toString(settings.relativeTo());
            }
            case TRACK_COLOR -> {
                return Objects.toString(settings.trackColor());
            }
            case TRACK_PULSE -> {
                return Objects.toString(settings.trackColorPulse());
            }
            case TRACK_STYLE -> {
                return Objects.toString(settings.trackStyle());
            }
            case ARTIST_LINKS -> {
                return Objects.toString(settings.artistLinks());
            }
            case SPEED_TRIAL_AIM -> {
                return Objects.toString(settings.speedTrialAim());
            }
            case BEATS_BEHIND -> {
                return Objects.toString(settings.beatsBehind());
            }
            case ICON_OUTLINE -> {
                return Objects.toString(settings.iconOutLines());
            }
            case PREVIEW_ICON -> {
                return Objects.toString(settings.previewIcon());
            }
            case VIDEO_OFFSET -> {
                return Objects.toString(settings.videoOffset());
            }
            case LOCK_ROTATION -> {
                return Objects.toString(settings.lockRot());
            }
            case SEPARATE_COUNTDOWN_TIME -> {
                return Objects.toString(settings.separateCountdownTime());
            }
            case PREVIEW_IMAGE -> {
                return Objects.toString(settings.previewImage());
            }
            case REQUIRED_MODS -> {
                return Objects.toString(settings.requireMods());
            }
            case BG_IMAGE_COLOR -> {
                return Objects.toString(settings.bgImageColor());
            }
            case PULSE_ON_FLOOR -> {
                return Objects.toString(settings.pulseOnFloor());
            }
            case STICK_TO_FLOORS -> {
                return Objects.toString(settings.stickToFloors());
            }
            case BG_DISPLAY_MODE -> {
                return Objects.toString(settings.bgDisplayMode());
            }
            case COUNTDOWN_TICKS -> {
                return Objects.toString(settings.countdownTicks());
            }
            case SECONDARY_COLOR -> {
                return Objects.toString(settings.secondaryTrackColor());
            }
            case SHOW_DEFAULT_BG -> {
                return Objects.toString(settings.showDefaultBGIfNoImage());
            }
            case TRACK_ANIMATION -> {
                return Objects.toString(settings.trackAnimation());
            }
            case BACKGROUND_COLOR -> {
                return Objects.toString(settings.backgroundColor());
            }
            case HIT_SOUND_VOLUME -> {
                return Objects.toString(settings.hitSoundVolume());
            }
            case TRACK_COLOR_TYPE -> {
                return Objects.toString(settings.trackColorType());
            }
            case ARTIST_PERMISSION -> {
                return Objects.toString(settings.artistPermission());
            }
            case PLANET_EASE_PARTS -> {
                return Objects.toString(settings.planetEaseParts());
            }
            case PREVIEW_ICON_COLOR -> {
                return Objects.toString(settings.previewIconColor());
            }
            case PREVIEW_SONG_START -> {
                return Objects.toString(settings.previewSongStart());
            }
            case TRACK_PULSE_LENGTH -> {
                return Objects.toString(settings.trackPulseLength());
            }
            case SPECIAL_ARTIST_TYPE -> {
                return Objects.toString(settings.specialArtistType());
            }
            case TRACK_ANIM_DURATION -> {
                return Objects.toString(settings.trackColorAnimDuration());
            }
            case TRACK_DIS_ANIMATION -> {
                return Objects.toString(settings.trackDisappearAnimation());
            }
            case TRACK_GLOW_INTENSITY -> {
                return Objects.toString(settings.trackGlowIntensity());
            }
            case PLANET_EASE_BEHAVIOUR -> {
                return Objects.toString(settings.planetEasePartBehavior());
            }
            case PREVIEW_SONG_DURATION -> {
                return Objects.toString(settings.previewSongDuration());
            }
            case PERFECT_TEXT -> {
                return Objects.toString(settings.perfectText());
            }
            case CONGRATS_TEXT -> {
                return Objects.toString(settings.congratsText());
            }
            case SCALING_RATIO -> {
                return Objects.toString(settings.scalingRatio());
            }
            case TRACK_TEXTURE -> {
                return Objects.toString(settings.trackTexture());
            }
            case IMAGE_SMOOTHING -> {
                return Objects.toString(settings.imageSmoothing());
            }
            case DEFAULT_TEXT_COLOR -> {
                return Objects.toString(settings.defaultTextColor());
            }
            case TRACK_TEXTURE_SCALE -> {
                return Objects.toString(settings.trackTextureScale());
            }
            case SHOW_DEFAULT_BG_TILE -> {
                return Objects.toString(settings.showDefaultBGTile());
            }
            case DEFAULT_BG_SHAPE_TYPE -> {
                return Objects.toString(settings.defaultBGShapeType());
            }
            case DEFAULT_BG_TILE_COLOR -> {
                return Objects.toString(settings.defaultBGTileColor());
            }
            case DEFAULT_BG_SHAPE_COLOR -> {
                return Objects.toString(settings.defaultBGShapeColor());
            }
            case DEFAULT_TEXT_SHADOW_COLOR -> {
                return Objects.toString(settings.defaultTextShadowColor());
            }

            default -> throw new IllegalArgumentException(settingType.name);
        }
    }

    private static int getEventFloorFromString(String data) {
        final int floor;
        final StringContentsReader reader = new StringContentsReader(data);

        String content;

        content = reader.getContentOfQuotes();

        if (!content.equals("floor")) {
            return 0;
        } else {
            reader.skip();
            content = reader.getContentOfComma();
            floor = (int) Double.parseDouble(content.replaceAll(REGEX_COMMA, ""));
        }
        return floor;
    }

    private static String readLine(BufferedReader reader, int a) throws IOException {
        String r = "";
        for (int i = 0; i < a; i++) {
            r = reader.readLine();
        }
        return r;
    }

    /**
     * 파일 열기 | 미선택시 null
     */
    @Nullable
    static File loadFile(String path) {
        return chooseFile(path, LOAD);
    }

    /**
     * 파일 저장하기 | 미선택시 null
     */
    @Nullable
    static File saveFile(String path) {
        return chooseFile(path, SAVE);
    }

    private static StrData getEventDataFromString(String s) {

        final String eventType;
        final StringContentsReader scr = new StringContentsReader(s);

        String content;

        content = scr.getContentOfQuotes();
        scr.skip(); //skip firstElement

        if (content.equals("floor")) { // most case of first element of events is floor, but Global offset is not
            scr.getContentOfQuotes();
            scr.skip(); //skip "eventType"
        }

        eventType = scr.getContentOfQuotes();
        scr.skip();
        content = scr.getContentOfQuotes();

        StrData.Builder eventData = new StrData.Builder(eventType);

        while (!content.isEmpty()) {
            String optionName = content;
            String optionValue = "";
            scr.skip();
            String data = scr.get();

            int i = 0;


            while (data.charAt(i) != '[' && data.charAt(i) != ',' && data.charAt(i) != '}' && data.charAt(i) != '\"') {
                i++;
            }

            switch (data.charAt(i)) {
                case ',' -> optionValue = data.substring(0, data.indexOf(',')).replaceAll(REGEX_COMMA, "");
                case '}' -> optionValue = data.substring(0, data.indexOf('}')).replaceAll(REGEX_BRACE, "");//last item
                case '\"' -> optionValue = scr.getContentOfQuotes();
                case '[' -> optionValue = scr.getContentOfBrackets();
                default -> {
                    //noop
                }
            }
            eventData.setOption(optionName, optionValue);
            scr.skip();
            content = scr.getContentOfQuotes();
        }
        return eventData.build();
    }

    /**
     * 백업을 저장합니다.
     */
    public static boolean backup(PACL editor) {
        File saved = saveFile(editor.generateOutputFolder() + "/backup" + EXTENSION_STR);
        if (saved == null) {
            return false;
        }
        parseFile(editor, editor.level(), saved.getAbsolutePath());
        return true;
    }

    /**
     * 데이터를 파싱합니다.
     */
    public static void parseFile(CustomLevel level, String filePath) {
        parseFile(null, level, filePath);
    }

    /**
     * 데이터를 파싱합니다.
     */
    public static boolean parseFile(PACL editor, boolean overwrite) {
        File input = editor.getInputFile();

        File saved = saveFile(overwrite && input != null ? input.getAbsolutePath() : editor.generateOutputFolder());
        if (saved == null) {
            return false;
        }
        editor.setInputFile(saved);
        String path = saved.getAbsolutePath();
        parseFile(editor, editor.level(), path);
        return true;
    }

    private static void parseFile(@Nullable PACL editor, @Nonnull CustomLevel level, String filePath) {
        File file = saveFile(filePath);

        if (file == null) {
            if (editor != null) {
                editor.setProcessType(Process.LOAD);
            }
            return;
        }

        if (editor == null) {
            long total = level.getCurrentTotalEvents();
            PARSED[0] = 0;
            String parsing = "Parsing...";
            TaskManager.runTask(() -> {
                ConsoleUtils.printProgress(parsing, total, PARSED[0]);
                if (total <= PARSED[0]) {
                    ConsoleUtils.printProgress(parsing, total, PARSED[0]);
                    System.out.println();
                    System.out.println("Finished!!");
                    System.out.println("[ " + file.getAbsolutePath() + " ]");
                    System.out.println();
                    Thread.currentThread().interrupt();
                }
            }, Integer.MAX_VALUE, 50);
        } else {
            editor.setProcessType(Process.PARSE);
            editor.processCurrent = 0;
            editor.processAll = level.getCurrentTotalEvents();
        }

        Assets.pasteAllCopiedAssets(file);

        List<String> parsedEvents = getParsedEvents(editor, level.getTileActions());
        List<String> parsedDecorations = getParsedEvents(editor, level.getTileDecorations());

        if (!write(file, parseAngles(level), parseSettings(level), parsedEvents, parsedDecorations)) {
            if (editor != null) {
                editor.setProcessType(Process.ERROR);
            }
            return;
        }

        if (editor != null) {
            editor.setProcessType(Process.FINISHED);
        }
    }

    private static List<String> parseAngles(CustomLevel level) {
        List<String> parsedAngles = new ArrayList<>();
        for (int i = 1; i <= level.getLength(); i++) {
            parsedAngles.add(Double.toString(level.getAngle(i)));
        }
        return parsedAngles;
    }

    private static List<String> parseSettings(CustomLevel level) {
        Settings settings = level.getSettings();
        List<String> parsedSettings = new ArrayList<>();
        for (StrData.SettingName type : StrData.SettingName.values()) {
            String value = settingValueString(settings, type);

            switch (type) {
                case ARTIST, SPECIAL_ARTIST_TYPE, ARTIST_PERMISSION, SONG, AUTHOR, TRACK_TEXTURE,
                        PREVIEW_IMAGE, PREVIEW_ICON, PREVIEW_ICON_COLOR, LEVEL_DESC, LEVEL_TAGS, ARTIST_LINKS,
                        SONG_FILE, HIT_SOUND, TRACK_COLOR_TYPE, PLANET_EASE_BEHAVIOUR, TRACK_COLOR, SECONDARY_COLOR, TRACK_PULSE, TRACK_STYLE,
                        TRACK_ANIMATION, TRACK_DIS_ANIMATION, BACKGROUND_COLOR, BG_IMAGE, BG_IMAGE_COLOR, BG_DISPLAY_MODE,
                        DEFAULT_BG_TILE_COLOR, DEFAULT_BG_SHAPE_TYPE,
                        DEFAULT_BG_SHAPE_COLOR, DEFAULT_TEXT_COLOR, CONGRATS_TEXT, PERFECT_TEXT,
                        DEFAULT_TEXT_SHADOW_COLOR, RELATIVE_TO, BG_VIDEO, PLANET_EASE ->
                        parsedSettings.add(DOUBLE_TAB + "\"" + type + "\": \"" + value + "\"");
                case VERSION, PREVIEW_SONG_START, LOCK_ROTATION, LOOP_BG, IMAGE_SMOOTHING, TRACK_GLOW_INTENSITY,
                        SHOW_DEFAULT_BG, SHOW_DEFAULT_BG_TILE, PREVIEW_SONG_DURATION, SEIZURE, PULSE_ON_FLOOR, STICK_TO_FLOORS,
                        SEPARATE_COUNTDOWN_TIME, DIFF, BPM, HIT_SOUND_VOLUME, OFFSET, LOOP_VIDEO, ICON_OUTLINE,
                        TRACK_TEXTURE_SCALE, SPEED_TRIAL_AIM, SCALING_RATIO, PITCH, VOLUME, COUNTDOWN_TICKS, TRACK_ANIM_DURATION, TRACK_PULSE_LENGTH,
                        BEATS_AHEAD, BEATS_BEHIND, ROTATION, ZOOM, VIDEO_OFFSET, PLANET_EASE_PARTS, L_FLASH, L_CAMERA, L_TILE ->
                        parsedSettings.add(DOUBLE_TAB + "\"" + type + "\": " + value);
                case POSITION, PARALLAX, REQUIRED_MODS ->
                        parsedSettings.add(DOUBLE_TAB + "\"" + type + "\": [" + value + "]");
                default -> {
                    //noop
                }
            }
        }
        return parsedSettings;
    }

    /**
     * 파싱된 데이터로 파일 작성하기.
     *
     * @return true if success, false otherwise
     */
    private static boolean write(File outputFile, List<String> chart, List<String> settings, List<String> actions, List<String> decorations) {

        try (BufferedWriter output = new BufferedWriter(new FileWriter(outputFile))) {

            output.write("{\n\t");
            output.write("\"angleData\": [");
            output.write(String.join(COMMA_DELIMITER, chart));
            output.write(" ],\n");
            output.write("\t\"settings\":\n\t{\n");
            output.write(String.join(COMMA_LINE, settings));
            output.write("\n\t},\n\t\"actions\":\n\t[\n");
            output.write(String.join(",\n", actions));
            output.write("\n\t],\n\t\"decorations\":\n\t[\n");
            output.write(String.join(COMMA_LINE, decorations));
            output.write("\n\t]\n}\n");
            return true;

        } catch (IOException e) {
            ConsoleUtils.logError(e);
            JOptionPane.showMessageDialog(null, "An Error Occurred during file writing.");
            return false;
        }
    }

    private static <T extends Event> List<String> getParsedEvents(@Nullable PACL editor, List<List<T>> eventDataList) {
        List<String> parsedActions = new ArrayList<>();

        for (int i = 0; i < eventDataList.size(); i++) {
            for (Event eventData : eventDataList.get(i)) {
                if (eventData != null) {
                    parsedActions.add(getParsedEvent(editor, i, eventData));
                }
            }
        }
        return parsedActions;
    }

    private static String getParsedEvent(@Nullable PACL editor, @Nullable Integer eventFloor, Event event) {
        StringBuilder parsed = new StringBuilder("\t\t{ ");

        if (editor == null) {
            PARSED[0]++;
        } else {
            editor.processCurrent++;
        }

        if (eventFloor != null) {
            parsed.append("\"floor\": ")
                    .append(eventFloor)
                    .append(", ");
        }

        parsed.append("\"eventType\": \"")
                .append(event.eventType())
                .append("\"");

        StrData data = StrData.from(event);

        for (String optionName : data.options().keySet()) {

            String optionValue = data.options().get(optionName);

            parsed.append(", \"")
                    .append(optionName)
                    .append("\": ");

            try {
                if (optionValue != null) {
                    if (optionValue.equals("true") || optionValue.equals("false")) {
                        parsed.append(optionValue);
                    } else {
                        Double.parseDouble(optionValue); //exception
                        if (optionName.equals("image") || optionName.contains("Image") || optionName.equals(TAG.name) || optionName.contains("Tag") || optionName.equals(DEC_TEXT.name) || optionName.equals(COLOR.name) || optionName.contains("Color")) {
                            parsed.append("\"")
                                    .append(optionValue)
                                    .append("\"");
                        } else {
                            parsed.append(optionValue);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                if (optionValue.contains(",")) {
                    if (optionName.equals("image") || optionName.contains("Image") || optionName.equals(TAG.name) || optionName.contains("Tag") || optionName.equals(DEC_TEXT.name) || optionName.equals(COMPONENTS.name)) {
                        parsed.append("\"")
                                .append(optionValue)
                                .append("\"");
                    } else {
                        parsed.append("[")
                                .append(optionValue)
                                .append("]");
                    }
                } else {
                    parsed.append("\"")
                            .append(optionValue)
                            .append("\"");
                }
            }
        }
        parsed.append(" }");
        return parsed.toString();
    }
}

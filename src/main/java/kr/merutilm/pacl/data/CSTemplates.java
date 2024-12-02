package kr.merutilm.pacl.data;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.*;
import kr.merutilm.customswing.*;
import kr.merutilm.pacl.al.event.events.Event;
import kr.merutilm.pacl.al.event.events.EventBuilder;
import kr.merutilm.pacl.al.event.events.action.*;
import kr.merutilm.pacl.al.event.events.decoration.AddDecoration;
import kr.merutilm.pacl.al.event.events.decoration.AddObject;
import kr.merutilm.pacl.al.event.events.decoration.AddText;
import kr.merutilm.pacl.al.event.events.decoration.Decorations;
import kr.merutilm.pacl.al.event.selectable.*;
import kr.merutilm.pacl.al.event.selectable.Font;
import kr.merutilm.pacl.al.event.struct.EID;
import kr.merutilm.pacl.al.event.struct.RelativeTile;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import java.awt.*;


final class CSTemplates {
    private CSTemplates() {
    }

    static final class MutableEID {

        EID eventID;

        public MutableEID(EID eventID) {
            this.eventID = eventID;
        }

    }

    private enum OptionName {
        ACTIVE("Active"),
        ANGLE_OFFSET("Angle Offset"),
        COLOR("Color"),
        DECORATION_TAG("Decoration Tag"),
        DEPTH("Depth"),
        DURATION("Duration"),
        EASE("Ease"),
        ENABLED("Enabled"),
        EVENT_TAG("Event Tag"),
        IMAGE("Image"),
        LOCK_ROTATION("Lock Rotation"),
        LOCK_SCALE("Lock Scale"),
        LOCKED("Locked"),
        MASKING_BACK_DEPTH("Masking Back Depth"),
        MASKING_FRONT_DEPTH("Masking Front Depth"),
        MASKING_TYPE("Masking Type"),
        OPACITY("Opacity"),
        PARALLAX("Parallax"),
        PARALLAX_OFFSET("Parallax Offset"),
        PIVOT_OFFSET("Pivot Offset"),
        POSITION("Position"),
        POSITION_OFFSET("Position Offset"),
        RELATIVE_TO("Relative to"),
        ROTATION("Rotation"),
        ROTATION_OFFSET("Rotation Offset"),
        SCALE("Scale"),
        TRACK_COLOR("Track Color"),
        TRACK_STYLE("Track Style"),
        USE_MASKING_DEPTH("Use Masking Depth"),
        VISIBLE("Visible");

        private final String name;

        OptionName(String name) {
            this.name = name;
        }
    }

    @Nonnull
    static EventEditor fromEvent(@Nonnull CSMainFrame master, @Nonnull Event event, @Nonnull MutableEID eid) {

        @Nonnull final EventBuilder builder;
        final CSValueInputGroupPanel groupPanel = new CSValueInputGroupPanel(master, master.analysisPanel(), event.eventType(), CSValueInputGroupPanel.InputType.VERTICAL, true);

        groupPanel.createTextInput("Floor", eid.eventID.floor(), 0, Integer::parseInt, floor -> {
            if (event instanceof Actions) {
                master.getEditor().level().moveAction(eid.eventID, floor);
                eid.eventID = new EID(floor, master.getEditor().level().getFloorActions(floor).size() - 1);
            }
            if (event instanceof Decorations) {
                master.getEditor().level().moveDecoration(eid.eventID, floor);
                eid.eventID = new EID(floor, master.getEditor().level().getFloorDecorations(floor).size() - 1);
            }
        });
        switch (event) {
            case AddDecoration e -> {
                AddDecoration.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.VISIBLE.name, e.visible(), true, edit::setVisible);
                groupPanel.createBoolInput(OptionName.LOCKED.name, e.locked(), true, edit::setLocked);
                groupPanel.createTemplateInput(OptionName.IMAGE.name, e.image(), new ImageFile(""), ImageFile.class, edit::setDecorationImage, getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput(OptionName.POSITION.name, e.position(), new Point2D(0, 0), Point2D.class, edit::setPosition, getNoneMatchTemplatesProvider());
                groupPanel.createSelectInput(OptionName.RELATIVE_TO.name, e.relativeTo(), RelativeTo.AddDecoration.TILE, RelativeTo.AddDecoration.values(), edit::setRelativeTo);
                groupPanel.createTemplateInput(OptionName.PIVOT_OFFSET.name, e.pivotOffset(), new Point2D(0, 0), Point2D.class, edit::setPivotOffset, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput(OptionName.ROTATION.name, e.rotation(), 0.0, Double::parseDouble, edit::setRotation);
                groupPanel.createBoolInput(OptionName.LOCK_ROTATION.name, e.lockRotation(), false, edit::setLockRotation);
                groupPanel.createTemplateInput(OptionName.SCALE.name, e.scale(), new Point2D(100, 100), Point2D.class, edit::setScale, getNoneMatchTemplatesProvider());
                groupPanel.createBoolInput(OptionName.LOCK_SCALE.name, e.lockScale(), false, edit::setLockScale);
                groupPanel.createTemplateInput("Decoration Tiling", e.tile(), new Point2D(1, 1), Point2D.class, edit::setTile, getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput(OptionName.COLOR.name, e.color(), HexColor.WHITE, HexColor.class, edit::setColor, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput(OptionName.OPACITY.name, e.opacity(), 0.0, Double::parseDouble, edit::setOpacity);
                groupPanel.createTextInput(OptionName.DEPTH.name, e.depth(), (short) -1, Short::parseShort, edit::setDepth);
                groupPanel.createTemplateInput(OptionName.PARALLAX.name, e.parallax(), new Point2D(0, 0), Point2D.class, edit::setParallax, getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput(OptionName.PARALLAX_OFFSET.name, e.parallaxOffset(), new Point2D(0, 0), Point2D.class, edit::setParallaxOffset, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput(OptionName.DECORATION_TAG.name, e.tag(), Tag.of(), Tag::convert, edit::setTag);
                groupPanel.createBoolInput("Image Smoothing", e.smoothing(), true, edit::setSmoothing);
                groupPanel.createSelectInput("Blend Mode", e.blendMode(), BlendMode.NONE, BlendMode.values(), edit::setBlendMode);
                groupPanel.createSelectInput(OptionName.MASKING_TYPE.name, e.maskingType(), MaskingType.NONE, MaskingType.values(), edit::setMaskingType);
                groupPanel.createBoolInput(OptionName.USE_MASKING_DEPTH.name, e.useMaskingDepth(), false, edit::setUseMaskingDepth);
                groupPanel.createTextInput(OptionName.MASKING_FRONT_DEPTH.name, e.maskingFrontDepth(), (short) -1, Short::parseShort, edit::setMaskingFrontDepth);
                groupPanel.createTextInput(OptionName.MASKING_BACK_DEPTH.name, e.maskingBackDepth(), (short) -1, Short::parseShort, edit::setMaskingBackDepth);
                groupPanel.createBoolInput("Fail HitBox", e.failBox(), false, edit::setFailBox);
                groupPanel.createSelectInput("Fail HitBox Type", e.failType(), FailHitBox.BOX, FailHitBox.values(), edit::setFailType);
                groupPanel.createTemplateInput("Fail HitBox Scale", e.failScale(), new Point2D(100, 100), Point2D.class, edit::setFailScale, getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput("Fail HitBox Position", e.failPosition(), new Point2D(0, 0), Point2D.class, edit::setFailPosition, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput("Fail HitBox Rotation", e.failRotation(), 0.0, Double::parseDouble, edit::setFailRotation);
                builder = edit;
            }
            case AddText e -> {
                AddText.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.VISIBLE.name, e.visible(), true, edit::setVisible);
                groupPanel.createBoolInput(OptionName.LOCKED.name, e.locked(), true, edit::setLocked);
                groupPanel.createTextInput("Text", e.decText(), "", v -> v, edit::setDecText);
                groupPanel.createSelectInput("Font", e.font(), Font.DEFAULT, Font.values(), edit::setFont);
                groupPanel.createTemplateInput(OptionName.POSITION.name, e.position(), new Point2D(0, 0), Point2D.class, edit::setPosition, getNoneMatchTemplatesProvider());
                groupPanel.createSelectInput(OptionName.RELATIVE_TO.name, e.relativeTo(), RelativeTo.AddDecoration.TILE, RelativeTo.AddDecoration.values(), edit::setRelativeTo);
                groupPanel.createTemplateInput(OptionName.PIVOT_OFFSET.name, e.pivotOffset(), new Point2D(0, 0), Point2D.class, edit::setPivotOffset, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput(OptionName.ROTATION.name, e.rotation(), 0.0, Double::parseDouble, edit::setRotation);
                groupPanel.createBoolInput(OptionName.LOCK_ROTATION.name, e.lockRotation(), false, edit::setLockRotation);
                groupPanel.createTemplateInput(OptionName.SCALE.name, e.scale(), new Point2D(100, 100), Point2D.class, edit::setScale, getNoneMatchTemplatesProvider());
                groupPanel.createBoolInput(OptionName.LOCK_SCALE.name, e.lockScale(), false, edit::setLockScale);
                groupPanel.createTemplateInput(OptionName.COLOR.name, e.color(), HexColor.WHITE, HexColor.class, edit::setColor, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput(OptionName.OPACITY.name, e.opacity(), 0.0, Double::parseDouble, edit::setOpacity);
                groupPanel.createTextInput(OptionName.DEPTH.name, e.depth(), (short) -1, Short::parseShort, edit::setDepth);
                groupPanel.createTemplateInput(OptionName.PARALLAX.name, e.parallax(), new Point2D(0, 0), Point2D.class, edit::setParallax, getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput(OptionName.PARALLAX_OFFSET.name, e.parallaxOffset(), new Point2D(0, 0), Point2D.class, edit::setParallaxOffset, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput(OptionName.DECORATION_TAG.name, e.tag(), Tag.of(), Tag::convert, edit::setTag);
                builder = edit;
            }
            case AddObject e -> {
                AddObject.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.VISIBLE.name, e.visible(), true, edit::setVisible);
                groupPanel.createBoolInput(OptionName.LOCKED.name, e.locked(), true, edit::setLocked);
                groupPanel.createSelectInput("Object Type", e.objectType(), ObjectType.FLOOR, ObjectType.values(), edit::setObjectType);
                groupPanel.createSelectInput("Planet Color Type", e.planetColorType(), PlanetColorType.RED, PlanetColorType.values(), edit::setPlanetColorType);
                groupPanel.createTemplateInput("Planet Color", e.planetColor(), HexColor.RED, HexColor.class, edit::setPlanetColor, getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput("Planet Tail Color", e.planetColor(), HexColor.RED, HexColor.class, edit::setPlanetTailColor, getNoneMatchTemplatesProvider());
                groupPanel.createSelectInput("Track Type", e.trackType(), TrackType.NORMAL, TrackType.values(), edit::setTrackType);
                groupPanel.createTextInput("Track Angle", e.trackAngle(), 180.0, Double::parseDouble, edit::setTrackAngle);
                groupPanel.createTemplateInput(OptionName.TRACK_COLOR.name, e.trackColor(), HexColor.get(222, 187, 123), HexColor.class, edit::setTrackColor, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput("Track Opacity", e.trackOpacity(), 100.0, Double::parseDouble, edit::setTrackOpacity);
                groupPanel.createSelectInput(OptionName.TRACK_STYLE.name, e.trackStyle(), TrackStyle.STANDARD, TrackStyle.values(), edit::setTrackStyle);
                groupPanel.createSelectInput("Track Icon", e.trackIcon(), TrackIcon.NONE, TrackIcon.values(), edit::setTrackIcon);
                groupPanel.createTextInput("Track Icon Angle", e.trackIconAngle(), 0.0, Double::parseDouble, edit::setTrackIconAngle);
                groupPanel.createBoolInput("Red Swirl", e.trackRedSwirl(), false, edit::setTrackRedSwirl);
                groupPanel.createBoolInput("Gray SetSpeed Icon", e.trackGraySetSpeedIcon(), false, edit::setTrackGraySetSpeedIcon);
                groupPanel.createTextInput("SetSpeed Icon BPM", e.trackSetSpeedIconBpm(), 100.0, Double::parseDouble, edit::setTrackSetSpeedIconBpm);
                groupPanel.createBoolInput("Track Glow Enabled", e.trackGlowEnabled(), false, edit::setTrackGlowEnabled);
                groupPanel.createTemplateInput("Track Glow Color", e.trackGlowColor(), HexColor.WHITE, HexColor.class, edit::setTrackGlowColor, getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput(OptionName.POSITION.name, e.position(), new Point2D(0, 0), Point2D.class, edit::setPosition, getNoneMatchTemplatesProvider());
                groupPanel.createSelectInput(OptionName.RELATIVE_TO.name, e.relativeTo(), RelativeTo.AddDecoration.TILE, RelativeTo.AddDecoration.values(), edit::setRelativeTo);
                groupPanel.createTemplateInput(OptionName.PIVOT_OFFSET.name, e.pivotOffset(), new Point2D(0, 0), Point2D.class, edit::setPivotOffset, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput(OptionName.ROTATION.name, e.rotation(), 0.0, Double::parseDouble, edit::setRotation);
                groupPanel.createBoolInput(OptionName.LOCK_ROTATION.name, e.lockRotation(), false, edit::setLockRotation);
                groupPanel.createTemplateInput(OptionName.SCALE.name, e.scale(), new Point2D(100, 100), Point2D.class, edit::setScale, getNoneMatchTemplatesProvider());
                groupPanel.createBoolInput(OptionName.LOCK_SCALE.name, e.lockScale(), false, edit::setLockScale);
                groupPanel.createTextInput(OptionName.DEPTH.name, e.depth(), (short) -1, Short::parseShort, edit::setDepth);
                groupPanel.createTemplateInput(OptionName.PARALLAX.name, e.parallax(), new Point2D(0, 0), Point2D.class, edit::setParallax, getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput(OptionName.PARALLAX_OFFSET.name, e.parallaxOffset(), new Point2D(0, 0), Point2D.class, edit::setParallaxOffset, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput(OptionName.DECORATION_TAG.name, e.tag(), Tag.of(), Tag::convert, edit::setTag);
                builder = edit;
            }
            case AnimateTrack e -> {
                AnimateTrack.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createSelectInput("Track Appear Animation", e.appearAnimation(), TrackAppearAnimation.NONE, TrackAppearAnimation.values(), edit::setAppearAnimation);
                groupPanel.createTextInput("Beats Ahead", e.beatsAhead(), 3.0, Double::parseDouble, edit::setBeatsAhead);
                groupPanel.createSelectInput("Track Disappear Animation", e.disappearAnimation(), TrackDisappearAnimation.FADE, TrackDisappearAnimation.values(), edit::setDisappearAnimation);
                groupPanel.createTextInput("Beats Behind", e.beatsBehind(), 4.0, Double::parseDouble, edit::setBeatsBehind);
                builder = edit;
            }
            case AutoPlayTiles e -> {
                AutoPlayTiles.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createBoolInput(OptionName.ENABLED.name, e.enabled(), true, edit::setEnabled);
                groupPanel.createBoolInput("Safety Tiles", e.safetyTiles(), true, edit::setSafetyTiles);
                builder = edit;
            }
            case Bloom e -> {
                Bloom.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createBoolInput(OptionName.ENABLED.name, e.enabled(), true, edit::setEnabled);
                groupPanel.createTextInput("Threshold", e.threshold(), 50.0, Double::parseDouble, edit::setThreshold);
                groupPanel.createTextInput("Intensity", e.intensity(), 100.0, Double::parseDouble, edit::setIntensity);
                groupPanel.createTemplateInput(OptionName.COLOR.name, e.color(), HexColor.WHITE, HexColor.class, edit::setColor, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput(OptionName.DURATION.name, e.duration(), 0.0, Double::parseDouble, edit::setDuration);
                groupPanel.createSelectInput(OptionName.EASE.name, e.ease(), Ease.LINEAR, Ease.values(), edit::setEase);
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                builder = edit;
            }
            case Bookmark e -> {
                Bookmark.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                builder = edit;
            }
            case CheckPoint e -> {
                CheckPoint.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTextInput("Tile Offset", e.tileOffset(), 0, Integer::parseInt, edit::setTileOffset);
                builder = edit;
            }
            case ColorTrack e -> {
                ColorTrack.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createSelectInput("Track Color Type", e.trackColorType(), TrackColorType.SINGLE, TrackColorType.values(), edit::setTrackColorType);
                groupPanel.createTemplateInput(OptionName.TRACK_COLOR.name, e.trackColor(), HexColor.get(222, 187, 123, 255), HexColor.class, edit::setTrackColor, getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput("2nd Track Color", e.secondaryTrackColor(), HexColor.WHITE, HexColor.class, edit::setSecondaryTrackColor, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput("Animating Interval", e.trackColorAnimDuration(), 2.0, Double::parseDouble, edit::setTrackColorAnimDuration);
                groupPanel.createSelectInput("Color Pulsing Method", e.trackColorPulse(), TrackColorPulse.NONE, TrackColorPulse.values(), edit::setTrackColorPulse);
                groupPanel.createTextInput("Pulse Length", e.trackPulseLength(), 10, Integer::parseInt, edit::setTrackPulseLength);
                groupPanel.createSelectInput(OptionName.TRACK_STYLE.name, e.trackStyle(), TrackStyle.STANDARD, TrackStyle.values(), edit::setTrackStyle);
                groupPanel.createTemplateInput("Track Texture", e.trackTexture(), new ImageFile(""), ImageFile.class, edit::setTrackTexture, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput("Texture Scale", e.trackTextureScale(), 1.0, Double::parseDouble, edit::setTrackTextureScale);
                groupPanel.createTextInput("Glow Intensity", e.trackGlowIntensity(), 100, Integer::parseInt, edit::setTrackGlowIntensity);
                builder = edit;
            }
            case CustomBackground e -> {
                CustomBackground.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTemplateInput("Background Color", e.backgroundColor(), HexColor.BLACK, HexColor.class, edit::setBackgroundColor, getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput(OptionName.IMAGE.name, e.image(), new ImageFile(""), ImageFile.class, edit::setImage, getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput("Image Color", e.imageColor(), HexColor.WHITE, HexColor.class, edit::setImageColor, getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput("Parallax", e.parallax(), new Point2D(100, 100), Point2D.class, edit::setParallax, getNoneMatchTemplatesProvider());
                groupPanel.createSelectInput("Display Mode", e.bgDisplayMode(), BGDisplayMode.FIT_TO_SCREEN, BGDisplayMode.values(), edit::setBgDisplayMode);
                groupPanel.createBoolInput("Image Smoothing", e.imageSmoothing(), true, edit::setImageSmoothing);
                groupPanel.createBoolInput("Lock Rotation", e.lockRot(), false, edit::setLockRot);
                groupPanel.createBoolInput("Loop", e.loop(), false, edit::setLoop);
                groupPanel.createTextInput("Scaling Ratio", e.scalingRatio(), 100, Integer::parseInt, edit::setScalingRatio);
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                builder = edit;
            }
            case EditorComment e -> {
                EditorComment.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTextInput("Comment", e.comment(), "", v -> v, edit::setComment);
                builder = edit;
            }
            case Flash e -> {
                Flash.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTextInput(OptionName.DURATION.name, e.duration(), 1.0, Double::parseDouble, edit::setDuration);
                groupPanel.createSelectInput("Plane", e.plane(), Plane.BACKGROUND, Plane.values(), edit::setPlane);
                groupPanel.createTemplateInput("Start Color", e.startColor(), HexColor.WHITE, HexColor.class, edit::setStartColor, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput("Start Opacity", e.startOpacity(), 100.0, Double::parseDouble, edit::setStartOpacity);
                groupPanel.createTemplateInput("End Color", e.endColor(), HexColor.WHITE, HexColor.class, edit::setEndColor, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput("End Opacity", e.endOpacity(), 0.0, Double::parseDouble, edit::setEndOpacity);
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createSelectInput(OptionName.EASE.name, e.ease(), Ease.LINEAR, Ease.values(), edit::setEase);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                builder = edit;
            }
            case HallOfMirrors e -> {
                HallOfMirrors.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createBoolInput(OptionName.ENABLED.name, e.enabled(), true, edit::setEnabled);
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                builder = edit;
            }
            case Hide e -> {
                Hide.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createBoolInput("Hide Judgement", e.hideJudgment(), true, edit::setHideJudgment);
                groupPanel.createBoolInput("Hide Tile Icon", e.hideTileIcon(), true, edit::setHideTileIcon);
                builder = edit;
            }
            case Hold e -> {
                Hold.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTextInput(OptionName.DURATION.name, e.duration(), 1, Integer::parseInt, edit::setDuration);
                groupPanel.createTextInput("Travel Distance", e.distanceMultiplier(), 100.0, Double::parseDouble, edit::setDistanceMultiplier);
                groupPanel.createBoolInput("Landing Animation", e.landingAnimation(), false, edit::setLandingAnimation);
                builder = edit;
            }
            case MoveCamera e -> {
                MoveCamera.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTextInput(OptionName.DURATION.name, e.duration(), 1.0, Double::parseDouble, edit::setDuration);
                groupPanel.createNullableSelectInput(OptionName.RELATIVE_TO.name, e.relativeTo(), RelativeTo.Camera.PLAYER, RelativeTo.Camera.values(), edit::setRelativeTo);
                groupPanel.createNullableTemplateInput(OptionName.POSITION.name, e.position(), new Point2D(0, 0), Point2D.class, edit::setPosition, getNoneMatchTemplatesProvider());
                groupPanel.createNullableTextInput(OptionName.ROTATION.name, e.rotation(), 0.0, Double::parseDouble, edit::setRotation);
                groupPanel.createNullableTextInput("Zoom", e.zoom(), 100.0, Double::parseDouble, edit::setZoom);
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createSelectInput(OptionName.EASE.name, e.ease(), Ease.LINEAR, Ease.values(), edit::setEase);
                groupPanel.createBoolInput("Do Not Disable", e.doNotDisable(), false, edit::setDoNotDisable);
                groupPanel.createBoolInput("Min VFX Only", e.minVfxOnly(), false, edit::setMinVfxOnly);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                builder = edit;
            }
            case MoveDecorations e -> {
                MoveDecorations.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTextInput(OptionName.DURATION.name, e.duration(), 1.0, Double::parseDouble, edit::setDuration);
                groupPanel.createTextInput(OptionName.DECORATION_TAG.name, e.tag(), Tag.of(), Tag::convert, edit::setTag);
                groupPanel.createNullableBoolInput(OptionName.VISIBLE.name, e.visible(), true, edit::setVisible);
                groupPanel.createNullableSelectInput(OptionName.RELATIVE_TO.name, e.relativeTo(), RelativeTo.MoveDecoration.TILE, RelativeTo.MoveDecoration.values(), edit::setRelativeTo);
                groupPanel.createNullableTemplateInput(OptionName.IMAGE.name, e.image(), new ImageFile(""), ImageFile.class, edit::setImage, getNoneMatchTemplatesProvider());
                groupPanel.createNullableTemplateInput(OptionName.POSITION_OFFSET.name, e.positionOffset(), new Point2D(0, 0), Point2D.class, edit::setPositionOffset, getNoneMatchTemplatesProvider());
                groupPanel.createNullableTemplateInput(OptionName.PIVOT_OFFSET.name, e.pivotOffset(), new Point2D(0, 0), Point2D.class, edit::setPivotOffset, getNoneMatchTemplatesProvider());
                groupPanel.createNullableTextInput(OptionName.ROTATION_OFFSET.name, e.rotationOffset(), 0.0, Double::parseDouble, edit::setRotationOffset);
                groupPanel.createNullableTemplateInput(OptionName.SCALE.name, e.scale(), new Point2D(100, 100), Point2D.class, edit::setScale, getNoneMatchTemplatesProvider());
                groupPanel.createNullableTemplateInput(OptionName.COLOR.name, e.color(), HexColor.WHITE, HexColor.class, edit::setColor, getNoneMatchTemplatesProvider());
                groupPanel.createNullableTextInput(OptionName.OPACITY.name, e.opacity(), 100.0, Double::parseDouble, edit::setOpacity);
                groupPanel.createNullableTextInput(OptionName.DEPTH.name, e.depth(), (short) -1, Short::parseShort, edit::setDepth);
                groupPanel.createNullableTemplateInput(OptionName.PARALLAX.name, e.parallax(), new Point2D(0, 0), Point2D.class, edit::setParallax, getNoneMatchTemplatesProvider());
                groupPanel.createNullableTemplateInput(OptionName.PARALLAX_OFFSET.name, e.parallaxOffset(), new Point2D(0, 0), Point2D.class, edit::setParallaxOffset, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createSelectInput(OptionName.EASE.name, e.ease(), Ease.LINEAR, Ease.values(), edit::setEase);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                groupPanel.createNullableSelectInput(OptionName.MASKING_TYPE.name, e.maskingType(), MaskingType.NONE, MaskingType.values(), edit::setMaskingType);
                groupPanel.createNullableBoolInput(OptionName.USE_MASKING_DEPTH.name, e.useMaskingDepth(), false, edit::setUseMaskingDepth);
                groupPanel.createNullableTextInput(OptionName.MASKING_FRONT_DEPTH.name, e.maskingFrontDepth(), (short) -1, Short::parseShort, edit::setMaskingFrontDepth);
                groupPanel.createNullableTextInput(OptionName.MASKING_BACK_DEPTH.name, e.maskingBackDepth(), (short) -1, Short::parseShort, edit::setMaskingBackDepth);
                builder = edit;
            }
            case MoveTrack e -> {
                MoveTrack.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTemplateInput("Start Tile", e.startTile(), new RelativeTile(0, RelativeTo.Tile.THIS_TILE), RelativeTile.class, edit::setStartTile, getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput("End Tile", e.endTile(), new RelativeTile(0, RelativeTo.Tile.THIS_TILE), RelativeTile.class, edit::setEndTile, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput("Active Tile Interval", e.gap(), 1, Integer::parseInt, edit::setGap);
                groupPanel.createTextInput(OptionName.DURATION.name, e.duration(), 1.0, Double::parseDouble, edit::setDuration);
                groupPanel.createNullableTemplateInput(OptionName.POSITION_OFFSET.name, e.positionOffset(), new Point2D(0, 0), Point2D.class, edit::setPositionOffset, getNoneMatchTemplatesProvider());
                groupPanel.createNullableTextInput(OptionName.ROTATION_OFFSET.name, e.rotationOffset(), 0.0, Double::parseDouble, edit::setRotationOffset);
                groupPanel.createNullableTemplateInput(OptionName.SCALE.name, e.scale(), new Point2D(100, 100), Point2D.class, edit::setScale, getNoneMatchTemplatesProvider());
                groupPanel.createNullableTextInput(OptionName.OPACITY.name, e.opacity(), 100.0, Double::parseDouble, edit::setOpacity);
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createSelectInput(OptionName.EASE.name, e.ease(), Ease.LINEAR, Ease.values(), edit::setEase);
                groupPanel.createBoolInput("Min VFX Only", e.maxVfxOnly(), false, edit::setMaxVfxOnly);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                builder = edit;
            }
            case MultiPlanet e -> {
                MultiPlanet.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createSelectInput("Planets", e.planets(), Planets.TWO_PLANETS, Planets.values(), edit::setPlanets);
                builder = edit;
            }
            case Pause e -> {
                Pause.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTextInput(OptionName.DURATION.name, e.duration(), 1.0, Double::parseDouble, edit::setDuration);
                groupPanel.createTextInput("Countdown Ticks", e.countdownTicks(), 0, Integer::parseInt, edit::setCountdownTicks);
                groupPanel.createTextInput("Angle Correction", e.angleCorrectionDir(), -1, Integer::parseInt, edit::setAngleCorrectionDir);
                builder = edit;
            }
            case PlaySound e -> {
                PlaySound.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createSelectInput("Hit Sound", e.hitSound(), HitSound.KICK, HitSound.values(), edit::setHitSound);
                groupPanel.createTextInput("Volume", e.volume(), 100, Integer::parseInt, edit::setVolume);
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                builder = edit;
            }
            case PositionTrack e -> {
                PositionTrack.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTemplateInput(OptionName.POSITION_OFFSET.name, e.positionOffset(), new Point2D(0, 0), Point2D.class, edit::setPositionOffset, getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput(OptionName.RELATIVE_TO.name, e.relativeTo(), new RelativeTile(0, RelativeTo.Tile.THIS_TILE), RelativeTile.class, edit::setRelativeTo, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput(OptionName.ROTATION.name, e.rotation(), 0.0, Double::parseDouble, edit::setRotation);
                groupPanel.createTextInput(OptionName.SCALE.name, e.scale(), 100.0, Double::parseDouble, edit::setScale);
                groupPanel.createTextInput(OptionName.OPACITY.name, e.opacity(), 100.0, Double::parseDouble, edit::setOpacity);
                groupPanel.createBoolInput("Just This Tile", e.justThisTile(), false, edit::setJustThisTile);
                groupPanel.createBoolInput("Editor Only", e.editorOnly(), false, edit::setEditorOnly);
                groupPanel.createNullableBoolInput("Stick To Floors", e.stickToFloors(), true, edit::setStickToFloors);
                builder = edit;
            }
            case RecolorTrack e -> {
                RecolorTrack.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTemplateInput("Start Tile", e.startTile(), new RelativeTile(0, RelativeTo.Tile.THIS_TILE), RelativeTile.class, edit::setStartTile,getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput("End Tile", e.endTile(), new RelativeTile(0, RelativeTo.Tile.THIS_TILE), RelativeTile.class, edit::setEndTile,getNoneMatchTemplatesProvider());
                groupPanel.createTextInput("Active Tile Interval", e.gap(), 0, Integer::parseInt, edit::setGap);
                groupPanel.createTextInput(OptionName.DURATION.name, e.duration(), 0.0, Double::parseDouble, edit::setDuration);
                groupPanel.createSelectInput("Track Color Type", e.trackColorType(), TrackColorType.SINGLE, TrackColorType.values(), edit::setTrackColorType);
                groupPanel.createTemplateInput(OptionName.TRACK_COLOR.name, e.trackColor(), HexColor.get(222, 187, 123), HexColor.class, edit::setTrackColor,getNoneMatchTemplatesProvider());
                groupPanel.createTemplateInput("2nd Track Color", e.secondaryTrackColor(), HexColor.WHITE, HexColor.class, edit::setSecondaryTrackColor,getNoneMatchTemplatesProvider());
                groupPanel.createTextInput("Animating Interval", e.trackColorAnimDuration(), 2.0, Double::parseDouble, edit::setTrackColorAnimDuration);
                groupPanel.createSelectInput("Color Pulsing Method", e.trackColorPulse(), TrackColorPulse.NONE, TrackColorPulse.values(), edit::setTrackColorPulse);
                groupPanel.createTextInput("Pulse Length", e.trackPulseLength(), 10, Integer::parseInt, edit::setTrackPulseLength);
                groupPanel.createSelectInput(OptionName.TRACK_STYLE.name, e.trackStyle(), TrackStyle.STANDARD, TrackStyle.values(), edit::setTrackStyle);
                groupPanel.createTextInput("Glow Intensity", e.trackGlowIntensity(), 100, Integer::parseInt, edit::setTrackGlowIntensity);
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                builder = edit;
            }
            case RepeatEvents e -> {
                RepeatEvents.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createSelectInput("Repeat Type", e.repeatType(), RepeatType.BEAT, RepeatType.values(), edit::setRepeatType);
                groupPanel.createTextInput("Repetitions", e.repetitions(), 1, Integer::parseInt, edit::setRepetitions);
                groupPanel.createTextInput("Repeat Tile Count", e.floorCount(), 1, Integer::parseInt, edit::setFloorCount);
                groupPanel.createTextInput("Interval", e.interval(), 1.0, Double::parseDouble, edit::setInterval);
                groupPanel.createBoolInput("Execute On Execution Floor", e.executeOnCurrentFloor(), false, edit::setExecuteOnCurrentFloor);
                groupPanel.createTextInput("Target Action Tag", e.tag(), Tag.of(), Tag::convert, edit::setTag);
                builder = edit;
            }
            case ScaleMargin e -> {
                ScaleMargin.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTextInput(OptionName.SCALE.name, e.scale(), 100.0, Double::parseDouble, edit::setScale);
                builder = edit;
            }
            case ScalePlanets e -> {
                ScalePlanets.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTextInput(OptionName.DURATION.name, e.duration(), 1.0, Double::parseDouble, edit::setDuration);
                groupPanel.createSelectInput("Target Planet", e.targetPlanet(), TargetPlanet.FIRE, TargetPlanet.values(), edit::setTargetPlanet);
                groupPanel.createTextInput("Planet Scale", e.scale(), 100.0, Double::parseDouble, edit::setScale);
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createSelectInput(OptionName.EASE.name, e.ease(), Ease.LINEAR, Ease.values(), edit::setEase);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                builder = edit;
            }
            case ScaleRadius e -> {
                ScaleRadius.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTextInput(OptionName.SCALE.name, e.scale(), 100.0, Double::parseDouble, edit::setScale);
                builder = edit;
            }
            case ScreenScroll e -> {
                ScreenScroll.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTemplateInput("Scroll Speed", e.scroll(), new Point2D(0, 0), Point2D.class, edit::setScroll, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                builder = edit;
            }
            case ScreenTile e -> {
                ScreenTile.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTemplateInput("Tiling Amount", e.tile(), new Point2D(1, 1), Point2D.class, edit::setTile, getNoneMatchTemplatesProvider());
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                builder = edit;
            }
            case SetConditionalEvents e -> {
                SetConditionalEvents.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTextInput("Perfect! Tag", e.perfectTag(), SetConditionalEvents.NONE, v -> v, edit::setPerfectTag);
                groupPanel.createTextInput("EPerfect! Tag", e.earlyPerfectTag(), SetConditionalEvents.NONE, v -> v, edit::setEarlyPerfectTag);
                groupPanel.createTextInput("LPerfect! Tag", e.latePerfectTag(), SetConditionalEvents.NONE, v -> v, edit::setLatePerfectTag);
                groupPanel.createTextInput("Early! Tag", e.veryEarlyTag(), SetConditionalEvents.NONE, v -> v, edit::setVeryEarlyTag);
                groupPanel.createTextInput("Late! Tag", e.veryLateTag(), SetConditionalEvents.NONE, v -> v, edit::setVeryLateTag);
                groupPanel.createTextInput("Early!! Tag", e.tooEarlyTag(), SetConditionalEvents.NONE, v -> v, edit::setTooEarlyTag);
                groupPanel.createTextInput("Late!! Tag", e.tooLateTag(), SetConditionalEvents.NONE, v -> v, edit::setTooLateTag);
                groupPanel.createTextInput("Loss Tag", e.lossTag(), SetConditionalEvents.NONE, v -> v, edit::setLossTag);
                builder = edit;
            }
            case SetFilter e -> {
                SetFilter.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createBoolInput(OptionName.ENABLED.name, e.enabled(), true, edit::setEnabled);
                groupPanel.createSelectInput("Filter", e.filter(), Filter.GRAYSCALE, Filter.values(), edit::setFilter);
                groupPanel.createTextInput("Filter Intensity", e.intensity(), 100.0, Double::parseDouble, edit::setIntensity);
                groupPanel.createBoolInput("Disable Other Filters", e.disableOthers(), false, edit::setDisableOthers);
                groupPanel.createTextInput(OptionName.DURATION.name, e.duration(), 0.0, Double::parseDouble, edit::setDuration);
                groupPanel.createSelectInput(OptionName.EASE.name, e.ease(), Ease.LINEAR, Ease.values(), edit::setEase);
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                builder = edit;
            }
            case SetHitsound e -> {
                SetHitsound.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createSelectInput("Game Sound", e.gameSound(), GameSound.HITSOUND, GameSound.values(), edit::setGameSound);
                groupPanel.createSelectInput("Hit Sound", e.hitSound(), HitSound.KICK, HitSound.values(), edit::setHitSound);
                groupPanel.createTextInput("Volume", e.volume(), 100, Integer::parseInt, edit::setVolume);
                builder = edit;
            }
            case SetPlanetRotation e -> {
                SetPlanetRotation.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createSelectInput(OptionName.EASE.name, e.ease(), Ease.LINEAR, Ease.values(), edit::setEase);
                groupPanel.createTextInput("Ease Parts", e.easeParts(), 1, Integer::parseInt, edit::setEaseParts);
                groupPanel.createSelectInput("Behavior", e.easePartBehavior(), EasePartBehavior.MIRROR, EasePartBehavior.values(), edit::setEasePartBehavior);
                builder = edit;
            }
            case SetSpeed e -> {
                SetSpeed.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createSelectInput("Speed Type", e.speedType(), SpeedType.BPM, SpeedType.values(), edit::setSpeedType);
                groupPanel.createTextInput("BPM", e.bpm(), 100.0, Double::parseDouble, edit::setBpm);
                groupPanel.createTextInput("BPM Multiplier", e.multiplier(), 1.0, Double::parseDouble, edit::setMultiplier);
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                builder = edit;
            }
            case SetText e -> {
                SetText.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTextInput("Text", e.decText(), "", v -> v, edit::setDecText);
                groupPanel.createTextInput(OptionName.DECORATION_TAG.name, e.tag(), Tag.of(), Tag::convert, edit::setTag);
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                builder = edit;
            }
            case ShakeScreen e -> {
                ShakeScreen.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                groupPanel.createTextInput(OptionName.DURATION.name, e.duration(), 1.0, Double::parseDouble, edit::setDuration);
                groupPanel.createTextInput("Strength", e.strength(), 100.0, Double::parseDouble, edit::setStrength);
                groupPanel.createTextInput("Shake Intensity", e.intensity(), 100.0, Double::parseDouble, edit::setIntensity);
                groupPanel.createBoolInput("Fade Out", e.fadeOut(), true, edit::setFadeOut);
                groupPanel.createTextInput(OptionName.ANGLE_OFFSET.name, e.angleOffset(), 0.0, Double::parseDouble, edit::setAngleOffset);
                groupPanel.createTextInput(OptionName.EVENT_TAG.name, e.eventTag(), Tag.of(), Tag::convert, edit::setEventTag);
                builder = edit;
            }
            case Twirl e -> {
                Twirl.Builder edit = e.edit();
                groupPanel.createBoolInput(OptionName.ACTIVE.name, e.active(), true, edit::setActive);
                builder = edit;
            }
            default -> builder = event.edit();
        }
        master.analysisPanel().add(groupPanel);
        master.analysisPanel().repaint();
        groupPanel.setVisible(false);

        return new EventEditor(groupPanel, builder, eid);
    }

    static HexColor getEventBaseColor(@Nonnull kr.merutilm.pacl.al.event.events.Event event) {
        final Color baseColor = switch (event) {
            case AddDecoration c -> new Color(0, 255, 255);
            case AddText c -> new Color(0, 255, 165);
            case AddObject c -> new Color(0, 255, 75);
            case AnimateTrack c -> new Color(255, 100, 255);
            case AutoPlayTiles c -> new Color(125, 125, 255);
            case Bloom c -> new Color(255, 255, 155);
            case Bookmark c -> new Color(100, 145, 105);
            case CheckPoint c -> new Color(0, 155, 255);
            case ColorTrack c -> new Color(155, 55, 55);
            case CustomBackground c -> new Color(162, 122, 122);
            case EditorComment c -> new Color(195, 70, 105);
            case Flash c -> new Color(160, 100, 165);
            case HallOfMirrors c -> new Color(180, 155, 122);
            case Hide c -> new Color(120, 166, 125);
            case Hold c -> new Color(100, 150, 255);
            case MoveCamera c -> new Color(50, 150, 145);
            case MoveDecorations c -> new Color(255, 55, 255);
            case MoveTrack c -> new Color(195, 195, 5);
            case MultiPlanet c -> new Color(142, 88, 129);
            case Pause c -> new Color(10, 125, 214);
            case PlaySound c -> new Color(10, 180, 145);
            case PositionTrack c -> new Color(50, 112, 215);
            case RecolorTrack c -> new Color(255, 255, 0);
            case RepeatEvents c -> new Color(9, 130, 165);
            case ScaleMargin c -> new Color(190, 90, 25);
            case ScalePlanets c -> new Color(190, 86, 84);
            case ScaleRadius c -> new Color(10, 150, 25);
            case ScreenScroll c -> new Color(100, 255, 125);
            case ScreenTile c -> new Color(160, 190, 226);
            case SetConditionalEvents c -> new Color(114, 98, 92);
            case SetFilter c -> new Color(125, 30, 190);
            case SetHitsound c -> new Color(155, 60, 155);
            case SetPlanetRotation c -> new Color(125, 123, 12);
            case SetSpeed c -> new Color(50, 50, 255);
            case SetText c -> new Color(60, 60, 255);
            case ShakeScreen c -> new Color(100, 150, 125);
            case Twirl c -> new Color(120, 60, 20);
            default -> new Color(96, 96, 96);
        };

        return HexColor.fromAWT(baseColor);
    }

    public static <S extends Struct<?>> CSValueInputGroupPanel.TemplateProvider<S> getNoneMatchTemplatesProvider() {
        return (value, groupPanel, classType, enterFunction) -> {
            switch (value) {
                case RelativeTile s -> {
                RelativeTile.Builder edit = s.edit();
                groupPanel.createTextInput("offset", s.offset(), 0, Integer::parseInt, e -> {
                    edit.setOffset(e);
                    enterFunction.accept(classType.cast(edit.build()));
                });
                groupPanel.createSelectInput("RelativeTo", s.relativeTo(), RelativeTo.Tile.THIS_TILE, RelativeTo.Tile.values(), e -> {
                    edit.setRelativeTo(e);
                    enterFunction.accept(classType.cast(edit.build()));
                });
                }
                case AngleData s -> groupPanel.createTextInput("angle", s.angle(), 0.0, Double::parseDouble, e -> {
                    s.setAngle(e);
                    enterFunction.accept(classType.cast(s.angle()));
                });
                case EID s -> {
                    EID.Builder edit = s.edit();
                    groupPanel.createTextInput("Floor", s.floor(), 0, Integer::parseInt, e -> {
                        edit.setFloor(e);
                        enterFunction.accept(classType.cast(edit.build()));
                    });
                    groupPanel.createTextInput("Address", s.address(), 1, Integer::parseInt, e -> {
                        edit.setAddress(e);
                        enterFunction.accept(classType.cast(edit.build()));
                    });
                }
                default -> {
                    //noop
                }
            }
        };
    }
}
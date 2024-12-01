package kr.merutilm.pacl.data;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.customswing.CSAnimatablePanel;
import kr.merutilm.customswing.CSValueInputGroupPanel;
import kr.merutilm.pacl.al.event.selectable.*;

import javax.annotation.Nullable;
import java.io.Serial;

final class CSSettingsPanel extends CSAnimatablePanel {

    @Serial
    private static final long serialVersionUID = -8212958465864773282L;
    private CSValueInputGroupPanel groupPanel;
    private final CSTimelinePanel timelinePanel;

    public CSSettingsPanel(@Nullable CSMainFrame master, CSTimelinePanel timelinePanel) {
        super(master);
        this.timelinePanel = timelinePanel;
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        if (groupPanel != null && isStopped()) {
            remove(groupPanel);
            groupPanel = null;
        }
    }

    public void reload() {

        if (groupPanel != null) {
            remove(groupPanel);
        }

        this.groupPanel = new CSValueInputGroupPanel(getMainFrame(), this, "", CSValueInputGroupPanel.InputType.VERTICAL, false);
        Settings settings = ((CSMainFrame) getMainFrame()).getEditor().level().getSettings();
        Settings.Builder builder = settings.edit();

        groupPanel.createTitle("Level");
        groupPanel.createTextInput("File", settings.songFilename(), "", e -> e, builder::setSongFilename);
        groupPanel.createTextInput("Artist", settings.artist(), "Artist", e -> e, builder::setArtist);
        groupPanel.createTextInput("Song", settings.song(), "Song", e -> e, builder::setSong);
        groupPanel.createTextInput("Author", settings.author(), "", e -> e, builder::setAuthor);

        groupPanel.createTitle("Chart");
        groupPanel.createTextInput("BPM", settings.bpm(), 100.0, Double::parseDouble, builder::setBpm);
        groupPanel.createTextInput("Volume", settings.volume(), 100, Integer::parseInt, builder::setVolume);
        groupPanel.createTextInput("Offset", settings.offset(), 0, Integer::parseInt, builder::setOffset);
        groupPanel.createTextInput("Pitch", settings.pitch(), 100, Integer::parseInt, builder::setPitch);
        groupPanel.createSelectInput("Hit Sound", settings.hitSound(), HitSound.KICK, HitSound.values(), builder::setHitSound);
        groupPanel.createTextInput("HitSound Volume", settings.hitSoundVolume(), 100, Integer::parseInt, builder::setHitSoundVolume);
        groupPanel.createBoolInput("Stick To Floors", settings.stickToFloors(), true, builder::setStickToFloors);
        groupPanel.createTextInput("Countdown Ticks", settings.countdownTicks(), 4, Integer::parseInt, builder::setCountdownTicks);

        groupPanel.createTitle("Camera");
        groupPanel.createSelectInput("Camera Relative To", settings.relativeTo(), RelativeTo.Camera.PLAYER, RelativeTo.Camera.values(), builder::setRelativeTo);
        groupPanel.createTemplateInput("Camera Position", settings.position(), Point2D.ORIGIN, Point2D.class, builder::setPosition, CSTemplates.getNoneMatchTemplatesProvider());
        groupPanel.createTextInput("Camera Rotation", settings.rotation(), 0.0, Double::parseDouble, builder::setRotation);
        groupPanel.createTextInput("Camera Zoom", settings.zoom(), 220.0, Double::parseDouble, builder::setZoom);

        groupPanel.createTitle("Track");
        groupPanel.createSelectInput("Track Color Type", settings.trackColorType(), TrackColorType.SINGLE, TrackColorType.values(), builder::setTrackColorType);
        groupPanel.createTemplateInput("Track Color", settings.trackColor(), HexColor.get(222, 187, 123), HexColor.class, builder::setTrackColor, CSTemplates.getNoneMatchTemplatesProvider());
        groupPanel.createTemplateInput("Secondary Track Color", settings.secondaryTrackColor(), HexColor.WHITE, HexColor.class, builder::setSecondaryTrackColor, CSTemplates.getNoneMatchTemplatesProvider());
        groupPanel.createTextInput("Track Color Pulse Duration", settings.trackColorAnimDuration(), 2.0, Double::parseDouble, builder::setTrackColorAnimDuration);
        groupPanel.createSelectInput("trackColorPulse", settings.trackColorPulse(), TrackColorPulse.NONE, TrackColorPulse.values(), builder::setTrackColorPulse);
        groupPanel.createTextInput("Track Pulse Length", settings.trackPulseLength(), 10, Integer::parseInt, builder::setTrackPulseLength);
        groupPanel.createSelectInput("Track Style", settings.trackStyle(), TrackStyle.STANDARD, TrackStyle.values(), builder::setTrackStyle);
        groupPanel.createTemplateInput("Track Texture", settings.trackTexture(), new ImageFile(""), ImageFile.class, builder::setTrackTexture, CSTemplates.getNoneMatchTemplatesProvider());
        groupPanel.createTextInput("Track Texture Scale", settings.trackTextureScale(), 1.0, Double::parseDouble, builder::setTrackTextureScale);
        groupPanel.createTextInput("Track Glow Intensity", settings.trackGlowIntensity(), 0, Integer::parseInt, builder::setTrackGlowIntensity);
        groupPanel.createSelectInput("Track Appear Animation", settings.trackAnimation(), TrackAppearAnimation.NONE, TrackAppearAnimation.values(), builder::setTrackAnimation);
        groupPanel.createTextInput("Beats Ahead", settings.beatsAhead(), 3.0, Double::parseDouble, builder::setBeatsAhead);
        groupPanel.createSelectInput("Track Disappear Animation", settings.trackDisappearAnimation(), TrackDisappearAnimation.NONE, TrackDisappearAnimation.values(), builder::setTrackDisappearAnimation);
        groupPanel.createTextInput("Beats Behind", settings.beatsBehind(), 4.0, Double::parseDouble, builder::setBeatsBehind);

        groupPanel.createTitle("Background");
        groupPanel.createTemplateInput("Background Color", settings.backgroundColor(), HexColor.BLACK, HexColor.class, builder::setBackgroundColor, CSTemplates.getNoneMatchTemplatesProvider());
        groupPanel.createTemplateInput("Background Image", settings.bgImage(), new ImageFile(""), ImageFile.class, builder::setBgImage, CSTemplates.getNoneMatchTemplatesProvider());
        groupPanel.createTemplateInput("Background Image Color", settings.bgImageColor(), HexColor.WHITE, HexColor.class, builder::setBgImageColor, CSTemplates.getNoneMatchTemplatesProvider());
        groupPanel.createTemplateInput("Background Parallax", settings.parallax(), new Point2D(50, 50), Point2D.class, builder::setParallax, CSTemplates.getNoneMatchTemplatesProvider());
        groupPanel.createSelectInput("Background DisplayMode", settings.bgDisplayMode(), BGDisplayMode.FIT_TO_SCREEN, BGDisplayMode.values(), builder::setBgDisplayMode);
        groupPanel.createBoolInput("Lock Background Rotation", settings.lockRot(), false, builder::setLockRot);
        groupPanel.createBoolInput("Loop Background", settings.loopBG(), true, builder::setLoopBG);
        groupPanel.createTextInput("Scaling Ratio", settings.scalingRatio(), 4, Integer::parseInt, builder::setScalingRatio);
        groupPanel.createTemplateInput("Background Video", settings.bgVideo(), new ImageFile(""), ImageFile.class, builder::setBgVideo, CSTemplates.getNoneMatchTemplatesProvider());
        groupPanel.createBoolInput("Loop Video", settings.loopVideo(), false, builder::setLoopVideo);
        groupPanel.createTextInput("Video Offset", settings.videoOffset(), 0, Integer::parseInt, builder::setVideoOffset);


        groupPanel.addPropertyChangedAction(() -> {
            ((CSMainFrame) getMainFrame()).getEditor().level().setSettings(builder.build());
            timelinePanel.runTimelineFixedAction();
        });

        add(groupPanel);
    }

    public CSValueInputGroupPanel getGroupPanel() {
        return groupPanel;
    }
}

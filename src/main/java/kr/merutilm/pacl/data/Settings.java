package kr.merutilm.pacl.data;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.*;
import kr.merutilm.pacl.al.event.selectable.*;

public record Settings(
        int version,
        String artist,
        String specialArtistType,
        String artistPermission,
        String song,
        String author,
        boolean separateCountdownTime,
        ImageFile previewImage,
        ImageFile previewIcon,
        HexColor previewIconColor,
        int previewSongStart,
        int previewSongDuration,
        boolean seizureWarning,
        String levelDesc,
        String levelTags,
        String artistLinks,
        double speedTrialAim,
        int difficulty,
        String requireMods,
        String songFilename,
        double bpm,
        int volume,
        int offset,
        int pitch,
        HitSound hitSound,
        int hitSoundVolume,
        int countdownTicks,
        TrackColorType trackColorType,
        HexColor trackColor,
        HexColor secondaryTrackColor,
        double trackColorAnimDuration,
        TrackColorPulse trackColorPulse,
        int trackPulseLength,
        TrackStyle trackStyle,
        ImageFile trackTexture,
        double trackTextureScale,
        int trackGlowIntensity,
        TrackAppearAnimation trackAnimation,
        double beatsAhead,
        TrackDisappearAnimation trackDisappearAnimation,
        double beatsBehind,
        HexColor backgroundColor,
        boolean showDefaultBGIfNoImage,
        boolean showDefaultBGTile,
        HexColor defaultBGTileColor,
        BGShapeType defaultBGShapeType,
        HexColor defaultBGShapeColor,
        ImageFile bgImage,
        HexColor bgImageColor,
        Point2D parallax,
        BGDisplayMode bgDisplayMode,
        boolean imageSmoothing,
        boolean lockRot,
        boolean loopBG,
        int scalingRatio,
        RelativeTo.Camera relativeTo,
        Point2D position,
        double rotation,
        double zoom,
        boolean pulseOnFloor,
        ImageFile bgVideo,
        boolean loopVideo,
        int videoOffset,
        boolean iconOutLines,
        boolean stickToFloors,
        Ease planetEase,
        int planetEaseParts,
        EasePartBehavior planetEasePartBehavior,
        HexColor defaultTextColor,
        HexColor defaultTextShadowColor,
        String congratsText,
        String perfectText,
        boolean legacyFlash,
        boolean legacyCamRelativeTo,
        boolean legacySpriteTiles) implements Struct<Settings> {

    @Override
    public Builder edit() {
        return new Builder()
                .setVersion(version)
                .setArtist(artist)
                .setSpecialArtistType(specialArtistType)
                .setArtistPermission(artistPermission)
                .setSong(song)
                .setAuthor(author)
                .setSeparateCountdownTime(separateCountdownTime)
                .setPreviewImage(previewImage)
                .setPreviewIcon(previewIcon)
                .setPreviewIconColor(previewIconColor)
                .setPreviewSongStart(previewSongStart)
                .setPreviewSongDuration(previewSongDuration)
                .setSeizureWarning(seizureWarning)
                .setLevelDesc(levelDesc)
                .setLevelTags(levelTags)
                .setArtistLinks(artistLinks)
                .setSpeedTrialAim(speedTrialAim)
                .setDifficulty(difficulty)
                .setRequireMods(requireMods)
                .setSongFilename(songFilename)
                .setBpm(bpm)
                .setVolume(volume)
                .setOffset(offset)
                .setPitch(pitch)
                .setHitSound(hitSound)
                .setHitSoundVolume(hitSoundVolume)
                .setCountdownTicks(countdownTicks)
                .setTrackColorType(trackColorType)
                .setTrackColor(trackColor)
                .setSecondaryTrackColor(secondaryTrackColor)
                .setTrackColorAnimDuration(trackColorAnimDuration)
                .setTrackColorPulse(trackColorPulse)
                .setTrackPulseLength(trackPulseLength)
                .setTrackStyle(trackStyle)
                .setTrackTexture(trackTexture)
                .setTrackTextureScale(trackTextureScale)
                .setTrackGlowIntensity(trackGlowIntensity)
                .setTrackAnimation(trackAnimation)
                .setBeatsAhead(beatsAhead)
                .setTrackDisappearAnimation(trackDisappearAnimation)
                .setBeatsBehind(beatsBehind)
                .setBackgroundColor(backgroundColor)
                .setShowDefaultBGIfNoImage(showDefaultBGIfNoImage)
                .setShowDefaultBGTile(showDefaultBGTile)
                .setDefaultBGTileColor(defaultBGTileColor)
                .setDefaultBGShapeType(defaultBGShapeType)
                .setDefaultBGShapeColor(defaultBGShapeColor)
                .setBgImage(bgImage)
                .setBgImageColor(bgImageColor)
                .setParallax(parallax)
                .setBgDisplayMode(bgDisplayMode)
                .setImageSmoothing(imageSmoothing)
                .setLockRot(lockRot)
                .setLoopBG(loopBG)
                .setScalingRatio(scalingRatio)
                .setRelativeTo(relativeTo)
                .setPosition(position)
                .setRotation(rotation)
                .setZoom(zoom)
                .setPulseOnFloor(pulseOnFloor)
                .setBgVideo(bgVideo)
                .setLoopVideo(loopVideo)
                .setVideoOffset(videoOffset)
                .setIconOutLines(iconOutLines)
                .setStickToFloors(stickToFloors)
                .setPlanetEase(planetEase)
                .setPlanetEaseParts(planetEaseParts)
                .setPlanetEasePartBehavior(planetEasePartBehavior)
                .setDefaultTextColor(defaultTextColor)
                .setDefaultTextShadowColor(defaultTextShadowColor)
                .setCongratsText(congratsText)
                .setPerfectText(perfectText)
                .setLegacyFlash(legacyFlash)
                .setLegacyCamRelativeTo(legacyCamRelativeTo)
                .setLegacySpriteTiles(legacySpriteTiles);
    }

    public static final class Builder implements StructBuilder<Settings> {
        private int version = FileIO.VERSION;
        private String artist = "Artist";
        private String specialArtistType = "None";
        private String artistPermission = "";
        private String song = "Song";
        private String author = "";
        private boolean separateCountdownTime = true;
        private ImageFile previewImage = new ImageFile("");
        private ImageFile previewIcon = new ImageFile("");
        private HexColor previewIconColor = HexColor.get(63, 82, 255);
        private int previewSongStart = 0;
        private int previewSongDuration = 10;
        private boolean seizureWarning = false;
        private String levelDesc = "Say something about your level";
        private String levelTags = "";
        private String artistLinks = "";
        private double speedTrialAim = 0;
        private int difficulty = 1;
        private String requireMods = "";
        private String songFilename = "";
        private double bpm = 100;
        private int volume = 100;
        private int offset = 0;
        private int pitch = 100;
        private HitSound hitSound = HitSound.KICK;
        private int hitSoundVolume = 100;
        private int countdownTicks = 4;
        private TrackColorType trackColorType = TrackColorType.SINGLE;
        private HexColor trackColor = HexColor.get(222, 187, 123);
        private HexColor secondaryTrackColor = HexColor.WHITE;
        private double trackColorAnimDuration = 2;
        private TrackColorPulse trackColorPulse = TrackColorPulse.NONE;
        private int trackPulseLength = 10;
        private TrackStyle trackStyle = TrackStyle.STANDARD;
        private ImageFile trackTexture = new ImageFile("");
        private double trackTextureScale = 1;
        private int trackGlowIntensity = 100;
        private TrackAppearAnimation trackAnimation = TrackAppearAnimation.NONE;
        private double beatsAhead = 3;
        private TrackDisappearAnimation trackDisappearAnimation = TrackDisappearAnimation.NONE;
        private double beatsBehind = 4;
        private HexColor backgroundColor = HexColor.get(0, 0, 0);
        private boolean showDefaultBGIfNoImage = true;
        private boolean showDefaultBGTile = true;
        private HexColor defaultBGTileColor = HexColor.get(16, 17, 33);
        private BGShapeType defaultBGShapeType = BGShapeType.DEFAULT;
        private HexColor defaultBGShapeColor = HexColor.WHITE;
        private ImageFile bgImage = new ImageFile("");
        private HexColor bgImageColor = HexColor.WHITE;
        private Point2D parallax = new Point2D(100, 100);
        private BGDisplayMode bgDisplayMode = BGDisplayMode.FIT_TO_SCREEN;
        private boolean imageSmoothing = true;
        private boolean lockRot = false;
        private boolean loopBG = false;
        private int scalingRatio = 100;
        private RelativeTo.Camera relativeTo = RelativeTo.Camera.PLAYER;
        private Point2D position = new Point2D(0, 0);
        private double rotation = 0;
        private double zoom = 100;
        private boolean pulseOnFloor = true;
        private ImageFile bgVideo = new ImageFile("");
        private boolean loopVideo = false;
        private int videoOffset = 0;
        private boolean iconOutLines = false;
        private boolean stickToFloors = true;
        private Ease planetEase = Ease.LINEAR;
        private int planetEaseParts = 1;
        private EasePartBehavior planetEasePartBehavior = EasePartBehavior.MIRROR;
        private HexColor defaultTextColor = HexColor.WHITE;
        private HexColor defaultTextShadowColor = HexColor.get(0, 0, 0, 80);
        private String congratsText = "";
        private String perfectText = "";
        private boolean legacyFlash = false;
        private boolean legacyCamRelativeTo = false;
        private boolean legacySpriteTiles = false;

        public Builder setVersion(int version) {
            this.version = version;
            return this;
        }

        public Builder setArtist(String artist) {
            this.artist = artist;
            return this;
        }

        public Builder setSpecialArtistType(String specialArtistType) {
            this.specialArtistType = specialArtistType;
            return this;
        }

        public Builder setArtistPermission(String artistPermission) {
            this.artistPermission = artistPermission;
            return this;
        }

        public Builder setSong(String song) {
            this.song = song;
            return this;
        }

        public Builder setAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder setSeparateCountdownTime(boolean separateCountdownTime) {
            this.separateCountdownTime = separateCountdownTime;
            return this;
        }

        public Builder setPreviewImage(ImageFile previewImage) {
            this.previewImage = previewImage;
            return this;
        }

        public Builder setPreviewIcon(ImageFile previewIcon) {
            this.previewIcon = previewIcon;
            return this;
        }

        public Builder setPreviewIconColor(HexColor previewIconColor) {
            this.previewIconColor = previewIconColor;
            return this;
        }

        public Builder setPreviewSongStart(int previewSongStart) {
            this.previewSongStart = previewSongStart;
            return this;
        }

        public Builder setPreviewSongDuration(int previewSongDuration) {
            this.previewSongDuration = previewSongDuration;
            return this;
        }

        public Builder setSeizureWarning(boolean seizureWarning) {
            this.seizureWarning = seizureWarning;
            return this;
        }

        public Builder setLevelDesc(String levelDesc) {
            this.levelDesc = levelDesc;
            return this;
        }

        public Builder setLevelTags(String levelTags) {
            this.levelTags = levelTags;
            return this;
        }

        public Builder setArtistLinks(String artistLinks) {
            this.artistLinks = artistLinks;
            return this;
        }

        public Builder setSpeedTrialAim(double speedTrialAim) {
            this.speedTrialAim = speedTrialAim;
            return this;
        }

        public Builder setDifficulty(int difficulty) {
            this.difficulty = difficulty;
            return this;
        }

        public Builder setRequireMods(String requireMods) {
            this.requireMods = requireMods;
            return this;
        }

        public Builder setSongFilename(String songFilename) {
            this.songFilename = songFilename;
            return this;
        }

        public Builder setBpm(double bpm) {
            this.bpm = bpm;
            return this;
        }

        public Builder setVolume(int volume) {
            this.volume = volume;
            return this;
        }

        public Builder setOffset(int offset) {
            this.offset = offset;
            return this;
        }

        public Builder setPitch(int pitch) {
            this.pitch = pitch;
            return this;
        }

        public Builder setHitSound(HitSound hitSound) {
            this.hitSound = hitSound;
            return this;
        }

        public Builder setHitSoundVolume(int hitSoundVolume) {
            this.hitSoundVolume = hitSoundVolume;
            return this;
        }

        public Builder setCountdownTicks(int countdownTicks) {
            this.countdownTicks = countdownTicks;
            return this;
        }

        public Builder setTrackColorType(TrackColorType trackColorType) {
            this.trackColorType = trackColorType;
            return this;
        }

        public Builder setTrackColor(HexColor trackColor) {
            this.trackColor = trackColor;
            return this;
        }

        public Builder setSecondaryTrackColor(HexColor secondaryTrackColor) {
            this.secondaryTrackColor = secondaryTrackColor;
            return this;
        }

        public Builder setTrackColorAnimDuration(double trackColorAnimDuration) {
            this.trackColorAnimDuration = trackColorAnimDuration;
            return this;
        }

        public Builder setTrackColorPulse(TrackColorPulse trackColorPulse) {
            this.trackColorPulse = trackColorPulse;
            return this;
        }

        public Builder setTrackPulseLength(int trackPulseLength) {
            this.trackPulseLength = trackPulseLength;
            return this;
        }

        public Builder setTrackStyle(TrackStyle trackStyle) {
            this.trackStyle = trackStyle;
            return this;
        }

        public Builder setTrackTexture(ImageFile trackTexture) {
            this.trackTexture = trackTexture;
            return this;
        }

        public Builder setTrackTextureScale(double trackTextureScale) {
            this.trackTextureScale = trackTextureScale;
            return this;
        }

        public Builder setTrackGlowIntensity(int trackGlowIntensity) {
            this.trackGlowIntensity = trackGlowIntensity;
            return this;
        }

        public Builder setTrackAnimation(TrackAppearAnimation trackAnimation) {
            this.trackAnimation = trackAnimation;
            return this;
        }

        public Builder setBeatsAhead(double beatsAhead) {
            this.beatsAhead = beatsAhead;
            return this;
        }

        public Builder setTrackDisappearAnimation(TrackDisappearAnimation trackDisappearAnimation) {
            this.trackDisappearAnimation = trackDisappearAnimation;
            return this;
        }

        public Builder setBeatsBehind(double beatsBehind) {
            this.beatsBehind = beatsBehind;
            return this;
        }

        public Builder setBackgroundColor(HexColor backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder setShowDefaultBGIfNoImage(boolean showDefaultBGIfNoImage) {
            this.showDefaultBGIfNoImage = showDefaultBGIfNoImage;
            return this;
        }

        public Builder setShowDefaultBGTile(boolean showDefaultBGTile) {
            this.showDefaultBGTile = showDefaultBGTile;
            return this;
        }

        public Builder setDefaultBGTileColor(HexColor defaultBGTileColor) {
            this.defaultBGTileColor = defaultBGTileColor;
            return this;
        }

        public Builder setDefaultBGShapeType(BGShapeType defaultBGShapeType) {
            this.defaultBGShapeType = defaultBGShapeType;
            return this;
        }

        public Builder setDefaultBGShapeColor(HexColor defaultBGShapeColor) {
            this.defaultBGShapeColor = defaultBGShapeColor;
            return this;
        }

        public Builder setBgImage(ImageFile bgImage) {
            this.bgImage = bgImage;
            return this;
        }

        public Builder setBgImageColor(HexColor bgImageColor) {
            this.bgImageColor = bgImageColor;
            return this;
        }

        public Builder setParallax(Point2D parallax) {
            this.parallax = parallax;
            return this;
        }

        public Builder setBgDisplayMode(BGDisplayMode bgDisplayMode) {
            this.bgDisplayMode = bgDisplayMode;
            return this;
        }

        public Builder setImageSmoothing(boolean imageSmoothing) {
            this.imageSmoothing = imageSmoothing;
            return this;
        }

        public Builder setLockRot(boolean lockRot) {
            this.lockRot = lockRot;
            return this;
        }

        public Builder setLoopBG(boolean loopBG) {
            this.loopBG = loopBG;
            return this;
        }

        public Builder setScalingRatio(int scalingRatio) {
            this.scalingRatio = scalingRatio;
            return this;
        }

        public Builder setRelativeTo(RelativeTo.Camera relativeTo) {
            this.relativeTo = relativeTo;
            return this;
        }

        public Builder setPosition(Point2D position) {
            this.position = position;
            return this;
        }

        public Builder setRotation(double rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder setZoom(double zoom) {
            this.zoom = zoom;
            return this;
        }

        public Builder setPulseOnFloor(boolean pulseOnFloor) {
            this.pulseOnFloor = pulseOnFloor;
            return this;
        }

        public Builder setBgVideo(ImageFile bgVideo) {
            this.bgVideo = bgVideo;
            return this;
        }

        public Builder setLoopVideo(boolean loopVideo) {
            this.loopVideo = loopVideo;
            return this;
        }

        public Builder setVideoOffset(int videoOffset) {
            this.videoOffset = videoOffset;
            return this;
        }

        public Builder setIconOutLines(boolean iconOutLines) {
            this.iconOutLines = iconOutLines;
            return this;
        }

        public Builder setStickToFloors(boolean stickToFloors) {
            this.stickToFloors = stickToFloors;
            return this;
        }

        public Builder setPlanetEase(Ease planetEase) {
            this.planetEase = planetEase;
            return this;
        }

        public Builder setPlanetEaseParts(int planetEaseParts) {
            this.planetEaseParts = planetEaseParts;
            return this;
        }

        public Builder setPlanetEasePartBehavior(EasePartBehavior planetEasePartBehavior) {
            this.planetEasePartBehavior = planetEasePartBehavior;
            return this;
        }

        public Builder setDefaultTextColor(HexColor defaultTextColor) {
            this.defaultTextColor = defaultTextColor;
            return this;
        }

        public Builder setDefaultTextShadowColor(HexColor defaultTextShadowColor) {
            this.defaultTextShadowColor = defaultTextShadowColor;
            return this;
        }

        public Builder setCongratsText(String congratsText) {
            this.congratsText = congratsText;
            return this;
        }

        public Builder setPerfectText(String perfectText) {
            this.perfectText = perfectText;
            return this;
        }

        public Builder setLegacyFlash(boolean legacyFlash) {
            this.legacyFlash = legacyFlash;
            return this;
        }

        public Builder setLegacyCamRelativeTo(boolean legacyCamRelativeTo) {
            this.legacyCamRelativeTo = legacyCamRelativeTo;
            return this;
        }

        public Builder setLegacySpriteTiles(boolean legacySpriteTiles) {
            this.legacySpriteTiles = legacySpriteTiles;
            return this;
        }

        public Settings build() {
            return new Settings(version, artist, specialArtistType, artistPermission, song, author, separateCountdownTime, previewImage,
                    previewIcon, previewIconColor, previewSongStart, previewSongDuration, seizureWarning, levelDesc, levelTags, artistLinks, speedTrialAim,
                    difficulty, requireMods, songFilename, bpm, volume, offset, pitch, hitSound, hitSoundVolume, countdownTicks, trackColorType,
                    trackColor, secondaryTrackColor, trackColorAnimDuration, trackColorPulse, trackPulseLength, trackStyle, trackTexture, trackTextureScale,
                    trackGlowIntensity, trackAnimation, beatsAhead, trackDisappearAnimation, beatsBehind, backgroundColor, showDefaultBGIfNoImage,
                    showDefaultBGTile, defaultBGTileColor, defaultBGShapeType, defaultBGShapeColor, bgImage, bgImageColor, parallax,
                    bgDisplayMode, imageSmoothing, lockRot, loopBG, scalingRatio, relativeTo, position, rotation, zoom, pulseOnFloor,
                    bgVideo, loopVideo, videoOffset, iconOutLines, stickToFloors, planetEase,
                    planetEaseParts, planetEasePartBehavior, defaultTextColor, defaultTextShadowColor, congratsText, perfectText,
                    legacyFlash, legacyCamRelativeTo, legacySpriteTiles);
        }
    }
}

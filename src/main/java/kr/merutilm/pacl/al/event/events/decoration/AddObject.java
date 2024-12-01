package kr.merutilm.pacl.al.event.events.decoration;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.Point2D;
import kr.merutilm.pacl.al.event.selectable.*;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;


public record AddObject(Boolean visible,
                        Boolean locked,
                        ObjectType objectType,
                        PlanetColorType planetColorType,
                        HexColor planetColor,
                        HexColor planetTailColor,
                        TrackType trackType,
                        Double trackAngle,
                        HexColor trackColor,
                        Double trackOpacity,
                        TrackStyle trackStyle,
                        TrackIcon trackIcon,
                        Double trackIconAngle,
                        Boolean trackRedSwirl,
                        Boolean trackGraySetSpeedIcon,
                        Double trackSetSpeedIconBpm,
                        Boolean trackGlowEnabled,
                        HexColor trackGlowColor,
                        Point2D position,
                        RelativeTo.AddDecoration relativeTo,
                        Point2D pivotOffset,
                        Double rotation,
                        Boolean lockRotation,
                        Point2D scale,
                        Boolean lockScale,
                        Short depth,
                        Point2D parallax,
                        Point2D parallaxOffset,
                        Tag tag
) implements Decorations {

    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setVisible(visible)
                .setLocked(locked)
                .setObjectType(objectType)
                .setPlanetColorType(planetColorType)
                .setPlanetColor(planetColor)
                .setPlanetTailColor(planetTailColor)
                .setTrackType(trackType)
                .setTrackAngle(trackAngle)
                .setTrackColor(trackColor)
                .setTrackOpacity(trackOpacity)
                .setTrackStyle(trackStyle)
                .setTrackIcon(trackIcon)
                .setTrackIconAngle(trackIconAngle)
                .setTrackRedSwirl(trackRedSwirl)
                .setTrackGraySetSpeedIcon(trackGraySetSpeedIcon)
                .setTrackSetSpeedIconBpm(trackSetSpeedIconBpm)
                .setTrackGlowEnabled(trackGlowEnabled)
                .setTrackGlowColor(trackGlowColor)
                .setPosition(position)
                .setRelativeTo(relativeTo)
                .setPivotOffset(pivotOffset)
                .setRotation(rotation)
                .setLockRotation(lockRotation)
                .setScale(scale)
                .setLockScale(lockScale)
                .setDepth(depth)
                .setParallax(parallax)
                .setParallaxOffset(parallaxOffset)
                .setTag(tag);
    }

    public static final class Builder implements DecorationsBuilder {
        private Boolean visible;
        private Boolean locked;
        private ObjectType objectType;
        private PlanetColorType planetColorType;
        private HexColor planetColor;
        private HexColor planetTailColor;
        private TrackType trackType;
        private Double trackAngle;
        private HexColor trackColor;
        private Double trackOpacity;
        private TrackStyle trackStyle;
        private TrackIcon trackIcon;
        private Double trackIconAngle;
        private Boolean trackRedSwirl;
        private Boolean trackGraySetSpeedIcon;
        private Double trackSetSpeedIconBpm;
        private Boolean trackGlowEnabled;
        private HexColor trackGlowColor;
        private Point2D position;
        private RelativeTo.AddDecoration relativeTo;
        private Point2D pivotOffset;
        private Double rotation;
        private Boolean lockRotation;
        private Point2D scale;
        private Boolean lockScale;
        private Short depth;
        private Point2D parallax;
        private Point2D parallaxOffset;
        private Tag tag;


        @Override
        public Builder setVisible(Boolean visible) {
            this.visible = visible;
            return this;
        }

        @Override
        public Builder setLocked(Boolean locked) {
            this.locked = locked;
            return this;
        }

        public Builder setObjectType(ObjectType objectType) {
            this.objectType = objectType;
            return this;
        }

        public Builder setPlanetColorType(PlanetColorType planetColorType) {
            this.planetColorType = planetColorType;
            return this;
        }

        public Builder setPlanetColor(HexColor planetColor) {
            this.planetColor = planetColor;
            return this;
        }

        public Builder setPlanetTailColor(HexColor planetTailColor) {
            this.planetTailColor = planetTailColor;
            return this;
        }

        public Builder setTrackType(TrackType trackType) {
            this.trackType = trackType;
            return this;
        }

        public Builder setTrackAngle(Double trackAngle) {
            this.trackAngle = trackAngle;
            return this;
        }

        public Builder setTrackColor(HexColor trackColor) {
            this.trackColor = trackColor;
            return this;
        }

        public Builder setTrackOpacity(Double trackOpacity) {
            this.trackOpacity = trackOpacity;
            return this;
        }

        public Builder setTrackStyle(TrackStyle trackStyle) {
            this.trackStyle = trackStyle;
            return this;
        }

        public Builder setTrackIcon(TrackIcon trackIcon) {
            this.trackIcon = trackIcon;
            return this;
        }

        public Builder setTrackIconAngle(Double trackIconAngle) {
            this.trackIconAngle = trackIconAngle;
            return this;
        }

        public Builder setTrackRedSwirl(Boolean trackRedSwirl) {
            this.trackRedSwirl = trackRedSwirl;
            return this;
        }

        public Builder setTrackGraySetSpeedIcon(Boolean trackGraySetSpeedIcon) {
            this.trackGraySetSpeedIcon = trackGraySetSpeedIcon;
            return this;
        }

        public Builder setTrackSetSpeedIconBpm(Double trackSetSpeedIconBpm) {
            this.trackSetSpeedIconBpm = trackSetSpeedIconBpm;
            return this;
        }

        public Builder setTrackGlowEnabled(Boolean trackGlowEnabled) {
            this.trackGlowEnabled = trackGlowEnabled;
            return this;
        }

        public Builder setTrackGlowColor(HexColor trackGlowColor) {
            this.trackGlowColor = trackGlowColor;
            return this;
        }

        public Builder setPosition(Point2D position) {
            this.position = position;
            return this;
        }

        @Override
        public Builder setRelativeTo(RelativeTo.AddDecoration relativeTo) {
            this.relativeTo = relativeTo;
            return this;
        }

        public Builder setPivotOffset(Point2D pivotOffset) {
            this.pivotOffset = pivotOffset;
            return this;
        }

        public Builder setRotation(Double rotation) {
            this.rotation = rotation;
            return this;
        }

        public Builder setLockRotation(Boolean lockRotation) {
            this.lockRotation = lockRotation;
            return this;
        }

        public Builder setScale(Point2D scale) {
            this.scale = scale;
            return this;
        }

        public Builder setLockScale(Boolean lockScale) {
            this.lockScale = lockScale;
            return this;
        }

        public Builder setDepth(Short depth) {
            this.depth = depth;
            return this;
        }

        public Builder setParallax(Point2D parallax) {
            this.parallax = parallax;
            return this;
        }

        public Builder setParallaxOffset(Point2D parallaxOffset) {
            this.parallaxOffset = parallaxOffset;
            return this;
        }

        @Override
        public Builder setTag(Tag tag) {
            this.tag = tag;
            return this;
        }

        @Override
        public AddObject build() {
            return new AddObject(visible, locked, objectType, planetColorType, planetColor, planetTailColor, trackType, trackAngle, trackColor, trackOpacity, trackStyle, trackIcon, trackIconAngle, trackRedSwirl, trackGraySetSpeedIcon, trackSetSpeedIconBpm, trackGlowEnabled, trackGlowColor, position, relativeTo, pivotOffset, rotation, lockRotation, scale, lockScale, depth, parallax, parallaxOffset, tag);
        }
    }

}

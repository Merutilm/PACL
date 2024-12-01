package kr.merutilm.pacl.al.event.events.action;

import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.pacl.al.event.events.TrackColorEvent;
import kr.merutilm.pacl.al.event.selectable.TrackColorPulse;
import kr.merutilm.pacl.al.event.selectable.TrackColorType;
import kr.merutilm.pacl.al.event.selectable.TrackStyle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record ColorTrack(Boolean active,
                         TrackColorType trackColorType,
                         HexColor trackColor,
                         HexColor secondaryTrackColor,
                         Double trackColorAnimDuration,
                         TrackColorPulse trackColorPulse,
                         Integer trackPulseLength,
                         TrackStyle trackStyle,
                         ImageFile trackTexture,
                         Double trackTextureScale,
                         Integer trackGlowIntensity) implements Actions, TrackColorEvent {

    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setTrackColorType(trackColorType)
                .setTrackColor(trackColor)
                .setSecondaryTrackColor(secondaryTrackColor)
                .setTrackColorAnimDuration(trackColorAnimDuration)
                .setTrackColorPulse(trackColorPulse)
                .setTrackPulseLength(trackPulseLength)
                .setTrackStyle(trackStyle)
                .setTrackTexture(trackTexture)
                .setTrackTextureScale(trackTextureScale)
                .setTrackGlowIntensity(trackGlowIntensity);
    }


    public static final class Builder implements ActionsBuilder {
        private Boolean active;
        private TrackColorType trackColorType = TrackColorType.SINGLE;
        private HexColor trackColor = HexColor.get(222, 187, 123, 255);
        private HexColor secondaryTrackColor = HexColor.get(255, 255, 255, 255);
        private Double trackColorAnimDuration = 2d;
        private TrackColorPulse trackColorPulse = TrackColorPulse.NONE;
        private Integer trackPulseLength = 10;
        private TrackStyle trackStyle = TrackStyle.STANDARD;
        private ImageFile trackTexture;
        private Double trackTextureScale = 1d;
        private Integer trackGlowIntensity = 100;

        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        public Builder setTrackColorType(@Nullable TrackColorType trackColorType) {
            this.trackColorType = trackColorType;
            return this;
        }


        public Builder setTrackColor(@Nullable HexColor trackColor) {
            this.trackColor = trackColor;
            return this;
        }


        public Builder setSecondaryTrackColor(@Nullable HexColor secondaryTrackColor) {
            this.secondaryTrackColor = secondaryTrackColor;
            return this;
        }


        public Builder setTrackColorAnimDuration(@Nullable Double trackColorAnimDuration) {
            this.trackColorAnimDuration = trackColorAnimDuration;
            return this;
        }


        public Builder setTrackColorPulse(@Nullable TrackColorPulse trackColorPulse) {
            this.trackColorPulse = trackColorPulse;
            return this;
        }


        public Builder setTrackPulseLength(@Nullable Integer trackPulseLength) {
            this.trackPulseLength = trackPulseLength;
            return this;
        }


        public Builder setTrackStyle(@Nullable TrackStyle trackStyle) {
            this.trackStyle = trackStyle;
            return this;
        }


        public Builder setTrackTexture(@Nullable ImageFile trackTexture) {
            this.trackTexture = trackTexture;
            return this;
        }


        public Builder setTrackTextureScale(@Nullable Double trackTextureScale) {
            this.trackTextureScale = trackTextureScale;
            return this;
        }

        public Builder setTrackGlowIntensity(@Nullable Integer trackGlowIntensity) {
            this.trackGlowIntensity = trackGlowIntensity;
            return this;
        }

        @Override
        public ColorTrack build() {
            return new ColorTrack(active, trackColorType, trackColor, secondaryTrackColor, trackColorAnimDuration, trackColorPulse, trackPulseLength, trackStyle, trackTexture, trackTextureScale, trackGlowIntensity);
        }
    }
}

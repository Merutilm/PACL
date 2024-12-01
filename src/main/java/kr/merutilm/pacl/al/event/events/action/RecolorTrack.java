package kr.merutilm.pacl.al.event.events.action;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.pacl.al.event.events.*;
import kr.merutilm.pacl.al.event.selectable.*;
import kr.merutilm.pacl.al.event.struct.RelativeTile;
import kr.merutilm.pacl.al.event.struct.Tag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public record RecolorTrack(Boolean active,
                           RelativeTile startTile,
                           RelativeTile endTile,
                           Integer gap,
                           Double duration,
                           TrackColorType trackColorType,
                           HexColor trackColor,
                           HexColor secondaryTrackColor,
                           Double trackColorAnimDuration,
                           TrackColorPulse trackColorPulse,
                           Integer trackPulseLength,
                           TrackStyle trackStyle,
                           Integer trackGlowIntensity,
                           Double angleOffset,
                           Ease ease,
                           Tag eventTag) implements Actions, AngleOffsetEvent, DoubleDurationEvent, EventTagEvent, TrackColorEvent, EaseEvent, RelativeTileEvent {


    @Nonnull
    @Override
    public Builder edit() {
        return new Builder()
                .setStartTile(startTile)
                .setEndTile(endTile)
                .setGap(gap)
                .setDuration(duration)
                .setTrackColorType(trackColorType)
                .setTrackColor(trackColor)
                .setSecondaryTrackColor(secondaryTrackColor)
                .setTrackColorAnimDuration(trackColorAnimDuration)
                .setTrackColorPulse(trackColorPulse)
                .setTrackPulseLength(trackPulseLength)
                .setTrackStyle(trackStyle)
                .setTrackGlowIntensity(trackGlowIntensity)
                .setAngleOffset(angleOffset)
                .setEase(ease)
                .setEventTag(eventTag);
    }

    public static final class Builder implements ActionsBuilder, DoubleDurationEventBuilder, AngleOffsetEventBuilder, EventTagEventBuilder, EaseEventBuilder, RelativeTileEventBuilder {
        private Boolean active;
        private RelativeTile startTile = new RelativeTile(0, RelativeTo.Tile.THIS_TILE);
        private RelativeTile endTile = new RelativeTile(0, RelativeTo.Tile.THIS_TILE);
        private Integer gap = 0;
        private Double duration = 0d;
        private TrackColorType trackColorType = TrackColorType.SINGLE;
        private HexColor trackColor = HexColor.get(222, 187, 123, 255);
        private HexColor secondaryTrackColor = HexColor.get(255, 255, 255, 255);
        private Double trackColorAnimDuration = 2.0;
        private TrackColorPulse trackColorPulse = TrackColorPulse.NONE;
        private Integer trackPulseLength = 10;
        private TrackStyle trackStyle = TrackStyle.STANDARD;
        private Integer trackGlowIntensity = 100;
        private Double angleOffset = 0d;
        private Ease ease = Ease.LINEAR;
        private Tag eventTag = Tag.of();
        @Override
        public Builder setActive(@Nullable Boolean active) {
            this.active = active;
            return this;
        }
        @Override
        public Builder setStartTile(@Nullable RelativeTile startTile) {
            this.startTile = startTile;
            return this;
        }

        @Override
        public Builder setEndTile(@Nullable RelativeTile endTile) {
            this.endTile = endTile;
            return this;
        }

        @Override
        public Builder setGap(Integer gap) {
            this.gap = gap;
            return this;
        }

        @Override
        public Builder setDuration(Double duration) {
            this.duration = duration;
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

        public Builder setTrackGlowIntensity(@Nullable Integer trackGlowIntensity) {
            this.trackGlowIntensity = trackGlowIntensity;
            return this;
        }

        @Override
        public Builder setAngleOffset(@Nullable Double angleOffset) {
            this.angleOffset = angleOffset;
            return this;
        }

        @Override
        public Builder setEase(Ease ease) {
            this.ease = ease;
            return this;
        }

        @Override
        public Builder setEventTag(@Nullable Tag eventTag) {
            this.eventTag = eventTag;
            return this;
        }

        @Override
        public RecolorTrack build() {
            return new RecolorTrack(active, startTile, endTile, gap, duration, trackColorType, trackColor, secondaryTrackColor, trackColorAnimDuration, trackColorPulse, trackPulseLength, trackStyle, trackGlowIntensity, angleOffset, ease, eventTag);
        }
    }
}

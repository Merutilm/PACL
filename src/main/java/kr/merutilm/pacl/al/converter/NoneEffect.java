package kr.merutilm.pacl.al.converter;

import kr.merutilm.pacl.al.event.events.action.*;
import kr.merutilm.pacl.al.event.events.decoration.Decorations;
import kr.merutilm.pacl.al.event.struct.EID;
import kr.merutilm.pacl.data.CustomLevel;
import kr.merutilm.pacl.data.Settings;

public record NoneEffect(boolean retainMoveTrackEvent,
                         boolean retainRepeatEvent,
                         boolean retainPositionTrack,
                         boolean retainConditionalEvent,
                         boolean retainColorTrack,
                         boolean retainMoveCamera) implements Converter {
    @Override
    public void convert(CustomLevel level) {
        Settings currentSettings = level.getSettings();
        level.setSettings(new Settings.Builder().build());

        for (int i = 0; i <= level.getLength(); i++) {
            for (int j = 0; j < level.getFloorActions(i).size(); j++) {
                Actions actions = level.getAction(i, j);
                if (actions instanceof MoveTrack moveTrack) {

                    if (retainMoveTrackEvent) {
                        MoveTrack fixed =
                                moveTrack.edit()
                                        .setOpacity(moveTrack.opacity() == null ? null : Math.min(moveTrack.opacity(), 100))
                                        .build();
                        level.setAction(new EID(i, j), fixed);
                    } else {
                        level.destroyAction(new EID(i, j));
                    }

                } else if (actions != null
                           && !actions.isRelatedToTiming()
                           && (!actions.eventTypeEquals(MoveCamera.class) || !retainMoveCamera)
                           && (!actions.eventTypeEquals(ColorTrack.class) || !retainColorTrack)
                           && (!actions.eventTypeEquals(PositionTrack.class) || !retainPositionTrack)
                           && !actions.eventTypeEquals(AnimateTrack.class)
                           && !actions.eventTypeEquals(Bookmark.class)
                           && (!actions.eventTypeEquals(RepeatEvents.class) || !retainRepeatEvent)
                           && !actions.eventTypeEquals(ScaleRadius.class)
                           && (!actions.eventTypeEquals(SetConditionalEvents.class) || !retainConditionalEvent)) {

                    level.destroyAction(i, j);
                }
            }
            for (int j = 0; j < level.getFloorDecorations(i).size(); j++) {
                Decorations decorations = level.getDecoration(i, j);
                if (decorations != null && !decorations.isRelatedToTiming()) {
                    level.destroyDecoration(i, j);
                }
            }
        }
        level.setSettings(level.getSettings().edit()
                .setBpm(currentSettings.bpm())
                .setSong(currentSettings.song())
                .setSongFilename(currentSettings.songFilename())
                .setArtist(currentSettings.artist())
                .setArtistLinks(currentSettings.artistLinks())
                .setArtistPermission(currentSettings.artistPermission())
                .setOffset(currentSettings.offset())
                .setIconOutLines(currentSettings.iconOutLines())
                .setStickToFloors(currentSettings.stickToFloors())
                .setAuthor(currentSettings.author())
                .setSpecialArtistType(currentSettings.specialArtistType())
                .setVersion(currentSettings.version())
                .setShowDefaultBGIfNoImage(currentSettings.showDefaultBGIfNoImage())
                .setLegacyCamRelativeTo(currentSettings.legacyCamRelativeTo())
                .setLegacyFlash(currentSettings.legacyFlash())
                .setLegacySpriteTiles(currentSettings.legacySpriteTiles())
                .setLevelDesc(currentSettings.levelDesc())
                .setZoom(300)
                .build());
    }
}

package kr.merutilm.pacl.al.generator;

import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.pacl.al.event.events.*;
import kr.merutilm.pacl.al.event.events.action.*;
import kr.merutilm.pacl.al.event.events.decoration.Decorations;
import kr.merutilm.pacl.al.event.selectable.Filter;
import kr.merutilm.pacl.al.event.struct.EID;
import kr.merutilm.pacl.data.CustomLevel;

public record Drunk(
        CustomLevel level,
        double angleErrorPercentage) implements Generator {


    @Override
    public CustomLevel generate() {


        boolean prevHide = false;
        double prevMultiplier = 1;

        CustomLevel result = CustomLevel.emptyLevel();


        result.setSettings(level.getSettings());

        for (int floor = 0; floor < level.getLength(); floor++) {

            double travel = level.getTravelAngle(floor);


            Pause p = level.getActivatedAction(floor, Pause.class);
            double lapAngle = 360;

            if (p != null) {
                lapAngle = 360 + p.duration() * 180;
            }

            double fixed = floor == 0 ? travel : Math.min(lapAngle, travel * AdvancedMath.random(1 - angleErrorPercentage / 100, 1 + angleErrorPercentage / 100));
            double currMultiplier = travel == 0 ? prevMultiplier : fixed / travel;

            double currBPM = level.getBPM(floor) * currMultiplier;
            boolean currHide = true;

            double relativeAngle = p == null ? travel * currMultiplier : travel * currMultiplier - p.duration() * 180;

            if (level.isTwirling(floor)) {
                result.createRelativeAngleExceptAnalysis(floor, 360 * (Math.floor(relativeAngle / 360) - (relativeAngle / 360 % 1 == 0 ? 1 : 0)) + 360 - relativeAngle % 360);
            } else {
                result.createRelativeAngleExceptAnalysis(floor, relativeAngle);
            }

            if (p != null) {
                result.createEvent(floor, p);
            }

            if (level.containsEvent(floor, SetSpeed.class) || level.containsEvent(floor, Twirl.class) || level.containsEvent(floor, MultiPlanet.class)) {
                currHide = false;
            }

            if (currHide != prevHide) {
                result.createEvent(floor, new Hide.Builder()
                        .setHideTileIcon(currHide)
                        .build()
                );
            }
            int speedID = -1;
            int twirlID = -1;
            if (level.containsEvent(floor, SetSpeed.class)) {
                speedID = level.getActionEIDs(floor, SetSpeed.class).get(0).address();
            }
            if (level.containsEvent(floor, Twirl.class)) {
                twirlID = level.getActionEIDs(floor, Twirl.class).get(0).address();
            }
            if (speedID == -1 || twirlID == -1) {
                twirlCreate(result, floor);
                speedCreate(result, floor, currBPM);
            } else {
                if (speedID > twirlID) {
                    twirlCreate(result, floor);
                    speedCreate(result, floor, currBPM);
                } else {
                    speedCreate(result, floor, currBPM);
                    twirlCreate(result, floor);
                }
            }


            for (int i = 0; i < level.getFloorActions(floor).size(); i++) {


                EID getID = new EID(floor, i);
                EID setID = new EID(floor, result.getFloorActions(floor).size());

                Actions actions = level.getActivatedAction(getID);

                if (actions == null || actions instanceof SetSpeed || actions instanceof Pause || actions instanceof Twirl) {
                    continue;
                }

                result.createEvent(floor, actions);

                if (actions instanceof SetFilter e) {
                    if (Boolean.TRUE.equals(e.disableOthers())) {

                        createFilter(result, floor, e.angleOffset() * currMultiplier);
                        continue;
                    }

                    if (e.filter() == Filter.LIGHT_WATER || e.filter() == Filter.WAVES) {
                        createFilter(result, floor, e.angleOffset() * currMultiplier);
                        continue;
                    }
                }

                ActionsBuilder builder = actions.edit();

                if (actions instanceof IntDurationEvent e && builder instanceof IntDurationEventBuilder b) {
                    b.setDuration((int) (e.duration() * currMultiplier));
                }
                if (actions instanceof DoubleDurationEvent e && builder instanceof DoubleDurationEventBuilder b) {
                    b.setDuration(e.duration() * currMultiplier);
                }
                if (actions instanceof AngleOffsetEvent e && builder instanceof AngleOffsetEventBuilder b) {
                    b.setAngleOffset(e.angleOffset() * currMultiplier);
                }
                if (actions instanceof RepeatEvents e && builder instanceof RepeatEvents.Builder b) {
                    b.setInterval(e.interval() * currMultiplier);
                }
                result.setAction(setID, builder.build());
            }


            for (int i = 0; i < level.getFloorDecorations(floor).size(); i++) {
                EID id = new EID(floor, i);
                Decorations decorations = level.getDecoration(id);
                if (decorations != null) {
                    result.createEvent(floor, decorations);
                }

            }


            prevHide = currHide;
            prevMultiplier = currMultiplier;

        }


        createFilter(result, 0, 0);
        return result;
    }

    private void twirlCreate(CustomLevel level, int floor) {
        if (this.level.containsEvent(floor, Twirl.class)) {
            level.createEvent(floor, new Twirl.Builder().build());
        }
    }

    private void createFilter(CustomLevel level, int floor, double angleOffset) {
        level.createEvent(floor, new SetFilter.Builder()
                .setIntensity(angleErrorPercentage * 2)
                .setFilter(Filter.LIGHT_WATER)
                .setAngleOffset(angleOffset)
                .build()
        );
        level.createEvent(floor, new SetFilter.Builder()
                .setIntensity(angleErrorPercentage * 20)
                .setFilter(Filter.WAVES)
                .setAngleOffset(angleOffset)
                .build()
        );
    }

    private void speedCreate(CustomLevel level, int floor, double bpm) {
        if (!this.level.isMidSpinTile(floor) && (floor > 0)) {
            level.createEvent(floor, new SetSpeed.Builder()
                    .setBpm(bpm)
                    .build()
            );

        }
    }

}

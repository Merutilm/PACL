package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public class RelativeTo {

    private RelativeTo(){

    }
    public enum MoveDecoration implements Selectable {
        TILE("Tile"),
        GLOBAL("Global"),
        FIRE_PLANET("RedPlanet"),
        ICE_PLANET("BluePlanet"),
        WIND_PLANET("GreenPlanet"),
        CAMERA("Camera"),
        CAMERA_ASPECT("CameraAspect"),
        LAST_POSITION("LastPosition");

        private final String name;

        @Override
        public String toString() {
            return name;
        }

        MoveDecoration(String name) {
            this.name = name;
        }

        public static MoveDecoration typeOf(String name) {
            return name == null ? null :  Arrays.stream(values())
                    .filter(value -> value.name.equals(name))
                    .findAny()
                    .orElseThrow(() -> new NullPointerException(name));
        }
    }

    public enum AddDecoration implements Selectable {
        TILE("Tile"),
        GLOBAL("Global"),
        FIRE_PLANET("RedPlanet"),
        ICE_PLANET("BluePlanet"),
        WIND_PLANET("GreenPlanet"),
        CAMERA("Camera"),
        CAMERA_ASPECT("CameraAspect");
        private final String name;

        @Override
        public String toString() {
            return name;
        }

        AddDecoration(String name) {
            this.name = name;
        }

        public static AddDecoration typeOf(String name) {
            return name == null ? null : Arrays.stream(values())
                    .filter(value -> value.name.equals(name))
                    .findAny()
                    .orElseThrow(() -> new NullPointerException(name));
        }

    }

    public enum Camera implements Selectable {
        PLAYER("Player"),
        TILE("Tile"),
        GLOBAL("Global"),
        LAST_POSITION("LastPosition"),
        LAST_POSITION_NO_ROTATION("LastPositionNoRotation");


        private final String name;

        @Override
        public String toString() {
            return name;
        }

        Camera(String name) {
            this.name = name;
        }

        public static Camera typeOf(String name) {
            return name == null ? null : Arrays.stream(values())
                    .filter(value -> value.name.equals(name))
                    .findAny()
                    .orElseThrow(() -> new NullPointerException(name));
        }
    }

    public enum Tile implements Selectable {
        THIS_TILE("ThisTile"),
        START("Start"),
        END("End");
        private final String name;

        @Override
        public String toString() {
            return name;
        }

        Tile(String name) {
            this.name = name;
        }

        public static Tile typeOf(String name) {
            return name == null ? null : Arrays.stream(values())
                    .filter(value -> value.name.equals(name))
                    .findAny()
                    .orElseThrow(() -> new NullPointerException(name));
        }
    }
}

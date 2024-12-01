package kr.merutilm.pacl.al.event.selectable;

import java.util.Arrays;

import kr.merutilm.base.selectable.Selectable;

public enum HitSound implements Selectable {
    HAT("Hat"),
    KICK("Kick"),
    SHAKER("Shaker"),
    SIZZLE("Sizzle"),
    CHUCK("Chuck"),
    SHAKER_LOUD("ShakerLoud"),
    NONE("None"),
    HAMMER("Hammer"),
    KICK_CHROMA("KickChroma"),
    SNARE_ACOUSTIC_2("SnareAcoustic2"),
    SIDE_STICK("Sidestick"),
    STICK("Stick"),
    REVERB_CLACK("ReverbClack"),
    SQUARE_SHOT("Squareshot"),
    SLOW_DOWN("PowerDown"),
    SPEED_UP("PowerUp"),
    KICK_HOUSE("KickHouse"),
    KICK_RUPTURE("KickRupture"),
    HAT_HOUSE("HatHouse"),
    SNARE_HOUSE("SnareHouse"),
    SNARE_VAPOR("SnareVapor"),
    CLAP_HIT("ClapHit"),
    CLAP_HIT_ECHO("ClapHitEcho"),
    REVERB_CLAP("ReverbClap"),
    FIRE_TILE("FireTile"),
    ICE_TILE("IceTile"),
    MORE_PLANET("VehiclePositive"),
    LESS_PLANET("VehicleNegative");


    private final String name;

    @Override
    public String toString() {
        return name;
    }

    HitSound(String name) {
        this.name = name;
    }

    public static HitSound typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}

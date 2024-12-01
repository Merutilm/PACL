package kr.merutilm.pacl.al.event.selectable;

import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.selectable.Selectable;
import kr.merutilm.base.struct.HexColor;
import kr.merutilm.base.struct.Range;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.base.util.ArrayFunction;
import kr.merutilm.pacl.al.event.events.Event;
import kr.merutilm.pacl.al.event.events.action.Bloom;
import kr.merutilm.pacl.al.event.events.action.Flash;
import kr.merutilm.pacl.al.event.events.action.HallOfMirrors;
import kr.merutilm.pacl.al.event.events.action.SetFilter;

import java.util.*;
import java.util.stream.Collectors;

public enum Filter implements Selectable {
    GRAYSCALE("Grayscale", 100,0),
    SEPIA("Sepia", 20,0),
    INVERT_COLORS("Invert", 100,0),
    VHS("VHS", 20,0),
    TV80("EightiesTV", 100,0),
    TV50("FiftiesTV", 15,0),
    ARCADE("Arcade", 30,0),
    LED("LED", 100,100),
    RAIN("Rain", 100,0),
    BLIZZARD("Blizzard", 60,100),
    PIXEL_SNOW("PixelSnow", 0,0),
    COMPRESSION("Compression", 0,0),
    GLITCH("Glitch", 0,0),
    PIXELATE("Pixelate", 20,0.1),
    WAVES("Waves", 0,0),
    STATIC("Static", 10,0),
    FILM_GRAIN("Grain", 100,0),
    MOTION_BLUR("MotionBlur", 100,0),
    FISHEYE("Fisheye", 90,50),
    ABERRATION("Aberration", 100,50),
    DRAWING("Drawing", 10,0),
    NEON("Neon", 50,0),
    HANDHELD_8BIT("Handheld", 0,0),
    NIGHT_VISION("NightVision", 0,0),
    FUNK("Funk", 2,0),
    TUNNEL("Tunnel", 0,0),
    WEIRD_3D("Weird3D", 0,0),
    BLUR("Blur", 0,0),
    BLUR_FOCUS("BlurFocus", 0,0),
    GAUSSIAN_BLUR("GaussianBlur", 0,0),
    HEXAGON_BLACK("HexagonBlack", 20,0),
    POSTER("Posterize", 100,0),
    SHARPEN("Sharpen", 100,0),
    CONTRAST("Contrast", 250,0),
    EDGE_BLACK_LINE("EdgeBlackLine", 30,0),
    OIL_PAINT("OilPaint", 60,0),
    SUPER_DOT("SuperDot", 5,0),
    WATER_DROP("WaterDrop", 90,0),
    LIGHT_WATER("LightWater", 80,0),
    FALLING_PETALS("Petals", 0,0),
    PETALS_INSTANT("PetalsInstant", 5,0);

    private final String name;
    private final double relativeP;
    private final double defaultIntensity;
    private double getRelativeP() {
        return relativeP;
    }
    public double defaultIntensity(){
        return defaultIntensity;
    }
    @Override
    public String toString() {
        return name;
    }

    Filter(String name, double relativeP, double defaultIntensity) {
        this.name = name;
        this.relativeP = relativeP;
        this.defaultIntensity = defaultIntensity;
    }

    public static List<Event> random(int amount, double angleOffset) {

        List<Double> probabilityList = Arrays.stream(Filter.values()).map(Filter::getRelativeP).collect(Collectors.toList());
        if(probabilityList.stream().filter(e -> e != 0.0).toList().size() < amount + 5){
            throw new IllegalArgumentException("TOO LARGE!!");
        }

        Set<Filter> currentFilter = new HashSet<>();
        List<Event> currentEvent = new ArrayList<>();

        int tvAmount = 0;

        //Filter Area
        for (int i = 0; i < amount; i++) {

            double total = probabilityList.stream().mapToDouble(v -> v).sum();
            double randomValue = AdvancedMath.doubleRandom(total);
            List<Double> pList = generatePList(probabilityList);
            int index = ArrayFunction.searchIndex(pList, randomValue);
            Filter filter = Filter.values()[index];
            currentFilter.add(filter);
            probabilityList.set(index, 0d);

            double intensity = 0;
            switch (filter) {
                case LED -> intensity = AdvancedMath.isExecuted(20) ? -11 : AdvancedMath.random(20, 100);
                case VHS -> intensity = AdvancedMath.random(-10, 10);
                case RAIN -> intensity = Range.random(new Range(-30, -10), new Range(2, 5));
                case SEPIA -> {
                    if (currentFilter.contains(Filter.NEON)) {
                        intensity = AdvancedMath.isExecuted(20) ? AdvancedMath.random(300, 500) : AdvancedMath.random(-80, -10);
                    } else {
                        intensity = AdvancedMath.random(-110, -10);
                    }
                }
                case POSTER -> {
                    intensity = AdvancedMath.random(50, 140);
                    probabilityList.set(Filter.FILM_GRAIN.ordinal(), probabilityList.get(Filter.FILM_GRAIN.ordinal()) * 2);
                }
                case STATIC -> intensity = AdvancedMath.random(-10, 4);
                case DRAWING -> intensity = AdvancedMath.random(5, 12);
                case FISHEYE -> intensity = AdvancedMath.random(48, 50);
                case SHARPEN ->
                        intensity = AdvancedMath.isExecuted(80) ? AdvancedMath.random(50, 100) : AdvancedMath.random(120000, 240000);
                case BLIZZARD -> intensity = AdvancedMath.isExecuted(90) ? AdvancedMath.random(100, 200) : 7777777;
                case CONTRAST -> intensity = AdvancedMath.random(20, 50);
                case PIXELATE -> intensity = AdvancedMath.random(0.1, 10);
                case OIL_PAINT -> intensity = AdvancedMath.random(30, 100);
                case ABERRATION -> intensity = AdvancedMath.isExecuted(50) ? 45 : 55;
                case BLUR_FOCUS -> intensity = 100;
                case FILM_GRAIN -> intensity = AdvancedMath.random(20, 60);
                case GRAYSCALE -> intensity = AdvancedMath.isExecuted(20) ? 100 : AdvancedMath.random(-100, 200);
                case WATER_DROP, HEXAGON_BLACK -> intensity = 20;
                case LIGHT_WATER -> intensity = AdvancedMath.random(1, 5);
                case NEON -> {
                    probabilityList.set(Filter.DRAWING.ordinal(), probabilityList.get(Filter.DRAWING.ordinal()) * 10);
                    probabilityList.set(Filter.TV80.ordinal(), probabilityList.get(Filter.TV80.ordinal()) * 1.5);
                    probabilityList.set(Filter.TV50.ordinal(), probabilityList.get(Filter.TV50.ordinal()) * 3);
                    probabilityList.set(Filter.ARCADE.ordinal(), probabilityList.get(Filter.ARCADE.ordinal()) * 1.4);
                }
                case MOTION_BLUR -> intensity = AdvancedMath.random(24, 300, Ease.IN_CIRCLE.fun());
                case TV80 -> {
                    probabilityList.set(Filter.DRAWING.ordinal(), probabilityList.get(Filter.DRAWING.ordinal()) * 3);
                    probabilityList.set(Filter.TV50.ordinal(), probabilityList.get(Filter.TV50.ordinal()) * 0.05);
                    probabilityList.set(Filter.ARCADE.ordinal(), probabilityList.get(Filter.ARCADE.ordinal()) * 0.2);
                    tvAmount++;
                }
                case ARCADE -> {
                    probabilityList.set(Filter.DRAWING.ordinal(), 0d);
                    probabilityList.set(Filter.TV50.ordinal(), probabilityList.get(Filter.TV50.ordinal()) * 0.05);
                    probabilityList.set(Filter.TV80.ordinal(), probabilityList.get(Filter.TV80.ordinal()) * 0.7);
                    tvAmount++;
                }
                case TV50 -> {
                    probabilityList.set(Filter.DRAWING.ordinal(), 0d);
                    probabilityList.set(Filter.TV80.ordinal(), probabilityList.get(Filter.TV80.ordinal()) * 0.05);
                    probabilityList.set(Filter.ARCADE.ordinal(), probabilityList.get(Filter.ARCADE.ordinal()) * 0.7);
                    tvAmount++;
                }
                default -> intensity = 0;
            }


            SetFilter.Builder filterPreset = new SetFilter.Builder()
                    .setDuration(0d)
                    .setAngleOffset(0d);

            if (i == 0) {
                filterPreset.setDisableOthers(true);
            }

            currentEvent.add(
                    filterPreset.setFilter(filter)
                            .setIntensity(intensity)
                            .setAngleOffset(angleOffset)
                            .build()
            );

        }

        //Flash Area
        currentEvent.add(new HallOfMirrors.Builder().build());

        HexColor color;
        if ((currentFilter.contains(NEON) && AdvancedMath.isExecuted(10))
                || currentFilter.contains(INVERT_COLORS) && AdvancedMath.isExecuted(90)) {
            color = HexColor.WHITE;
        } else {
            color = HexColor.BLACK;
        }
        double opacity;
        switch (tvAmount) {
            case 0 -> opacity = 80;
            case 1 -> opacity = 85;
            case 2 -> opacity = 90;
            default -> opacity = 100;
        }
        if (!currentFilter.contains(NEON)) {
            opacity *= AdvancedMath.random(0.8, 0.9);
        }
        Flash.Builder flashPreset = new Flash.Builder()
                .setDuration(0d)
                .setStartColor(color)
                .setEndColor(color)
                .setStartOpacity(opacity)
                .setEndOpacity(opacity)
                .setAngleOffset(angleOffset);

        currentEvent.add(flashPreset.build());

        //Bloom Area
        HexColor randomColor = HexColor.random();
        double intensity = AdvancedMath.random(60, 140) * 1024 / (randomColor.r() + randomColor.g() + randomColor.b());

        currentEvent.add(new Bloom.Builder()
                .setColor(randomColor)
                .setIntensity(intensity)
                .setDuration(0d)
                .setThreshold(AdvancedMath.random(0, 30, Ease.IN_QUART.fun()))
                .setAngleOffset(angleOffset)
                .build());

        return currentEvent;
    }

    private static List<Double> generatePList(List<Double> probabilityList) {
        List<Double> calcPList = new ArrayList<>();
        calcPList.add(probabilityList.get(0));
        for (int i = 1; i < probabilityList.size(); i++) {
            double prev = calcPList.get(i - 1);
            double current = Math.max(0, probabilityList.get(i));
            calcPList.add(prev + current);
        }
        return calcPList;
    }

    public static Filter typeOf(String name) {
        return name == null ? null : Arrays.stream(values())
                .filter(value -> value.name.equals(name))
                .findAny()
                .orElseThrow(() -> new NullPointerException(name));
    }
}

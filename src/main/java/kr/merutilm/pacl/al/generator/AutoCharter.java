package kr.merutilm.pacl.al.generator;

import kr.merutilm.base.io.WAVFile;
import kr.merutilm.pacl.data.CustomLevel;
import kr.merutilm.pacl.data.Settings;

import java.util.ArrayList;
import java.util.List;

public record AutoCharter(WAVFile file, int offset,double power, double bpm, double filter,
                          double angleDivisor) implements Generator {
    @Override
    public CustomLevel generate() {
        List<Double> offsets = new ArrayList<>();
        short[] waveform = file.function(e -> Math.pow(e / 32767.0, power) * 32767).waveform()[0];
        int max = Short.MAX_VALUE;
        double prev = 0;
        for (int i = 0; i < waveform.length; i++) {
            double value = (double) (i / file.sampleRate()) - prev;
            if (filter < (double) waveform[i] / max && value > 0.05) {
                offsets.add(value);
                prev += value;
            }
        }
        CustomLevel level = CustomLevel.emptyLevel();
        offsets.remove(0);
        level.setSettings(new Settings.Builder().setBpm(bpm).setOffset(offset).build());
        level.createAngle(0);
        double add = 0;
        double relative = 0;
        for (double offset : offsets) {
            relative += bpm * 3 * offset;

            if(relative + add < 0){
                continue;
            }

            double finalRelative = (int) (Math.round((relative + add) / angleDivisor)) * angleDivisor;
            add = relative - finalRelative;
            relative = 0;

            level.createRelativeAngleExceptAnalysis(finalRelative);
        }

        return level;
    }
}

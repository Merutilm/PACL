package kr.merutilm.pacl.al.generator;

import kr.merutilm.base.exception.WAVException;
import kr.merutilm.base.io.WAVFile;
import kr.merutilm.base.selectable.Ease;
import kr.merutilm.base.util.AdvancedMath;
import kr.merutilm.base.util.ArrayFunction;
import kr.merutilm.base.util.ConsoleUtils;
import kr.merutilm.pacl.al.event.events.action.PlaySound;
import kr.merutilm.pacl.al.event.events.action.SetHitsound;
import kr.merutilm.pacl.al.event.events.action.SetSpeed;
import kr.merutilm.pacl.al.event.events.action.Twirl;
import kr.merutilm.pacl.al.event.selectable.HitSound;
import kr.merutilm.pacl.al.event.selectable.TrackDisappearAnimation;
import kr.merutilm.pacl.data.CustomLevel;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class GeneratorWaveform implements Generator {
    private final String filePath;
    private final ChartType chartType;
    private final HitSound hitSound;
    private final float sampleRate;
    private final double highPass;
    private final double offset;
    private final int outputVolume;
    private final double duration;
    private AudioInputStream fileInput;
    private boolean rendered = false;
    private final List<AudioOffset> audioOffsetList = new ArrayList<>();
    private static final double VOLUME_BOOSTER = 1.5;
    private static final Ease VOLUME_GAIN_EASE = Ease.INOUT_QUAD;

    /**
     * wav 파일을 채보 파일로 변환합니다.
     *
     * @param filePath     wav 파일 경로
     * @param chartType    채보 유형
     * @param hitSound     히트사운드
     * @param sampleRate   초당 샘플 개수 (최대값 : 해당 파일의 sampleRate)
     * @param highPass     렌더링할 최소 볼륨 (0~100)
     * @param offset       렌더링 시작 오프셋(초)
     * @param outputVolume 최종 볼륨(0~100,하이패스가 적용되기 전 계산됩니다)
     * @param duration     기간(초, 0이면 끝까지 렌더링합니다)
     */
    public GeneratorWaveform(String filePath, ChartType chartType, HitSound hitSound, float sampleRate, int highPass, double offset, int outputVolume, double duration) {
        this.filePath = filePath;
        this.chartType = chartType;
        this.hitSound = hitSound;
        this.sampleRate = sampleRate;
        this.highPass = highPass;
        this.offset = offset;
        this.outputVolume = outputVolume;
        this.duration = duration;
    }

    public static final class Generator {

        private final float sampleRate;
        private final List<AudioOffset> audioOffsetList;

        public Generator(float sampleRate) {
            this.sampleRate = sampleRate;
            this.audioOffsetList = new ArrayList<>();
        }

        public Generator addFrame(int frame, int volume) {
            audioOffsetList.add(new AudioOffset(frame, volume));
            return this;
        }

        public Generator addFrameSec(double timeSec, int volume) {
            return addFrame((int) (timeSec * sampleRate), volume);
        }

        public Generator addFrames(AudioOffset... audioOffset) {
            audioOffsetList.addAll(Arrays.stream(audioOffset).toList());
            return this;
        }

        public Generator addFrames(List<AudioOffset> audioOffset) {
            audioOffsetList.addAll(audioOffset);
            return this;
        }

        public WAVFile generate(String hitSoundPath) throws WAVException {
            return GeneratorWaveform.generateHitSoundWAVFile(audioOffsetList, sampleRate, hitSoundPath);
        }
    }

    @Override
    public CustomLevel generate() {
        CustomLevel level = CustomLevel.emptyLevel();

        try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(filePath))) {

            WAVFile result = WAVFile.getMono(inputStream);

            float fileSampleRate = result.sampleRate();
            this.fileInput = inputStream;
            long sampleSum = 0;
            int prevAverage = 0;

            double secPerSample = 1 / fileSampleRate;

            //샘플 크기로 볼륨 정하기
            int prevSample = 0;
            int prevFrame = 0;
            int count = 0;
            for (int frame = 0; frame < result.waveform()[0].length; frame += (int) Math.max(1, fileSampleRate / this.sampleRate)) {
                int index = frame + (int) (fileSampleRate * offset);
                short sample = index >= result.waveform()[0].length ? 0 : result.waveform()[0][index];

                sampleSum += sample;
                count++;

                if (sample != 0 && (double) prevSample / sample < 0) {
                    short average = (short) (sampleSum / count);
                    int increment = average - prevAverage;
                    if (increment > 0) {
                        // volume 공식
                        int volume = (int) (increment * VOLUME_GAIN_EASE.fun().apply(this.outputVolume / 100.0));
                        audioOffsetList.add(new AudioOffset(prevFrame, volume));
                    }
                    prevFrame = frame;
                    count = 0;
                    sampleSum = 0;
                    prevAverage = average;
                }


                prevSample = sample;
            }


            int currentFrame = 0;
            double timeSec;
            boolean twirled = false;

            int highestVolume = audioOffsetList.stream().mapToInt(AudioOffset::volume).max().orElse(0);
            double averageTimeSecInterval = secPerSample * inputStream.getFrameLength() / audioOffsetList.size();
            double averageBPM = 60 / averageTimeSecInterval;

            //Volume Normalize
            for (int i = 0; i < audioOffsetList.size(); i++) {
                AudioOffset current = audioOffsetList.get(i);
                audioOffsetList.set(i, new AudioOffset(current.frame(), current.volume() * 100 / highestVolume));
            }

            level.createAngle(0);
            int prevVolume = 100;
            prevFrame = 0;
            boolean onlyFirst = true;

            //Render
            for (AudioOffset audioOffset : audioOffsetList) {

                int frameInterval = audioOffset.frame() - prevFrame;
                currentFrame += frameInterval;
                timeSec = secPerSample * currentFrame;

                int volume = audioOffset.volume();

                if (volume > highPass && volume != 0) {

                    double bpm = 60 / timeSec;
                    int floor = level.getLength() + 1;

                    if (chartType instanceof LinearChart) {

                        level.createAngle(0);
                        level.createEvent(floor, new SetSpeed.Builder()
                                .setBpm(bpm)
                                .build()
                        );

                        if (prevVolume != volume) {
                            level.createEvent(floor, new SetHitsound.Builder()
                                    .setHitSound(hitSound)
                                    .setVolume(volume)
                                    .build()
                            );
                        }

                    } else if (chartType instanceof CurvedChart c) {
                        double relativeAngle = averageBPM / bpm * 180;

                        if (c.twirl()) {

                            double twirledRelativeAngle = 360 - (relativeAngle % 360) + ((int) (relativeAngle / 360)) * 360;
                            double angle = 180 - relativeAngle + level.getAngle(level.getLength());
                            double twirledAngle = 180 - twirledRelativeAngle + level.getAngle(level.getLength());
                            if (Math.cos(Math.toRadians(twirledAngle)) > Math.cos(Math.toRadians(angle))) {
                                if (!twirled) {
                                    level.createEvent(level.getLength(), new Twirl.Builder().build());
                                    twirled = true;
                                }
                                level.createRelativeAngleExceptAnalysis(twirledRelativeAngle);
                            } else {
                                if (twirled) {
                                    level.createEvent(level.getLength(), new Twirl.Builder().build());
                                    twirled = false;
                                }
                                level.createRelativeAngleExceptAnalysis(relativeAngle);
                            }
                        } else {
                            level.createRelativeAngleExceptAnalysis(relativeAngle);
                        }
                        if (prevVolume != volume) {
                            level.createEvent(floor,  new SetHitsound.Builder()
                                    .setHitSound(hitSound)
                                    .setVolume(volume)
                                    .build()
                            );
                        }
                    } else if (chartType instanceof PlaySoundEventChart c) {
                        if (onlyFirst) {
                            level.createEvent(1, new SetSpeed.Builder()
                                    .setBpm(c.bpm())
                                    .build()
                            );

                            onlyFirst = false;
                        }

                        level.createEvent(1,  new PlaySound.Builder()
                                .setHitSound(hitSound)
                                .setVolume(volume)
                                .setAngleOffset(3 * audioOffset.frame() / fileSampleRate * c.bpm())
                                .build()
                        );
                    }

                    prevVolume = volume;
                    currentFrame = 0;
                }
                prevFrame = audioOffset.frame();

                if (audioOffset.frame() / fileSampleRate > duration && duration != 0) {
                    break;
                }
            }

            level.setSettings(level.getSettings().edit().setZoom(300)
                    .setBeatsBehind(0d)
                    .setBpm(averageBPM)
                    .setTrackDisappearAnimation(TrackDisappearAnimation.FADE)
                    .build());

            rendered = true;

        } catch (UnsupportedAudioFileException | IOException e) {
            ConsoleUtils.logError(e);
        }
        return level;
    }

    /**
     * Generates the waveform with hitSounds.
     *
     * @return returns null when Exception occurs during generation
     */
    private static WAVFile generateHitSoundWAVFile(List<AudioOffset> audioOffsetList, float audioOffsetListSampleRate, String hitSoundPath) throws WAVException {
        try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(hitSoundPath))) {

            float hitSoundSampleRate = inputStream.getFormat().getSampleRate();

            int channelAmount = inputStream.getFormat().getChannels();
            int resultFrameLength = (int) (audioOffsetList.stream().mapToInt(AudioOffset::frame).max().orElse(0) + inputStream.getFrameLength());
            WAVFile hitSoundWavFile = WAVFile.get(inputStream);

            List<List<Integer>> waveform = Collections.nCopies(channelAmount, new ArrayList<>(Collections.nCopies(resultFrameLength, 0)));
            int divisor = 1;
            for (int i = 0; i < audioOffsetList.size(); i++) {
                AudioOffset audioOffset = audioOffsetList.get(i);
                if (i % divisor == 0) {
                    ConsoleUtils.printProgress("Rendering...", audioOffsetList.size(), i);
                    divisor = (int) AdvancedMath.random(290, 310);
                }
                WAVFile modified = hitSoundWavFile.copy();
                modified.volume(audioOffset.volume());

                int frame = (int) (audioOffset.frame() * hitSoundSampleRate / audioOffsetListSampleRate);
                //변환하기
                for (int channel = 0; channel < channelAmount; channel++) {
                    List<Integer> samples = waveform.get(channel);
                    int hitSoundFrameLength = modified.waveform()[channel].length;

                    for (int frameHS = 0; frameHS < hitSoundFrameLength; frameHS++) {
                        int currentValue = samples.get(frame + frameHS);
                        samples.set(frame + frameHS, currentValue + modified.waveform()[channel][frameHS]);
                    }

                }
            }

            int maxFrameSize = waveform.stream().mapToInt(List::size).max().orElse(0);
            int maxValue = waveform.stream().flatMap(List::stream).mapToInt(v -> v).max().orElse(0);
            int minValue = waveform.stream().flatMap(List::stream).mapToInt(v -> v).min().orElse(0);
            double multiplier = 1;

            if (maxValue > Short.MAX_VALUE) {
                multiplier = (double) Short.MAX_VALUE / maxValue;
            }
            if (minValue < Short.MIN_VALUE) {
                multiplier = (double) Short.MIN_VALUE / minValue;
            }
            List<List<Short>> finalWaveform = new ArrayList<>();

            for (int channel = 0; channel < channelAmount; channel++) {
                List<Integer> samples = waveform.get(channel);

                finalWaveform.add(new ArrayList<>());
                List<Short> finalSamples = finalWaveform.get(channel);

                for (Integer sample : samples) {
                    int modified = (int) (sample * multiplier * VOLUME_BOOSTER);
                    modified = Math.min(Short.MAX_VALUE, modified);
                    modified = Math.max(Short.MIN_VALUE, modified);
                    finalSamples.add((short) modified);
                }

                while (finalSamples.size() < maxFrameSize) {
                    finalSamples.add((short) 0);
                }
            }
            return new WAVFile(hitSoundSampleRate, ArrayFunction.toDeepShortArray(finalWaveform));
        } catch (UnsupportedAudioFileException | IOException e) {
            throw new WAVException();
        }
    }


    /**
     * 히트사운드 파일로 미리듣기
     */
    public void hitSoundPreview(String hitSoundPath, String filePath) {
        if (!rendered) {
            throw new IllegalStateException("Use generate() method first.");
        }
        try {

            //모든 채널의 샘플 개수를 통일하고, 최대값이 32767, 최솟값이 -32768이 되도록 모든 값을 일정 값만큼 곱하기

            WAVFile wavFile = new Generator(fileInput.getFormat().getSampleRate())
                    .addFrames(audioOffsetList)
                    .generate(hitSoundPath);

            ConsoleUtils.printProgress("Rendering...", audioOffsetList.size(), audioOffsetList.size());
            System.out.println();
            System.out.println();
            wavFile.export(filePath);

        } catch (WAVException e) {
            ConsoleUtils.logError(e);
        }
    }

    public interface ChartType {

    }

    /**
     * 오디오 파일 재생 프레임
     *
     * @param frame  프레임
     * @param volume 음량
     */
    public record AudioOffset(int frame, int volume) {

    }

    public record LinearChart() implements ChartType {
    }

    public record CurvedChart(boolean twirl) implements ChartType {
    }

    public record PlaySoundEventChart(double bpm) implements ChartType {

    }

}
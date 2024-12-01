package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.io.WAVFile;
import kr.tkdydwk7071.base.util.ArrayFunction;

final class SampleGeneratorWave2 {
    private SampleGeneratorWave2() {
    }

    public static void main(String[] args) {
        String file = "C:\\Users\\nudet\\Desktop\\Main\\Games\\ADOFAI\\Custom\\TestLevel\\BilliumMoto - Rocky Buinne.wav";
        String dest = "C:/Users/nudet/Desktop/Main/Games/ADOFAI/Custom/TestLevel/test.wav";


        WAVFile wav = WAVFile.get(file).cut(0, 10);
        double intervalSec = 0.03;
        short[] resultWaveform = new short[wav.length()];
        double[] waveformDouble = ArrayFunction.toDoubleShortArray(wav.waveform()[0]);

        for (int i = 0; i * intervalSec < wav.lengthSec(); i++) {
            int[] f = WAVFile.fft(waveformDouble, intervalSec * i, intervalSec, wav.sampleRate());
            short[] waveform = WAVFile.generateFrequencies(WAVFile.WaveType.LINEAR, wav.sampleRate(), intervalSec * i, intervalSec, (short) -1, f);
            int len = waveform.length;
            int off = i * len;
            len = Math.min(len, resultWaveform.length - off);
            System.arraycopy(waveform, 0, resultWaveform, off, len);
        }

        WAVFile result = new WAVFile(wav.sampleRate(), new short[][]{resultWaveform, resultWaveform});
        result.export(dest);
    }


}

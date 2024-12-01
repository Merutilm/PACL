package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.pacl.adofailevel.event.selectable.HitSound;
import kr.tkdydwk7071.pacl.adofailevel.generator.GeneratorWaveform;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class SampleGeneratorWave {
    private SampleGeneratorWave() {
    }

    public static void main(String[] args) {
        String file = "C:\\Users\\nudet\\Downloads\\Enderman Stare.wav";
        String dest = "C:/Users/nudet/Desktop/Main/Games/ADOFAI/Custom/TestLevel/";

        HitSound hitSound = HitSound.KICK;
        float sampleRate = 44000;
        int highPass = 10;
        double offset = 0;
        double duration = 1000;
        int volume = 150;


        GeneratorWaveform generator = new GeneratorWaveform(file, new GeneratorWaveform.CurvedChart(true), hitSound, sampleRate, highPass, offset, volume, duration);
        CustomLevel level = generator.generate();

        level.save(dest + "DONE" + ".adofai");
         generator.hitSoundPreview("C:/Users/nudet/Desktop/Main/Games/ADOFAI/Custom/SoundPack/Kick.wav", dest + "ENDHit.wav");


    }

}

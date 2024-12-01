package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.io.WAVFile;
import kr.tkdydwk7071.base.util.ConsoleUtils;
import kr.tkdydwk7071.pacl.adofailevel.generator.Drunk;
import kr.tkdydwk7071.pacl.data.CustomLevel;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

final class SampleEditorForConversion {
    private SampleEditorForConversion() {
    }

    public static void main(String[] args) {
        String parent = "C:\\Users\\nudet\\Desktop\\Main\\Games\\ADOFAI\\Custom\\LevelPack\\AWC_Final_L-X_LORELEI";

        CustomLevel level = CustomLevel.generate(
                new Drunk(CustomLevel.load(parent + "\\final.adofai"),
                        10)
        );
        File songFile = new File(parent, level.getSettings().songFilename());
        try (AudioInputStream inputStream = AudioSystem.getAudioInputStream(songFile)) {
            WAVFile file = WAVFile.get(inputStream);
            file = file.pitch(e -> (Math.sin(41 * Math.sin(89 * e)) + Math.sin(291 * e)) / 16 + 1);
            file.export(parent + "\\drunken.wav");
            level.setSettings(level.getSettings().edit().setSongFilename("drunken.wav").build());
        } catch (IOException | UnsupportedAudioFileException e) {
            ConsoleUtils.logError(e);
        }
        level.save(parent + "\\DONE.adofai");


    }
}

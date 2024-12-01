package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.io.WAVFile;
import kr.tkdydwk7071.pacl.adofailevel.generator.AutoCharter;
import kr.tkdydwk7071.pacl.data.CustomLevel;

final class SampleEditorForMusic {
    private SampleEditorForMusic() {
    }

    public static void main(String[] args) {
        CustomLevel level = CustomLevel.generate(new AutoCharter(WAVFile.get("C:\\Users\\nudet\\Downloads\\테트리스 BGM - Bradinsky ⧸ Tetris BGM Bradinsky (брадинский).wav"), -220, 0.1,166.6, 0.812, 15));
        level.setSettings(level.getSettings().edit().setSongFilename("테트리스 BGM - Bradinsky ⧸ Tetris BGM Bradinsky (брадинский).wav").build());
        level.save("C:\\Users\\nudet\\Desktop\\Main\\Games\\ADOFAI\\Custom\\TestLevel\\DONE.adofai");
    }
}

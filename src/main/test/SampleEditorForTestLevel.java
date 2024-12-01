package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.pacl.data.CustomLevel;

final class SampleEditorForTestLevel {
    private SampleEditorForTestLevel() {
    }

    public static void main(String[] args) {
        CustomLevel level = CustomLevel.load("C:/Users/nudet/Desktop/Main/Games/ADOFAI/Custom/TestLevel/A Forgotten Night test test.adofai");
        new VFX3DTravel().apply(level);
        new VFXAngleCorrection().apply(level);

        level.save("C:/Users/nudet/Desktop/Main/Games/ADOFAI/Custom/TestLevel/DONE.adofai");
    }
}

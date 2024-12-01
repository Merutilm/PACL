package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.pacl.data.CustomLevel;

final class SampleEditorOnce {
    private SampleEditorOnce() {
    }

    public static void main(String[] args) {

        CustomLevel level = CustomLevel.load("C:/Users/nudet/Desktop/Main/Games/ADOFAI/Custom/TestLevel/Bananananana.adofai");
        new VFXAngleCorrection().apply(level);
        level.save("C:/Users/nudet/Desktop/Main/Games/ADOFAI/Custom/TestLevel/Bananananana_modified.adofai");
    }
}

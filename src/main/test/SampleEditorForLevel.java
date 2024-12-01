package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.pacl.data.CustomLevel;

final class SampleEditorForLevel {


    private SampleEditorForLevel() {
    }

    public static void main(String[] args) {

        CustomLevel level = CustomLevel.load("C:\\Users\\nudet\\Desktop\\Main\\Games\\ADOFAI\\Custom\\MyLevels\\S34_DDONGSSADA 3302\\CICADA3302_Part4_LOW.adofai");

        new VFX3DTravel().apply(level);
        new VFXAngleCorrection().apply(level);

        level.save("C:\\Users\\nudet\\Desktop\\Main\\Games\\ADOFAI\\Custom\\MyLevels\\S34_DDONGSSADA 3302\\DONE.adofai");

    }
}
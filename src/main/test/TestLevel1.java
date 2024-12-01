package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.pacl.data.CustomLevel;

final class TestLevel1 {
    private TestLevel1() {

    }

    public static void main(String[] args) {
        CustomLevel level = CustomLevel.createLevel();
        new SamplePhysics().apply(level);
        level.save("C:\\Users\\nudet\\Desktop\\Main\\Games\\ADOFAI\\Custom\\TestLevel\\test.adofai");

    }
}

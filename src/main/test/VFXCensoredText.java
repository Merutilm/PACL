package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.selectable.Ease;
import kr.tkdydwk7071.base.struct.Point2D;
import kr.tkdydwk7071.base.util.AdvancedMath;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.VFXGeneratorTypos;
import kr.tkdydwk7071.pacl.adofailevel.editor.vfx.t6typo.variable.Fade;
import kr.tkdydwk7071.pacl.data.CustomLevel;

import java.util.Arrays;
import java.util.List;

final class VFXCensoredText implements VFX {

    private int groupID = 1;
    private static final String KEY = "*";

    @Override
    public void apply(CustomLevel level) {
        VFXGeneratorTypos macro = new VFXGeneratorTypos(level, 3);
        macro.attributeAsset.setDefaultDepth((short) -10000);
        add(macro, 1, 1, "WHEN");
        add(macro, 2, 1, "YOU");
        add(macro, 3, 2, "F**KING");
        add(macro, 5, 1, "SEE");
        add(macro, 6, 1, "IT");
    }

    private void add(VFXGeneratorTypos macro, int floor, double duration, String str) {
        double moveDuration = 1;

        List<String> r = Arrays.stream(str.split("")).toList();


        if (r.contains(KEY)) {
            for (double i = 0; i < (duration + moveDuration) * 180; i += 10) {
                StringBuilder sb = new StringBuilder();
                for (String s : r) {
                    if (s.equals(KEY)) {
                        sb.append(randomString(1));
                    } else {
                        sb.append(s);
                    }
                }
                String finalStr = sb.toString();
                if (i == 0) {
                    typo(macro, floor, moveDuration, finalStr);
                }
                macro.changeText(floor, groupID, finalStr, i);
            }
        } else {
            typo(macro, floor, moveDuration, str);
        }

        macro.out(floor, groupID, new Fade(moveDuration, Ease.INOUT_CUBIC, new Point2D(0, -1), 100, 0.02, 0), duration * 180);
        groupID++;
    }

    private void typo(VFXGeneratorTypos macro, int floor, double duration, String str) {
        macro.in(floor, str, new Point2D(0, 1), 0, 40, new Fade(duration, Ease.INOUT_CUBIC, new Point2D(0, -1), 100, 0.02, 0), 0);
    }

    public String randomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= length; i++) {
            sb.append(Character.toString(AdvancedMath.doubleRandom(1) > 0.5 ? (int) AdvancedMath.random(65, 91) : (int) AdvancedMath.random(97, 122)));
        }
        return sb.toString();
    }
}

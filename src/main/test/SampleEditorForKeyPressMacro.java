package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.util.AdvancedMath;
import kr.tkdydwk7071.base.util.ConsoleUtils;
import kr.tkdydwk7071.base.util.TaskManager;
import kr.tkdydwk7071.pacl.data.CustomLevel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

final class SampleEditorForKeyPressMacro {
    private SampleEditorForKeyPressMacro() {
    }

    public static void main(String[] args) throws AWTException {
        CustomLevel level = CustomLevel.load("C:\\Users\\nudet\\Desktop\\Main\\Games\\ADOFAI\\Custom\\MyLevels\\S30_WYSI\\NONE.adofai");
        Robot keyMacro = new Robot();

        int startTile = 5172;
        int delay = 1908;

        List<Long> millisList = new ArrayList<>();
        for (int i = startTile + 1; i <= level.getLength(); i++) {
            double traveled = level.getTraveledAngle(i) - level.getTraveledAngle(startTile);

            long millis = (long) (traveled / 3 * 1000 / level.getSettings().bpm());

            if (!level.isMidSpinTile(i)) {
                millisList.add(millis);
            }
        }

        millisList = millisList.stream().sorted().toList();

        try {
            System.out.println("Press any key and Enter to start...");
            new Scanner(System.in).next();
            System.out.println("3");
            Thread.sleep(1000);
            System.out.println("2");
            Thread.sleep(1000);
            System.out.println("1");
            Thread.sleep(1000);
            System.out.println("START!!");

            List<KeyCodeHoldTime> keyList = generateKeyList();

            long start = System.currentTimeMillis() + delay;
            press(keyMacro, KeyEvent.VK_SPACE, 1);
            for (int i = 0; i < millisList.size(); i++) {

                while (millisList.get(i) > System.currentTimeMillis() - start) {
                }
                if (keyList.size() <= i) {
                    press(keyMacro, KeyEvent.VK_SPACE, 0);
                } else {
                    press(keyMacro, keyList.get(i).keyCode(), keyList.get(i).holdTime);
                }
            }

        } catch (InterruptedException e) {
            ConsoleUtils.logError(e);
            Thread.currentThread().interrupt();
        }
    }

    private record KeyCodeHoldTime(
            int keyCode,
            long holdTime
    ) {

    }

    private static List<KeyCodeHoldTime> generateKeyList() {
        List<KeyCodeHoldTime> keyList = new ArrayList<>();
        add(keyList, 70,
                9, 9
        );
        add(keyList, 47,
                6, 5, 4, 3, 2, 1,
                7, 8, 9, 10, 11, 12,
                5, 4, 3, 2
        );

        add(keyList, 70,
                8, 9, 10, 11,
                5, 4, 3, 2,
                8, 9, 10, 11,
                5, 4, 3, 2,
                8, 9, 10, 11,
                5, 4, 3, 2
        );
        add(keyList, 47,
                7, 8, 9, 10, 11, 12,
                6, 5, 4, 3, 2, 1,
                9, 10, 11, 12
        );
        add(keyList, 70,
                5, 4, 3, 2
        );
        add(keyList, 35,
                9, 10, 11, 12,
                4, 3, 2, 1
        );
        add(keyList, 70,
                8, 9, 10, 11
        );
        add(keyList, 35,
                6, 5, 4, 3, 2, 1,
                7, 8, 9, 10, 11, 12
        );
        add(keyList, 70,
                6, 5, 4, 3, 2,
                8, 9, 10, 11,
                6, 5, 4, 3, 2, 1,
                7, 8, 9, 10, 11, 12,
                5, 4, 3, 2,
                8, 9, 10, 11,
                5, 4, 3, 2,
                8, 9, 10, 11,
                5, 4, 3,
                8, 9, 10,
                5, 4, 3
        );
        add(keyList, 35,
                7, 8, 9, 10, 11, 12,
                6, 5, 4, 3, 2, 1
        );
        add(keyList, 47,
                7, 8, 9, 10, 11, 12,
                6, 5, 4, 3, 2, 1,
                8, 9, 10, 11
        );
        add(keyList, 70,
                5, 4, 3
        );
        add(keyList, 35,
                7, 8, 9, 10, 11, 12,
                6, 5, 4, 3, 2, 1,
                7, 8, 9, 10, 11, 12,
                6, 5, 4, 3, 2, 1
        );
        add(keyList, 70,
                8, 9, 10, 11
        );
        add(keyList, 47,
                6, 5, 4, 3, 2, 1,
                7, 8, 9, 10, 11, 12,
                5, 4, 3, 2
        );
        add(keyList, 70,
                8, 9, 10, 11
        );
        add(keyList, 35,
                4, 3, 2, 1,
                9, 10, 11, 12
        );
        add(keyList, 70,
                5, 4, 3, 2,
                8, 9, 10, 11
        );
        add(keyList, 53,
                6, 5, 4, 3, 2, 1,
                7, 8, 9, 10, 11, 12,
                5, 4, 3, 2, 1
        );
        add(keyList, 70,
                8, 9, 10,
                5, 4, 3,
                8, 9, 10, 11,
                5, 4, 3
        );
        add(keyList, 35,
                7, 8, 9, 10, 11, 12,
                4, 3
        );
        add(keyList, 70,
                8, 9, 10, 11,
                5, 4, 3, 2
        );
        add(keyList, 35,
                9, 10, 11, 12,
                4, 3, 2, 1,
                7, 8, 9, 10, 11, 12,
                6, 5, 4, 3, 2, 1,
                7, 8, 9, 10, 11, 12,
                4,
                7, 8, 9, 10, 11, 12,
                6, 5, 4, 3, 2, 1
        );
        add(keyList, 70,
                7, 8, 9, 10, 11,
                5, 4, 3, 2,
                7, 8, 9, 10, 11, 12,
                6, 5, 4, 3, 2, 1,
                8, 9, 10, 11,
                5, 4, 3, 2,
                8, 9, 10, 11,
                5, 4, 3, 2,
                8, 9, 10, 11,
                5, 4, 3, 2,
                8, 9, 10, 11
        );
        add(keyList, 47,
                6, 5, 4, 3, 2, 1,
                7, 8, 9, 10, 11, 12,
                5, 4, 3, 2,
                7, 8, 9, 10, 11, 12,
                6, 5, 4, 3, 2, 1,
                9, 10, 11, 12
        );
        add(keyList, 140,
                5, 4, 3, 2, 1);
        return keyList;
    }

    private static void add(List<KeyCodeHoldTime> keyList, long holdTime, int... keyNumbers) {
        for (int keyNumber : keyNumbers) {
            keyList.add(new KeyCodeHoldTime(getKeyCode(keyNumber), holdTime));
        }
    }

    private static int getKeyCode(int keyNumber) {
        return switch (keyNumber) {
            case 1 -> KeyEvent.VK_Q;
            case 2 -> KeyEvent.VK_2;
            case 3 -> KeyEvent.VK_3;
            case 4 -> KeyEvent.VK_R;
            case 5 -> KeyEvent.VK_SPACE;
            case 6 -> KeyEvent.VK_V;
            case 7 -> KeyEvent.VK_COMMA;
            case 8 -> KeyEvent.VK_PERIOD;
            case 9 -> KeyEvent.VK_O;
            case 10 -> KeyEvent.VK_MINUS;
            case 11 -> KeyEvent.VK_EQUALS;
            case 12 -> KeyEvent.VK_BACK_SLASH;
            default -> KeyEvent.VK_P;
        };
    }

    private static void press(Robot robot, int keyCode, long holdTime) {

        TaskManager.runTask(() -> {
            robot.keyPress(keyCode);
            try {
                Thread.sleep((long) (holdTime * AdvancedMath.random(0.9, 1.1)));
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            robot.keyRelease(keyCode);
        });

    }

}

package kr.merutilm.pacl.data;

import kr.merutilm.base.util.ConsoleUtils;
import kr.merutilm.base.util.TaskManager;
import kr.merutilm.customswing.CSConfirmDialog;
import kr.merutilm.customswing.CSFrame;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;

public final class CSMainFrame extends CSFrame {
    @Serial
    private static final long serialVersionUID = -273179887756117934L;
    private final transient PACL editor;
    static final int FRAME_REFRESH_MILLIS = 10;

    private static final int SIZE_X = 1600;
    private static final int SIZE_Y = 900;

    int sizeX = SIZE_X;
    int sizeY = SIZE_Y;

    private final CSProcessPanel processPanel;
    private final CSEditor editorPanel;
    private final CSFunctionPanel functionPanel;
    private final transient FrameAnalyzer analyzer;
    private boolean saving = false;
    private boolean fixed = false;

    public FrameAnalyzer getAnalyzer() {
        return analyzer;
    }

    CSEditor editorPanel() {
        return editorPanel;
    }

    CSProcessPanel processPanel() {
        return processPanel;
    }

    CSFunctionPanel functionPanel() {
        return functionPanel;
    }


    CSAnalysisPanel analysisPanel() {
        return editorPanel.analysisPanel();
    }

    PACL getEditor() {
        return editor;
    }

    public CSMainFrame(PACL editor) {
        super(PACL.class.getSimpleName(), Assets.getIcon(), SIZE_X, SIZE_Y);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.editor = editor;

        setMinimumSize(new Dimension(640, 360));
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                sizeX = e.getComponent().getWidth() + X_CORRECTION_FRAME + 1;
                sizeY = e.getComponent().getHeight() + Y_CORRECTION_FRAME + 1;
                refreshBounds();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                super.windowActivated(e);
                disposeAll();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);

                if (saving) {
                    return;
                }

                if (!fixed) {
                    System.exit(0);
                }

                saveConfirmDialog("Exit", () -> System.exit(0), () -> {
                });
            }
        });


        analyzer = new FrameAnalyzer(editor.level());


        processPanel = new CSProcessPanel(this);
        functionPanel = new CSFunctionPanel(this);
        editorPanel = new CSEditor(this, functionPanel);

        add(processPanel);
        add(editorPanel);
        add(functionPanel);

        refreshBounds();
        setForeground(Color.BLACK);
        pack();

        TaskManager.runTask(() -> {
            processPanel.update();
            editorPanel.timelinePanel().repaint();
        }, Integer.MAX_VALUE, FRAME_REFRESH_MILLIS);
    }

    public void refreshBounds() {
        processPanel.setBounds(0, 0, sizeX, 30);
        functionPanel.setBounds(0, sizeY - 30, sizeX, 30);
        editorPanel.setBounds(0, 30, sizeX, sizeY - 60);
    }

    void setFixed(boolean fixed) {
        this.fixed = fixed;
    }

    public boolean targetLevelFixed() {
        return fixed;
    }

    void saveConfirmDialog(String title, Runnable enableAction, Runnable disposeAction) {

        String message = "<html><body style='text-align:center;'><body>" + title + " without saving? All changes will be <font color=#ff5555>lost!</font></body></html>";
        CSConfirmDialog dialog = new CSConfirmDialog(CSMainFrame.this, title, 100, message);

        dialog.addGreenOption("Save", e ->
                TaskManager.runTask(() -> {
                    saveLevel(true, false);
                    disposeAction.run();
                    enableAction.run();
                })
        );

        dialog.addRedOption("Discard", e -> {
            disposeAction.run();
            enableAction.run();
        });

        dialog.addOption("Cancel", e -> {
        });
        dialog.addDisposeAction(disposeAction);

        dialog.setup();
    }

    public void saveLevel(boolean wait, boolean overwrite) {
        saving = true;
        processPanel().update(); //프로세스 갱신
        functionPanel().disableAll(); //기능패널 비활성화

        Thread t = TaskManager.runTask(() -> {
            if (FileIO.parseFile(getEditor(), overwrite)) {
                setFixed(false);
                editorPanel().timelinePanel().getPainter().sendMessage("Level Saved");
            }
            functionPanel().enableAll();
            saving = false;
        });
        if (wait) {
            try {
                t.join();
            } catch (InterruptedException e) {
                ConsoleUtils.logError(e);
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isSaving() {
        return saving;
    }
}

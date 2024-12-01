package kr.merutilm.pacl.data;

import kr.merutilm.base.util.TaskManager;
import kr.merutilm.customswing.CSPanel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.io.File;
import java.io.Serial;

class CSProcessPanel extends CSPanel {
    @Serial
    private static final long serialVersionUID = 8863912612217087233L;
    private final JProgressBar progressBar;

    CSProcessPanel(CSMainFrame master) {
        super(master);

        progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 10000);
        progressBar.setLayout(null);
        progressBar.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() {
                return Color.BLACK;
            }

            @Override
            protected Color getSelectionForeground() {
                return Color.BLACK;
            }
        });
        progressBar.setString("");
        progressBar.setStringPainted(true);
        progressBar.setFont(new Font(Font.SERIF, Font.BOLD, 10));
        progressBar.setUI(new BasicProgressBarUI() {
            @Override
            protected Color getSelectionBackground() {
                return new Color(255, 255, 255, 197);
            }

            @Override
            protected Color getSelectionForeground() {
                return new Color(0, 0, 0, 197);
            }
        });
        progressBar.setBorder(null);
        startColor();
        setDefaultMessage();
        setLayout(null);
        add(progressBar);
        repaint();
    }

    void startColor() {
        progressBar.setBackground(new Color(60, 60, 60));
        progressBar.setForeground(NORMAL_COLOR);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        progressBar.setBounds(x, y, w, h);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(255, 255, 255, 50));
        g2.setStroke(new BasicStroke(2f));
        g2.drawRect(0, 0, getWidth(), getHeight());
    }

    private static final Color NORMAL_COLOR = new Color(120, 220, 120);
    private static final Color ERROR_COLOR = new Color(190, 130, 130);

    transient Process prevProcessType;
    transient Thread statusThread;

    void update() {
        if (((CSMainFrame) getMainFrame()).getEditor().getProcessType() == null) {
            TaskManager.runTask(() -> progressBar.setString("Preparing..."));
        } else {
            Process curType = ((CSMainFrame) getMainFrame()).getEditor().getProcessType();
            if (curType != Process.FINISHED && curType != Process.ERROR) {
                setStatus();
            }
            if (prevProcessType != curType && (curType == Process.FINISHED || curType == Process.ERROR)) {
                setStatus();
                if (statusThread != null) {
                    statusThread.interrupt();
                }
                statusThread = TaskManager.runTask(() -> {
                    try {
                        Thread.sleep(3000);
                        startColor();
                        setDefaultMessage();
                        statusThread = null;
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
            prevProcessType = curType;
            repaint();
        }
    }

    private void setDefaultMessage() {
        progressBar.setValue(0);
        File input = ((CSMainFrame) getMainFrame()).getEditor().getInputFile();
        progressBar.setString((input == null ? "Level Not Saved." : input.getName()) + (((CSMainFrame) getMainFrame()).targetLevelFixed() ? "*" : ""));
    }

    private void setStatus() {
        switch (((CSMainFrame) getMainFrame()).getEditor().getProcessType()) {
            case FINISHED -> {
                progressBar.setForeground(NORMAL_COLOR);
                progressBar.setString("Finished!");
                progressBar.setValue(10000);
            }
            case LOAD -> setDefaultMessage();
            case READ ->
                    progressBar.setString("Reading...  Loaded Events : " + ((CSMainFrame) getMainFrame()).getEditor().level().getCurrentTotalEvents());
            case EDIT ->
                    progressBar.setString("Editing...  Loaded Events : " + ((CSMainFrame) getMainFrame()).getEditor().level().getCurrentTotalEvents());
            case ERROR -> {
                progressBar.setValue(10000);
                progressBar.setString("ERROR");
                progressBar.setForeground(ERROR_COLOR);
            }
            case PARSE -> {
                progressBar.setForeground(NORMAL_COLOR);
                progressBar.setString("Parsing...  " + ((CSMainFrame) getMainFrame()).getEditor().processCurrent + " / " + ((CSMainFrame) getMainFrame()).getEditor().processAll);
                progressBar.setValue((int) Math.floor(((CSMainFrame) getMainFrame()).getEditor().getParsingProgress() * 100));
            }

            default -> {
                //noop
            }
        }

    }
}

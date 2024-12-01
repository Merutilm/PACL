package kr.merutilm.pacl.data;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import kr.merutilm.customswing.*;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public final class CSFunctionPanel extends CSPanel {

    @Serial
    private static final long serialVersionUID = 8780612569791909051L;
    private final CSToggleButton renderButton;
    private final CSToggleButton saveButton;

    CSFunctionPanel(CSMainFrame master) {
        super(master);

        setBackground(new Color(60, 60, 60));

        renderButton = new CSToggleButton(master, "Render");
        renderButton.addLeftClickAction(e -> {
            disableAll();

            CSPreviewPanel pp = master.editorPanel().previewPanel();

            AtomicInteger frame = new AtomicInteger(pp.getCenterFrame());
            AtomicInteger xRes = new AtomicInteger(1920);
            AtomicBoolean tile = new AtomicBoolean(pp.isTileRendering());
            AtomicBoolean decoration = new AtomicBoolean(pp.isDecorationRendering());
            AtomicBoolean background = new AtomicBoolean(pp.isBackgroundRendering());
            AtomicBoolean videoBackground = new AtomicBoolean(pp.isVideoBackgroundRendering());
            AtomicBoolean foreground = new AtomicBoolean(pp.isForegroundRendering());
            AtomicBoolean colorFilter = new AtomicBoolean(pp.isColorFilterRendering());


            CSMultiDialog dialog = new CSMultiDialog(master, "Render Frame", () -> {

                int fx = 1280;
                CSFrame window = new CSFrame("Preview", master.getIconImage(), fx, (int) (fx * pp.getScreenRatio()));
                window.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                window.setResizable(false);
                CSPreviewPanel pp2 = new CSPreviewPanel(master);
                pp2.setBounds(window.getBounds());
                pp2.setShowTile(tile.get());
                pp2.setShowDecoration(decoration.get());
                pp2.setShowBackground(background.get());
                pp2.setShowVideoBackground(videoBackground.get());
                pp2.setShowForeground(foreground.get());
                pp2.setShowColorFilter(colorFilter.get());
                pp2.setXRes(xRes.get());
                pp2.reload();
                window.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        pp2.interruptRender();
                    }
                });

                window.add(pp2);
                window.pack();

                pp2.renderFrame(frame.get());

                enableAll();

            });
            dialog.getInput().createTextInput("Frame Number", frame.get(), 0, Integer::parseInt, frame::set);
            dialog.getInput().createTextInput("Resolution", xRes.get(), 15, Integer::parseInt, xRes::set);
            dialog.getInput().createBoolInput("Tile", tile.get(), false, tile::set);
            dialog.getInput().createBoolInput("Decoration", decoration.get(), false, decoration::set);
            dialog.getInput().createBoolInput("Background", background.get(), false, background::set);
            dialog.getInput().createBoolInput("Video Background", videoBackground.get(), false, videoBackground::set);
            dialog.getInput().createBoolInput("Foreground", foreground.get(), false, foreground::set);
            dialog.getInput().createBoolInput("Color Filter", colorFilter.get(), false, colorFilter::set);
            dialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    enableAll();
                }
            });
            dialog.setup();

        });

        saveButton = new CSToggleButton(master, "Save Level");

        saveButton.addLeftClickAction(e -> master.saveLevel(false, false));
        add(renderButton);
        add(saveButton);

        disableAll();
        setLayout(null);
    }

    @Override
    public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, y, w, h);
        renderButton.setBounds(0, 0, w / 2, h);
        saveButton.setBounds(w / 2, 0, w / 2, h);
    }

    public void disableAll() {
        for (Component component : getComponents()) {
            if (component instanceof CSButton e) {
                e.setEnabled(false);
            }
        }
    }

    public void enableAll() {
        for (Component component : getComponents()) {
            if (component instanceof CSButton e) {
                e.setEnabled(true);
            }
        }
    }

}

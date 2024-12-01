package kr.merutilm.pacl.data;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;

import kr.merutilm.base.util.ConsoleUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.ffmpeg.global.avutil.AV_LOG_QUIET;
import static org.bytedeco.ffmpeg.global.avutil.av_log_set_level;


public class VideoFrame {
    private final File file;

    private BufferedImage[] frames = null;

    double fps = -1;
    int frameSize = -1;

    public VideoFrame(String path) {

        this(new File(path));
    }

    public VideoFrame(File file) {
        this.file = file;
        av_log_set_level(AV_LOG_QUIET);
    }

    public int length() {
        checkNull();
        return frames.length;
    }

    public BufferedImage getFrame(int frame) {
        checkNull();
        return frames[frame];
    }

    public BufferedImage[] getAllFrames() {
        checkNull();
        return frames;
    }

    private void checkNull() {
        if (frames == null) {
            throw new IllegalStateException("Not Initialized -> use readAllFrames() first.");
        }
    }

    public double getVideoFps() {
        if(fps != -1) return fps;
        try (
                FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(this.file)
        ) {

            frameGrabber.start();
            double frameRate = frameGrabber.getFrameRate();
            frameGrabber.stop();
            fps = frameRate;
            return frameRate;
        } catch (FrameGrabber.Exception e) {
            ConsoleUtils.logError(e);
            return -1;
        }
    }

    public int getFrameLength() {
        if(frameSize != -1) return frameSize;
        try (
                FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(this.file)
        ) {
            frameGrabber.start();
            int len = frameGrabber.getLengthInVideoFrames();
            frameGrabber.stop();
            frameSize = len;
            return len;
        } catch (FrameGrabber.Exception e) {
            ConsoleUtils.logError(e);
            return -1;
        }
    }

    public BufferedImage readFrame(int frame) {

        try (
                FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(this.file);
                Java2DFrameConverter c = new Java2DFrameConverter()
        ) {

            frameGrabber.start();
            frameGrabber.setVideoFrameNumber(frame);
            Frame f = frameGrabber.grab();
            BufferedImage img = c.convert(f);
            BufferedImage finalFrame = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
            finalFrame.createGraphics().drawImage(img, 0, 0, null);
            frameGrabber.stop();
            return finalFrame; //Memory reference (native method)
        } catch (FrameGrabber.Exception e) {
            ConsoleUtils.logError(e);
        }
        return null;
    }

    public void readAllFrames(double fps) {

        Frame f;
        try (
                FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(this.file);
                Java2DFrameConverter c = new Java2DFrameConverter()
        ) {
            List<BufferedImage> result = new ArrayList<>();
            frameGrabber.start();
            int len = frameGrabber.getLengthInVideoFrames();
            double vidFps = frameGrabber.getFrameRate();

            for (int i = 0; (int) (i * vidFps / fps) < len; i++) {
                frameGrabber.setVideoFrameNumber((int) (i * vidFps / fps));
                f = frameGrabber.grab();
                BufferedImage frame = c.convert(f); //Memory reference (native method)

                if (frame == null) {
                    continue;
                }

                BufferedImage finalFrame = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
                finalFrame.createGraphics().drawImage(frame, 0, 0, null);
                result.add(finalFrame);

            }
            frameGrabber.stop();

            frames = result.toArray(BufferedImage[]::new);

        } catch (FrameGrabber.Exception e) {
            ConsoleUtils.logError(e);
        }
    }

}

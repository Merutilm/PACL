package kr.tkdydwk7071.laboratory.mac;

import kr.tkdydwk7071.base.io.BitMapImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

final class SampleImageTransparentParser{
    private SampleImageTransparentParser() {
    }

    public static void main(String[] args) throws IOException {
        String filePath = "";
        String fromTo = "";

        BitMapImage img = new BitMapImage(ImageIO.read(new File(filePath)));
        img.blackToAlpha();
        ImageIO.write(img.getImage(), "png", new File(fromTo));
    }
}

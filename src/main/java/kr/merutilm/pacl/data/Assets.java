package kr.merutilm.pacl.data;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import kr.merutilm.base.struct.ImageFile;
import kr.merutilm.base.util.ConsoleUtils;

public final class Assets {
    private Assets() {
    }

    private static final Set<ImageFile> TAKEN_ASSET_LIST = new HashSet<>();

    private static URL getImageURL(ImageFile image) {
        return Assets.class.getResource("/" + image.toString());
    }

    public static Image getAsset(ImageFile imageFile) {
        URL url = getImageURL(imageFile);
        return Toolkit.getDefaultToolkit().getImage(url);
    }

    public static InputStream getAssetStream(ImageFile image) {
        return Assets.class.getResourceAsStream("/" + image.toString());
    }

    public static Image getIcon() {
        return getAsset(new ImageFile("icon.png"));
    }

    /**
     * 제공된 에셋을 결과 파일로 복사합니다. 동일 이름으로의 복사는 최초 1회 (업데이트 혹은 설치)만 허용합니다.
     */
    static void pasteAllCopiedAssets(File resultFilePath) {
        File temp = new File("temp");
        if (temp.exists()) {
            try {
                for (File file : Objects.requireNonNull(temp.listFiles())) {
                    Path path = Path.of(resultFilePath.getParentFile().getAbsolutePath(), file.getName());
                    Files.copy(file.toPath(), path, StandardCopyOption.REPLACE_EXISTING);
                    Files.delete(file.toPath());
                }
                Files.delete(temp.toPath());
            } catch (IOException e) {
                ConsoleUtils.logError(e);
            }
        }
    }

    public static void copyAsset(ImageFile imageFile) {
        try {
            File temp = new File("temp");
            if (!temp.exists()) {
                temp.mkdir();
            }
            if (!TAKEN_ASSET_LIST.contains(imageFile) && getImageURL(imageFile) != null) {
                TAKEN_ASSET_LIST.add(imageFile);
                Path path = new File(temp.getName() + '/' + imageFile).toPath();
                Files.copy(getImageURL(imageFile).openConnection().getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (IOException e) {
            ConsoleUtils.logError(e);
        }
    }
}

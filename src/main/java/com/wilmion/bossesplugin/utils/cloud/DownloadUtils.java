package com.wilmion.bossesplugin.utils.cloud;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class DownloadUtils {
    public static void downloadFile(String fileUrl, String destinationPath) throws IOException {
        URL url = new URL(fileUrl);
        Path destination = Path.of(destinationPath);

        Files.copy(url.openStream(), destination, StandardCopyOption.REPLACE_EXISTING);
    }
}

package com.wilmion.bossesplugin.utils.cloud;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {
    public static void extractZip(String zipFilePath, String destinationFolderPath) throws IOException {
        byte[] buffer = new byte[1024];

        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry zipEntry = zipInputStream.getNextEntry();

        while (zipEntry != null) {
            String entryPath = destinationFolderPath + File.separator + zipEntry.getName();
            File entryFile = new File(entryPath);

            if (zipEntry.isDirectory()) {
                entryFile.mkdirs();
                zipEntry = zipInputStream.getNextEntry();
                continue;
            }

            File parentDir = entryFile.getParentFile();

            if (parentDir != null && !parentDir.exists()) parentDir.mkdirs();

            FileOutputStream outputStream = new FileOutputStream(entryFile);
            Integer bytesRead;

            while ((bytesRead = zipInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            zipEntry = zipInputStream.getNextEntry();
        }
    }
}

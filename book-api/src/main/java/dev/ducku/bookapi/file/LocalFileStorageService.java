package dev.ducku.bookapi.file;

import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import static java.io.File.separator;
import static java.lang.System.currentTimeMillis;

@Service
@Slf4j
public class LocalFileStorageService implements FileStorageService {
    @Value("${file.uploads.photos-output-path}")
    private String outputPath;

    @Override
    public String saveFile(@Nonnull MultipartFile file, @Nonnull Integer bookId) {
        final String fileUploadSubPath = "covers" + separator + bookId;
        return uploadFile(file, fileUploadSubPath);
    }

    private String uploadFile(MultipartFile file, String fileUploadSubPath) {
        final String finalUploadPath = outputPath + separator + fileUploadSubPath;
        File targetFile = new File(finalUploadPath);
        if (!targetFile.exists()) {
            boolean isDirectoryCreated = targetFile.mkdirs();
            if (!isDirectoryCreated) {
                log.error("Could not create directory: {}", targetFile.getAbsolutePath());
                return null;
            }
        }
        String extension = getFileExtension(Objects.requireNonNull(file.getOriginalFilename()));
        String targetFilePath = finalUploadPath + separator + currentTimeMillis() + "." + extension;
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, file.getBytes());
            log.info("File saved to {}", targetFilePath);
            return targetFilePath;
        } catch (IOException ex) {
            log.error("Could not save file", ex);
            return null;
        }
    }

    private String getFileExtension(@Nonnull String fileName) {
        if (fileName.isBlank() || fileName.isEmpty()) {
            return null;
        }
        int indexOfDot = fileName.lastIndexOf(".");
        if (indexOfDot == -1) {
            return null;
        }
        return fileName.substring(indexOfDot + 1).toLowerCase();
    }

}

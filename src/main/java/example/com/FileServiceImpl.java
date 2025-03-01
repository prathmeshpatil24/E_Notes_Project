// FileServiceImpl
package example.com;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;



@Service
public class FileServiceImpl implements FileService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Override
    public String uploadFile(MultipartFile file, String userEmail) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file");
        }

        // Create user-specific directory
        String userDir = uploadPath + userEmail + "/";
        File directory = new File(userDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Generate unique file name
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
        String storePath = userDir + uniqueFilename;

        // Save the file
        Files.copy(file.getInputStream(), Paths.get(storePath));

        return storePath; // Return the full path
    }

    // Accept full path
    @Override
    public byte[] downloadFile(String fullFilePath) throws Exception {
       // String fullPath = uploadPath + fileName; // Adjust if user-specific path is needed
        File file = new File(fullFilePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + fullFilePath);
        }
        try (FileInputStream inputStream = new FileInputStream(fullFilePath)) {
            return StreamUtils.copyToByteArray(inputStream);
        }
    }
}
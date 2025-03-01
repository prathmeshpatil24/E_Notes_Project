package example.com;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
   public String uploadFile(MultipartFile file, String userEmail) throws IOException;
   public byte[] downloadFile(String fileName) throws Exception;
}
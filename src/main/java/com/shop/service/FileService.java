package com.shop.service;


import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Log
public class FileService {
    public String uploadFile(String uploadPath, String originalFileName, byte[] fileData) throws Exception {
        UUID uuid = UUID.randomUUID();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String savedFileName = uuid.toString() + extension;
        String fileUploadFullUrl = uploadPath + "/" + savedFileName;
        log.info(fileUploadFullUrl);

        Path path = Paths.get(fileUploadFullUrl);
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }

        FileOutputStream fos = new FileOutputStream(fileUploadFullUrl);
        fos.write(fileData);
        fos.close();
        return savedFileName;
    }

    public void deleteFile(String filePath) throws Exception {
        File deleteFile = new File(filePath);
        if (deleteFile.exists()) {
            deleteFile.delete();
            log.info("파일을 삭제했습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }

    // // 파일 이동 ( 원본 경로, 원본경로 기준 대상 폴더)
    // public void moveFile(String filePath, String destinationFolder) throws Exception {
    //     Path sourcePath = Paths.get(filePath);
    //     Path destinationPath = Paths.get(destinationFolder + "/" + filePath);

    //     try {
    //         // 파일 이동
    //         Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
    //         log.info("파일이 성공적으로 이동되었습니다.");
    //     } catch (IOException e) {
    //         System.err.println("파일 이동 중 오류 발생: " + e.getMessage());
    //     }
        
    // }
}

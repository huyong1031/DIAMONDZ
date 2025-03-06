//package com.himedia.spserver.controller;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Paths;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/image")
//public class ImageController {
//
//    // ✅ YML에서 설정된 업로드 디렉토리 가져오기
//    @Value("${file.upload.customer_dir}")
//    private String uploadDir;
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
//        if (file.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("파일이 없습니다.");
//        }
//
//        try {
//            // ✅ 파일 이름 설정 (UUID로 중복 방지)
//            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//            String filePath = Paths.get(uploadDir, fileName).toString();
//
//            // ✅ 폴더가 없으면 생성
//            File uploadFolder = new File(uploadDir);
//            if (!uploadFolder.exists()) {
//                uploadFolder.mkdirs();
//            }
//
//            // ✅ 파일 저장
//            file.transferTo(new File(filePath));
//
//            // ✅ React에서 접근할 수 있도록 URL 반환
//            String fileUrl = "/uploads/customer_images/" + fileName;
//            return ResponseEntity.ok(fileUrl);
//
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패");
//        }
//    }
//}

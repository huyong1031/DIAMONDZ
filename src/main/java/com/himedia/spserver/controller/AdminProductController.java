package com.himedia.spserver.controller;

import com.himedia.spserver.entity.Product;
import com.himedia.spserver.service.AdminProductService;
import com.himedia.spserver.dto.ProductDTO; // ProductDTO import

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File; // File import 추가
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.UUID;

import org.springframework.core.io.ResourceLoader; // ResourceLoader import 추가
import org.springframework.util.ResourceUtils; // ResourceUtils import 추가



@RestController
@RequestMapping("/admin/product")
public class AdminProductController {

    @Autowired
    private AdminProductService adminProductService;

    @Value("${file.upload.dir}")
    private String uploadDir;

    private final ResourceLoader resourceLoader; // ResourceLoader 주입

    @Autowired // 생성자 주입
    public AdminProductController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }


    @GetMapping("/getProductList")
    public ResponseEntity<?> getProductList(
            @RequestParam("page") int page,
            @RequestParam(value = "key", required = false) String key) {

        HashMap<String, Object> result = adminProductService.getProductList(page, key);
        return ResponseEntity.ok(result);
    }

    // 범용 업로드 메서드 (DRY 원칙)
    private HashMap<String, Object> uploadFile(MultipartFile file) throws IOException {
        HashMap<String, Object> result = new HashMap<>();

        if (file == null || file.isEmpty()) { // 파일이 null이거나 비어있는 경우 처리
            result.put("savefilename", ""); // 빈 문자열 저장
            result.put("image", "");
            return result;
        }

        String originalFilename = file.getOriginalFilename(); // 원본 파일 이름
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String saveFilename = originalFilename; // 원본 파일 이름을 저장 파일 이름으로 사용

        // 수정된 부분: Paths.get(uploadDir) -> ResourceUtils.getFile(uploadDir).toPath()
        File uploadDirFile = ResourceUtils.getFile(uploadDir); // uploadDir를 File 객체로 변환
        Path uploadPath = uploadDirFile.toPath().toAbsolutePath(); // File 객체에서 Path 객체 획득 및 절대 경로로 변환
        Files.createDirectories(uploadPath);
        Path filePath = uploadPath.resolve(saveFilename);


        file.transferTo(filePath.toFile());
        result.put("savefilename", saveFilename);
        result.put("image", originalFilename); //"image" 키는 WriteProduct 와의 약속된 키.
        return result;
    }

    // productImage 업로드 처리
    @PostMapping("/uploadproductImage")
    public ResponseEntity<HashMap<String, Object>> uploadProductImage(@RequestParam("image") MultipartFile file) {
        try {
            HashMap<String, Object> result = uploadFile(file); // 공통 메서드 호출
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            System.err.println("파일 업로드 중 IO 오류 발생: " + e.getMessage());
            e.printStackTrace();
            HashMap<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "파일 업로드 중 IO 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    // productImage2 업로드
    @PostMapping("/uploadproductImage2")
    public ResponseEntity<HashMap<String, Object>> uploadProductImage2(@RequestParam("image") MultipartFile file) {
        try {
            return ResponseEntity.ok(uploadFile(file));
        } catch (IOException e) {
            System.err.println("파일 업로드 중 IO 오류 발생: " + e.getMessage());
            e.printStackTrace();
            HashMap<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "파일 업로드 중 IO 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    // productImage3 업로드
    @PostMapping("/uploadproductImage3")
    public ResponseEntity<HashMap<String, Object>> uploadProductImage3(@RequestParam("image") MultipartFile file) {
        try {
            return ResponseEntity.ok(uploadFile(file));
        } catch (IOException e) {
            System.err.println("파일 업로드 중 IO 오류 발생: " + e.getMessage());
            e.printStackTrace();
            HashMap<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "파일 업로드 중 IO 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    // productImage4 업로드
    @PostMapping("/uploadproductImage4")
    public ResponseEntity<HashMap<String, Object>> uploadProductImage4(@RequestParam("image") MultipartFile file) {
        try {
            return ResponseEntity.ok(uploadFile(file));
        } catch (IOException e) {
            System.err.println("파일 업로드 중 IO 오류 발생: " + e.getMessage());
            e.printStackTrace();
            HashMap<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "파일 업로드 중 IO 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    // infoImage 업로드 처리
    @PostMapping("/uploadinfoImage")
    public ResponseEntity<HashMap<String, Object>> uploadInfoImage(@RequestParam("image") MultipartFile file) {
        try {
            return ResponseEntity.ok(uploadFile(file));
        } catch (IOException e) {
            System.err.println("파일 업로드 중 IO 오류 발생: " + e.getMessage());
            e.printStackTrace();
            HashMap<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "파일 업로드 중 IO 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    // infoImage2 업로드
    @PostMapping("/uploadinfoImage2")
    public ResponseEntity<HashMap<String, Object>> uploadInfoImage2(@RequestParam("image") MultipartFile file) {
        try {
            return ResponseEntity.ok(uploadFile(file));
        } catch (IOException e) {
            System.err.println("파일 업로드 중 IO 오류 발생: " + e.getMessage());
            e.printStackTrace();
            HashMap<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "파일 업로드 중 IO 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    // infoImage3 업로드
    @PostMapping("/uploadinfoImage3")
    public ResponseEntity<HashMap<String, Object>> uploadInfoImage3(@RequestParam("image") MultipartFile file) {
        try {
            return ResponseEntity.ok(uploadFile(file));
        } catch (IOException e) {
            System.err.println("파일 업로드 중 IO 오류 발생: " + e.getMessage());
            e.printStackTrace();
            HashMap<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "파일 업로드 중 IO 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    // infoImage4 업로드
    @PostMapping("/uploadinfoImage4")
    public ResponseEntity<HashMap<String, Object>> uploadInfoImage4(@RequestParam("image") MultipartFile file) {
        try {
            return ResponseEntity.ok(uploadFile(file));
        } catch (IOException e) {
            System.err.println("파일 업로드 중 IO 오류 발생: " + e.getMessage());
            e.printStackTrace();
            HashMap<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "파일 업로드 중 IO 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }

    // infoImage5 업로드
    @PostMapping("/uploadinfoImage5")
    public ResponseEntity<HashMap<String, Object>> uploadInfoImage5(@RequestParam("image") MultipartFile file) {
        try {
            return ResponseEntity.ok(uploadFile(file));
        } catch (IOException e) {
            System.err.println("파일 업로드 중 IO 오류 발생: " + e.getMessage());
            e.printStackTrace();
            HashMap<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "파일 업로드 중 IO 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    // hoverImage 업로드
    @PostMapping("/uploadhoverImage")
    public ResponseEntity<HashMap<String, Object>> uploadhoverImage(@RequestParam("image") MultipartFile file) {
        try {
            return ResponseEntity.ok(uploadFile(file));
        } catch (IOException e) {
            System.err.println("파일 업로드 중 IO 오류 발생: " + e.getMessage());
            e.printStackTrace();
            HashMap<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "파일 업로드 중 IO 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }


    @GetMapping("/{productSeq}") // 상품 조회
    public ResponseEntity<?> getProduct(@PathVariable("productSeq") Integer productSeq) {
        try {
            Product product = adminProductService.getProduct(productSeq);
            if (product != null) {
                return ResponseEntity.ok(product);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("상품 조회 중 오류 발생");
        }
    }


    @PostMapping("/writeProduct") // 상품 등록
    public ResponseEntity<?> writeProduct(@RequestBody Product product) {
        try {
            Product savedProduct = adminProductService.saveProduct(product); // AdminProductService의 saveProduct 호출
            return ResponseEntity.ok("상품 등록 완료: " + savedProduct.getProductSeq()); // 저장된 상품의 ID 반환
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("상품 등록 중 오류 발생");
        }
    }

    @GetMapping("/getProduct") // 상품 조회
    public ResponseEntity<?> getProduct(@RequestParam("productSeq") Long productSeq) {
        if (productSeq == null) {
            return ResponseEntity.badRequest().body("productSeq cannot be null");
        }

        Product product = adminProductService.getProduct(Math.toIntExact(productSeq));

        if (product == null) {
            return ResponseEntity.notFound().build();
        }

        ProductDTO productDTO = ProductDTO.fromEntity(product); // 엔티티를 DTO로 변환
        return ResponseEntity.ok(productDTO); // DTO 반환
    }

    @PutMapping("/{productSeq}") // 상품 수정
    public ResponseEntity<?> updateProduct(@PathVariable Long productSeq, @RequestBody Product product) {
        try {
            // 기존 상품 정보를 가져옵니다.
            Product existingProduct = adminProductService.getProduct(Math.toIntExact(productSeq)); // productSeq를 Integer로 변환
            if (existingProduct == null) {
                return ResponseEntity.notFound().build();
            }

            // 기존 상품 정보에 변경된 값을 적용합니다. (텍스트 필드 업데이트 - 기존 코드 유지)
            existingProduct.setProductName(product.getProductName());
            existingProduct.setProductContent(product.getProductContent());
            existingProduct.setProductBest(product.getProductBest());
            existingProduct.setProductUse(product.getProductUse());
            existingProduct.setProductCostPrice(product.getProductCostPrice());
            existingProduct.setProductSalePrice(product.getProductSalePrice());
            existingProduct.setProductMarginPrice(product.getProductMarginPrice());
            existingProduct.setProductStatus(product.getProductStatus());

            // **이미지 필드 업데이트 추가 (아래 10줄 추가)**
            existingProduct.setProductImage(product.getProductImage());
            existingProduct.setProductImage2(product.getProductImage2());
            existingProduct.setProductImage3(product.getProductImage3());
            existingProduct.setProductImage4(product.getProductImage4());
            existingProduct.setInfoImage(product.getInfoImage());
            existingProduct.setInfoImage2(product.getInfoImage2());
            existingProduct.setInfoImage3(product.getInfoImage3());
            existingProduct.setInfoImage4(product.getInfoImage4());
            existingProduct.setInfoImage5(product.getInfoImage5());
            existingProduct.setHoverImage(product.getHoverImage());


            // 업데이트된 상품 정보를 저장합니다.
            Product updatedProduct = adminProductService.saveProduct(existingProduct);
            return ResponseEntity.ok(updatedProduct);
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("상품 수정 중 오류 발생");
        }
    }

    @DeleteMapping("/deleteProduct/{productSeq}") // 상품 삭제
    public ResponseEntity<?> deleteProduct(@PathVariable Long productSeq) {
        try {
            adminProductService.deleteProduct(Math.toIntExact(productSeq));
            return ResponseEntity.ok("상품 삭제 성공");
        } catch (Exception e) {
            System.err.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("상품 삭제 중 오류 발생");
        }
    }
}
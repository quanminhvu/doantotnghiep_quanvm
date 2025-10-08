package com.quanvm.applyin.controller;

import com.quanvm.applyin.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
public class UploadController {

  private final S3Client s3Client;
  
  public record UploadResponse(String fileName, String fileUrl, long fileSize) {}

  @Value("${r2.bucket}")
  private String bucket;

  @Value("${r2.endpoint}")
  private String endpoint;

  @Value("${r2.publicBaseUrl}")
  private String publicBaseUrl;

  public UploadController(S3Client s3Client) {
    this.s3Client = s3Client;
  }

  @PostMapping
  public ResponseEntity<ApiResponse<UploadResponse>> uploadFile(@RequestParam("file") MultipartFile file) {
    try {
      if (file.isEmpty()) {
        return ResponseEntity.badRequest()
            .body(ApiResponse.error(400, "File không được để trống"));
      }

      String originalFileName = file.getOriginalFilename();
      String fileExtension = originalFileName != null && originalFileName.contains(".") 
          ? originalFileName.substring(originalFileName.lastIndexOf("."))
          : "";
      String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

      s3Client.putObject(
          PutObjectRequest.builder()
              .bucket(bucket)
              .key(uniqueFileName)
              .contentType(file.getContentType())
              .build(),
          RequestBody.fromBytes(file.getBytes())
      );

      String publicUrl = publicBaseUrl + "/" + uniqueFileName;
      UploadResponse uploadResponse = new UploadResponse(
          originalFileName, 
          publicUrl, 
          file.getSize()
      );

      return ResponseEntity.ok(
          ApiResponse.ok("Upload thành công", uploadResponse)
      );

    } catch (IOException e) {
      return ResponseEntity.status(500)
          .body(ApiResponse.error(500, "Lỗi upload: " + e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(500)
          .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
    }
  }
}
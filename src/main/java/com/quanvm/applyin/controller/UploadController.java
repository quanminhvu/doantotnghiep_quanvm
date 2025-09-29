package com.quanvm.applyin.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@RestController
@RequestMapping("/upload")
public class UploadController {

  private final S3Client s3Client;

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
  public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
    try {
      s3Client.putObject(
          PutObjectRequest.builder()
              .bucket(bucket)
              .key(file.getOriginalFilename())
              .build(),
          RequestBody.fromBytes(file.getBytes())
      );

      String publicUrl = publicBaseUrl + "/" + file.getOriginalFilename();

      return ResponseEntity.ok("Upload thành công! URL: " + publicUrl);

    } catch (IOException e) {
      return ResponseEntity.status(500).body("Lỗi upload: " + e.getMessage());
    }
  }
}
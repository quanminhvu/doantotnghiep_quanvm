package com.quanvm.applyin.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "r2")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class R2Properties {
  private String endpoint;
  private String bucket;
  private String accessKey;
  private String secretKey;

}
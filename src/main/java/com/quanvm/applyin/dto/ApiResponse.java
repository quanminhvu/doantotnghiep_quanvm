package com.quanvm.applyin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
  private int statusCode;
  private String message;
  private T data;

  public static <T> ApiResponse<T> ok(String message, T data) {
    return ApiResponse.<T>builder().statusCode(200).message(message).data(data).build();
  }

  public static <T> ApiResponse<T> of(int statusCode, String message, T data) {
    return ApiResponse.<T>builder().statusCode(statusCode).message(message).data(data).build();
  }
}



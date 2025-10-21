package com.quanvm.applyin.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
public class HealthController {

    @GetMapping("")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }
}

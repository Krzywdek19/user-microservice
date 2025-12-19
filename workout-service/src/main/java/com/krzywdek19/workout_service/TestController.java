package com.krzywdek19.workout_service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api/v1/workouts/test")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("Hello");
    }
}

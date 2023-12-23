package com.CarServieStation.backend.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/demo-controller")
public class DemoController {

    @GetMapping("/admin")
    public ResponseEntity<String> sayHelloToAdmin() {
        return ResponseEntity.ok("Hello from ADMIN!");
    }

    @GetMapping("/employee")
    public ResponseEntity<String> sayHelloToEmployee() {
        return ResponseEntity.ok("Hello from EMPLOYEE!");
    }

    @GetMapping("/user")
    public ResponseEntity<String> sayHelloToClient() {
        return ResponseEntity.ok("Hello from USER!");
    }
}

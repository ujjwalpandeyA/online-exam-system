	package com.exam.controller;

	import org.springframework.http.ResponseEntity;
	import org.springframework.web.bind.annotation.GetMapping;
	import org.springframework.web.bind.annotation.RequestMapping;
	import org.springframework.web.bind.annotation.RestController;

	import java.util.HashMap;
	import java.util.Map;

	@RestController
	@RequestMapping("/api/v1/health")
	public class AppHealthCheckController {

	    @GetMapping
	    public ResponseEntity<Map<String, String>> checkHealth() {
	        Map<String, String> status = new HashMap<>();
	        status.put("status", "UP");
	        status.put("message", "Online Exam System application is up and running successfully.");
	        return ResponseEntity.ok(status);
	    }
	    @GetMapping("/info")
	    public ResponseEntity<Map<String, String>> appInfo() {
	        Map<String, String> info = new HashMap<>();
	        info.put("appName", "Online Exam System");
	        info.put("version", "1.0.0");
	        return ResponseEntity.ok(info);
	    }
	}



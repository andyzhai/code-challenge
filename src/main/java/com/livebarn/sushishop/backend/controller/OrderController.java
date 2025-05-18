package com.livebarn.sushishop.backend.controller;

import com.livebarn.sushishop.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
class OrderController {
	private final OrderService service;

	@PostMapping
	public Mono<ResponseEntity<Map<String, Object>>> createOrder(@RequestBody Map<String, String> req) {
		return service.submit(req.get("sushi_name"))
				.map(order -> ResponseEntity.status(201).body(Map.of(
						"code", 0,
						"msg", "Order created",
						"order", order
				)));
	}

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Map<String, Object>>> cancelOrder(@PathVariable int id) {
		return service.cancel(id).map(success -> ResponseEntity.ok(Map.of(
				"code", success ? 0 : 1,
				"msg", success ? "Order cancelled" : "Not found"
		)));
	}

	@PutMapping("/{id}/pause")
	public Mono<ResponseEntity<Map<String, Object>>> pauseOrder(@PathVariable int id) {
		return service.pause(id).map(success -> ResponseEntity.ok(Map.of(
				"code", success ? 0 : 1,
				"msg", success ? "Order paused" : "Invalid state"
		)));
	}

	@PutMapping("/{id}/resume")
	public Mono<ResponseEntity<Map<String, Object>>> resumeOrder(@PathVariable int id) {
		return service.resume(id).map(success -> ResponseEntity.ok(Map.of(
				"code", success ? 0 : 1,
				"msg", success ? "Order resumed" : "Invalid state"
		)));
	}

	@GetMapping("/status")
	public Mono<ResponseEntity<Map<String, List<Map<String, Object>>>>> getStatus() {
		return service.getStatus().map(ResponseEntity::ok);
	}
}

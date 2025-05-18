/**
 * 
 */
package com.livebarn.sushishop.backend.service;

import java.time.Instant;
import java.util.*;

import com.livebarn.sushishop.backend.domain.Status;
import com.livebarn.sushishop.backend.domain.Sushi;
import com.livebarn.sushishop.backend.domain.SushiOrder;
import com.livebarn.sushishop.backend.repo.SushiOrderRepo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * @author xin
 *
 */
@Service
@RequiredArgsConstructor
public class OrderService {
    private final SushiOrderRepo orderRepo;
    private final StaticDataService staticData;
    private final Sinks.Many<SushiOrder> taskSink;

    public Mono<SushiOrder> submit(String sushiName) {
        Sushi sushi = staticData.getAllSushi().stream()
                .filter(s -> s.getName().equalsIgnoreCase(sushiName))
                .findFirst()
                .orElseThrow();

        SushiOrder order = new SushiOrder(null,
                staticData.getStatusIdByName("created"),
                sushi.getId(),
                Instant.now(),
                0L,
                0L);

        return orderRepo.save(order)
                .doOnNext(taskSink::tryEmitNext);
    }

    public Mono<Boolean> pause(int id) {
        return orderRepo.findById(id).flatMap(order -> {
            if (order.getStatusId().equals(staticData.getStatusIdByName("in-progress"))) {
                long now = Instant.now().getEpochSecond();
                order.setTimeSpent(order.getTimeSpent() + (now - order.getLastStartedAt()));
                order.setStatusId(staticData.getStatusIdByName("paused"));
                return orderRepo.save(order).thenReturn(true);
            }
            return Mono.just(false);
        });
    }

    public Mono<Boolean> resume(int id) {
        return orderRepo.findById(id).flatMap(order -> {
            if (order.getStatusId().equals(staticData.getStatusIdByName("paused"))) {
                order.setStatusId(staticData.getStatusIdByName("created"));
                return orderRepo.save(order)
                        .doOnNext(taskSink::tryEmitNext)
                        .thenReturn(true);
            }
            return Mono.just(false);
        });
    }

    public Mono<Boolean> cancel(int id) {
        return orderRepo.findById(id).flatMap(order -> {
            if (!order.getStatusId().equals(staticData.getStatusIdByName("finished"))) {
                if (order.getStatusId().equals(staticData.getStatusIdByName("in-progress"))) {
                    long now = Instant.now().getEpochSecond();
                    order.setTimeSpent(order.getTimeSpent() + (now - order.getLastStartedAt()));
                }
                order.setStatusId(staticData.getStatusIdByName("cancelled"));
                return orderRepo.save(order).thenReturn(true);
            }
            return Mono.just(false);
        });
    }

    public Mono<Map<String, List<Map<String, Object>>>> getStatus() {
        return orderRepo.findAll()
                .collectList()
                .map(orders -> {
                    Map<String, List<Map<String, Object>>> result = new LinkedHashMap<>();
                    for (Status status : staticData.statusNameToId.entrySet().stream()
                            .map(entry -> new Status(entry.getValue(), entry.getKey()))
                            .toList()) {
                        result.put(status.getName(), new ArrayList<>());
                    }
                    for (SushiOrder order : orders) {
                        long timeSpent = Optional.ofNullable(order.getTimeSpent()).orElse(0L);
                        String statusName = staticData.getStatusNameById(order.getStatusId());
                        if ("in-progress".equals(statusName)) {
                            timeSpent += (Instant.now().getEpochSecond() - order.getLastStartedAt());
                        }
                        result.get(statusName).add(Map.of(
                                "orderId", order.getId(),
                                "timeSpent", timeSpent
                        ));
                    }
                    return result;
                });
    }
}

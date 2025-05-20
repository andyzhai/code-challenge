package com.livebarn.sushishop.backend.helper;

import com.livebarn.sushishop.backend.domain.RuntimeOrderState;
import com.livebarn.sushishop.backend.domain.Sushi;
import com.livebarn.sushishop.backend.domain.SushiOrder;
import com.livebarn.sushishop.backend.repo.SushiOrderRepo;
import com.livebarn.sushishop.backend.service.OrderService;
import com.livebarn.sushishop.backend.service.StaticDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
class OrderProcessor {
    private final SushiOrderRepo orderRepository;
    private final StaticDataService staticData;
    private final Sinks.Many<SushiOrder> taskSink;
    private final OrderService orderService;

    private static final int MAX_CHEFS = 3;

    @PostConstruct
    public void init() {
        taskSink.asFlux()
                .flatMap(this::checkAndProcessOrder, MAX_CHEFS)
                .subscribe();
    }

    private Mono<Void> checkAndProcessOrder(SushiOrder order) {
        // fix the bug when cancelling an order in waiting list, double check if the order is still in "created" status
        return orderRepository.findById(order.getId())
                .flatMap(this::processOrder)
                .doOnError(e -> {
                    // Handle error
                    System.err.println("Error processing order: " + e.getMessage());
                })
                .then();
    }
    private Mono<Void> processOrder(SushiOrder order) {
        Integer inProgressId = staticData.getStatusIdByName("in-progress");
        //Integer completedId = staticData.getStatusIdByName("finished");
        //Integer pausedId = staticData.getStatusIdByName("paused");
        RuntimeOrderState orderState = orderService.getOrderStates().get(order.getId());
        orderState.setLastStartedAt(Instant.now().getEpochSecond());
        if (!order.getStatusId().equals(staticData.getStatusIdByName("created"))) {
            return Mono.empty();
        }
        order.setStatusId(inProgressId);
        orderState.setLastStartedAt(Instant.now().getEpochSecond());
        //orderRepository.save(order).subscribe();

        Sushi sushi = staticData.getSushiById(order.getSushiId());
        long totalTime = sushi.getTimeToMake();
        //long timeLeft = totalTime - order.getTimeSpent();
        long updatedTimeLeft = totalTime = orderState.getTimeSpent();
        return orderRepository.save(order)
                .then(Mono
                        .delay(Duration.ofSeconds(sushi.getTimeToMake() - orderState.getTimeSpent()))
                        .publishOn(Schedulers.parallel())
                        .flatMap(tick -> orderRepository.findById(order.getId()).flatMap(latest -> {
                            if (latest.getStatusId().equals(staticData.getStatusIdByName("in-progress"))) {
                                RuntimeOrderState orderState1stat = orderService.getOrderStates().get(latest.getId());
                                orderState.setTimeSpent((long) sushi.getTimeToMake());
                                latest.setStatusId(staticData.getStatusIdByName("finished"));
                                return orderRepository.save(latest).then();
                            } else {
                                return Mono.empty(); // Do not finish if it's paused/cancelled
                            }
                        })));
    }
}

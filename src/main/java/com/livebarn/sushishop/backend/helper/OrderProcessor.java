package com.livebarn.sushishop.backend.helper;

import com.livebarn.sushishop.backend.domain.Sushi;
import com.livebarn.sushishop.backend.domain.SushiOrder;
import com.livebarn.sushishop.backend.repo.SushiOrderRepo;
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
    private static final int MAX_CHEFS = 3;

    @PostConstruct
    public void init() {
        taskSink.asFlux()
                .flatMap(this::processOrder, MAX_CHEFS)
                .subscribe();
    }

    private Mono<Void> processOrder(SushiOrder order) {
        Integer inProgressId = staticData.getStatusIdByName("in-progress");
        Integer completedId = staticData.getStatusIdByName("finished");
        Integer pausedId = staticData.getStatusIdByName("paused");

        if (!order.getStatusId().equals(staticData.getStatusIdByName("created")) &&
                !order.getStatusId().equals(pausedId)) {
            return Mono.empty();
        }

        order.setStatusId(inProgressId);
        order.setLastStartedAt(Instant.now().getEpochSecond());
        orderRepository.save(order).subscribe();

        Sushi sushi = staticData.getSushiById(order.getSushiId());
        long totalTime = sushi.getTimeToMake();
        long timeLeft = totalTime - order.getTimeSpent();

        return Mono.delay(Duration.ofSeconds(timeLeft))
                .publishOn(Schedulers.parallel())
                .doOnNext(tick -> {
                    if (order.getStatusId().equals(inProgressId)) {
                        order.setTimeSpent(totalTime);
                        order.setStatusId(completedId);
                        orderRepository.save(order).subscribe();
                    }
                })
                .then();
    }
}

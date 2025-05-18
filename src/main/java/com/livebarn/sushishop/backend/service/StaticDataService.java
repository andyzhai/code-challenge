package com.livebarn.sushishop.backend.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.livebarn.sushishop.backend.domain.Sushi;
import com.livebarn.sushishop.backend.repo.StatusRepo;
import com.livebarn.sushishop.backend.repo.SushiRepo;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@DependsOn("dataInitializer") // Matches the bean name
public class StaticDataService {
    private final SushiRepo sushiRepo;
    private final StatusRepo statusRepo;

    private final Map<Integer, Sushi> sushiMap = new HashMap<>();
    public final Map<String, Integer> statusNameToId = new HashMap<>();
    private final Map<Integer, String> statusIdToName = new HashMap<>();

    public Sushi getSushiById(Integer id) {
        return sushiMap.get(id);
    }

    public Integer getStatusIdByName(String name) {
        return statusNameToId.get(name);
    }

    public String getStatusNameById(Integer id) {
        return statusIdToName.get(id);
    }

    public Collection<Sushi> getAllSushi() {
        return sushiMap.values();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        loadStaticData().subscribe();
    }

    public Mono<Void> loadStaticData() {
        return sushiRepo.findAll()
                .doOnNext(sushi -> sushiMap.put(sushi.getId(), sushi))
                .thenMany(statusRepo.findAll())
                .doOnNext(status -> {
                    statusNameToId.put(status.getName().toLowerCase(), status.getId());
                    statusIdToName.put(status.getId(), status.getName().toLowerCase());
                })
                .then();
    }
}

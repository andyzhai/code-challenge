package com.livebarn.sushishop.backend.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.r2dbc.core.DatabaseClient;
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
    private final DatabaseClient databaseClient;

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

    //@PostConstruct
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        //initializeSchemaAndData()
        //        .thenMany(loadStaticData())
        //        .subscribe();
        loadStaticData().subscribe();
    }
/**
    private reactor.core.publisher.Mono<Void> initializeSchemaAndData() {
        return databaseClient.sql("""
                DROP TABLE IF EXISTS sushi_order;
                DROP TABLE IF EXISTS sushi;
                DROP TABLE IF EXISTS status;

                CREATE TABLE status (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(30) NOT NULL
                );

                CREATE TABLE sushi (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(30),
                    time_to_make INT DEFAULT NULL
                );

                CREATE TABLE sushi_order (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    status_id INT NOT NULL,
                    sushi_id INT NOT NULL,
                    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                );

                INSERT INTO sushi (name, time_to_make) VALUES
                    ('California Roll', 30),
                    ('Kamikaze Roll', 40),
                    ('Dragon Eye', 50);

                INSERT INTO status (name) VALUES
                    ('created'),
                    ('in-progress'),
                    ('paused'),
                    ('finished'),
                    ('cancelled');
            """).then();
    }
**/
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

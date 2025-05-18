package com.livebarn.sushishop.backend.init;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {
/**
    private final DatabaseClient databaseClient;

    @Bean
    public ApplicationRunner initData() {
        return args -> {
            // Schema initialization
            databaseClient.sql("DROP TABLE IF EXISTS sushi_order").then().block();
            databaseClient.sql("DROP TABLE IF EXISTS sushi").then().block();
            databaseClient.sql("DROP TABLE IF EXISTS status").then().block();

            databaseClient.sql("""
                CREATE TABLE sushi (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(30),
                    time_to_make INT DEFAULT NULL
                )
            """).then().block();

            databaseClient.sql("""
                CREATE TABLE status (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(30) NOT NULL
                )
            """).then().block();

            databaseClient.sql("""
                CREATE TABLE sushi_order (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    status_id INT NOT NULL,
                    sushi_id INT NOT NULL,
                    createdAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
            """).then().block();

            // Insert static data
            databaseClient.sql("""
                INSERT INTO sushi (name, time_to_make) VALUES
                    ('California Roll', 30),
                    ('Kamikaze Roll', 40),
                    ('Dragon Eye', 50)
            """).then().block();

            databaseClient.sql("""
                INSERT INTO status (name) VALUES
                    ('created'),
                    ('in-progress'),
                    ('paused'),
                    ('finished'),
                    ('cancelled')
            """).then().block();
        };
    }
**/
}
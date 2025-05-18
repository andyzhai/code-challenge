package com.livebarn.sushishop.backend.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.livebarn.sushishop.backend.domain.Sushi;

public interface SushiRepo  extends ReactiveCrudRepository<Sushi, Integer>{

}

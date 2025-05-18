/**
 * 
 */
package com.livebarn.sushishop.backend.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.livebarn.sushishop.backend.domain.SushiOrder;

/**
 * @author xin
 *
 */
public interface SushiOrderRepo extends ReactiveCrudRepository<SushiOrder, Integer>{

}

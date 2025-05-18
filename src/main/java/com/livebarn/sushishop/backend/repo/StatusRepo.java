/**
 * 
 */
package com.livebarn.sushishop.backend.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.livebarn.sushishop.backend.domain.Status;

/**
 * @author xin
 *
 */
public interface StatusRepo extends ReactiveCrudRepository<Status, Integer> {

}

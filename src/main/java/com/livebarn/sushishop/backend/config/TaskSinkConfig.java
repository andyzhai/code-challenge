/**
 * 
 */
package com.livebarn.sushishop.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.livebarn.sushishop.backend.domain.SushiOrder;

import reactor.core.publisher.Sinks;

/**
 * @author xin
 *
 */
@Configuration
public class TaskSinkConfig {

	@Bean
    public Sinks.Many<SushiOrder> taskSink() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }
}

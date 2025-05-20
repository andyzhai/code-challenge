package com.livebarn.sushishop.backend.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RuntimeOrderState {
    private Long timeSpent;
    private Long lastStartedAt;
}

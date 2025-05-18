/**
 * 
 */
package com.livebarn.sushishop.backend.domain;

import java.time.Instant;

//import javax.persistence.Column;

//import javax.persistence.PrePersist;
//import javax.persistence.Table;
//import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;

/**
 * @author xin
 *
 */
//@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="sushi_order")
public class SushiOrder {
	@Id
//	@Column(name="id")
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

//	@Column(name="sushi_id")
	private Integer sushiId;

//	@Column(name="status_id")
	private Integer statusId;

	@Column("createdAt")
	private Instant createdAt;
    @Column("timeSpent")
    private Long timeSpent; // Track time spent
    @Column("lastStartedAt")
    private Long lastStartedAt; // Epoch seconds
	
}

/**
 * 
 */
package com.livebarn.sushishop.backend.domain;

import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xin
 *
 */
@Data
//@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name="sushi")
public class Sushi {
	@Id
//	@Column(name="id")
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
//	@Column(name="name")
	private String name;
//	@Column(name="time_to_make")
	private Integer timeToMake = null;
}

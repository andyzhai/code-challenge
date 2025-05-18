/**
 * 
 */
package com.livebarn.sushishop.backend.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xin
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
//@Entity
@Table(name="status")
public class Status {
	@Id
//	@Column(name="id")
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
//	@Column(name="name")
	@NonNull
	private String name;
}

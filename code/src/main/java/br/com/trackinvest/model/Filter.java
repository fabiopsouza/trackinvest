package br.com.trackinvest.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Filter {

	@Id
	@Getter
	@Setter
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
	
	private Integer dateStart;
	
	private Integer dateEnd;
	
	@Transient
	private boolean group;
	
	private BigDecimal targetYield;
	
	@Transient
	private String stock;
}

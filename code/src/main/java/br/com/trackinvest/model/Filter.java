package br.com.trackinvest.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Filter {

	private Integer dateStart;
	
	private Integer dateEnd;
	
	private boolean group;
	
	private BigDecimal targetYield;
	
	private String stock;
}

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
public class Result {

	private BigDecimal total;
	
	private BigDecimal average;
	
	private BigDecimal averageYield;
	
	private BigDecimal price;
	
	private BigDecimal limitPrice;
	
	private BigDecimal difference;
}

package br.com.trackinvest.model;

import java.math.BigDecimal;
import java.util.List;

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
@Entity
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Result {

	@Id
	@Getter
	@Setter
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
	
	private Boolean selected;
	
	private String symbol;
	
	private BigDecimal total;
	
	private BigDecimal liquidTotal;
	
	private BigDecimal average;
	
	private BigDecimal liquidAverage;
	
	private BigDecimal averageYield;
	
	private BigDecimal liquidAverageYield;
	
	private BigDecimal price;
	
	private BigDecimal limitPrice;
	
	@Transient
	public List<Yield> yields;
}

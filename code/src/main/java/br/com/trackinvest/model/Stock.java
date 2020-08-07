package br.com.trackinvest.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Stock {

	@Id
	@Getter
	@Setter
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
	
	@Getter
	@Setter
	private String symbol;
}

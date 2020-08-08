package br.com.trackinvest.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Stock {

	@Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
	
	private String symbol;
}

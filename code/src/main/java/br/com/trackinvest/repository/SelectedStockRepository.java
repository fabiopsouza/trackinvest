package br.com.trackinvest.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import br.com.trackinvest.model.SelectedStock;

public interface SelectedStockRepository extends CrudRepository<SelectedStock, Long> {

	boolean existsBySymbol(String symbol);
	
	@Transactional
	long deleteBySymbol(String symbol);
}

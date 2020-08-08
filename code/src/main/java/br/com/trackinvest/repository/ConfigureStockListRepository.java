package br.com.trackinvest.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.trackinvest.model.Stock;

public interface ConfigureStockListRepository extends CrudRepository<Stock, Long> {

}

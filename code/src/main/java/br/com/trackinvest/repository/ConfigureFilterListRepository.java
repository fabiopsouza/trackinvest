package br.com.trackinvest.repository;

import org.springframework.data.repository.CrudRepository;

import br.com.trackinvest.model.Filter;

public interface ConfigureFilterListRepository extends CrudRepository<Filter, Long> {

}

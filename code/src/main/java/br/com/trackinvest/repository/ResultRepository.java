package br.com.trackinvest.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import br.com.trackinvest.model.Result;

public interface ResultRepository extends CrudRepository<Result, Long> {

	public List<Result> findAllByOrderByLiquidAverageYieldDesc();
}

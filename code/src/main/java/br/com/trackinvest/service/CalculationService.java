package br.com.trackinvest.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import br.com.trackinvest.model.Filter;
import br.com.trackinvest.model.Result;
import br.com.trackinvest.model.Yield;

@Service
public class CalculationService {

	public Result calculate(List<Yield> yields, Filter filter, Future<Elements> futurePrice)
			throws IOException, InterruptedException, ExecutionException {

		if (CollectionUtils.isEmpty(yields)) {
			return null;
		}

		Result result = new Result();

		BigDecimal targetYield = filter.getTargetYield().divide(new BigDecimal("100"), MathContext.DECIMAL128);

		BigDecimal total = yields.stream().map(Yield::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
		Map<Integer, List<Yield>> yearGroup = yields.stream()
				.collect(Collectors.groupingBy(y -> y.getDate().getYear()));

		result.setTotal(total);
		result.setAverage(total.divide(BigDecimal.valueOf(yearGroup.size()), MathContext.DECIMAL128));
		result.setPrice(getPrice(futurePrice));
		result.setAverageYield(
				result.getAverage().divide(result.getPrice(), MathContext.DECIMAL128).multiply(new BigDecimal("100")));
		result.setLimitPrice(result.getAverage().divide(targetYield, MathContext.DECIMAL128));
		result.setDifference(result.getLimitPrice().subtract(result.getPrice()));

		result.setAverage(result.getAverage().setScale(4, RoundingMode.DOWN));
		result.setAverageYield(result.getAverageYield().setScale(2, RoundingMode.DOWN));
		result.setLimitPrice(result.getLimitPrice().setScale(2, RoundingMode.DOWN));
		result.setDifference(result.getDifference().setScale(2, RoundingMode.DOWN));

		return result;
	}

	private BigDecimal getPrice(Future<Elements> futurePrice)
			throws IOException, InterruptedException, ExecutionException {
		String value = futurePrice.get().get(0).text();
		return new BigDecimal(value.replace(',', '.'));
	}
}

package br.com.trackinvest.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.trackinvest.model.Filter;
import br.com.trackinvest.model.Result;
import br.com.trackinvest.model.Yield;

@Service
public class TrackService {

	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	@Autowired
	private ScrapService scraper;

	@Autowired
	private CalculationService calculationService;
	
	public Result track(Filter filter) throws IOException, InterruptedException, ExecutionException {
		
		return track(filter.getStock(), filter);
	}
	
	public Result track(String symbol, Filter filter) throws IOException, InterruptedException, ExecutionException {
		
		List<Yield> yields = new ArrayList<>();
		
		Future<Elements> futureDividends = scraper.scrapDividend(symbol);
		Future<Elements> futurePrice = scraper.scrapPrice(symbol);
		
		for (Element line : futureDividends.get()) {
		
			Elements columns = line.select("td");
			String date = columns.get(0).text();
			Integer year = getYear(date);
			
			if(year > filter.getDateEnd()) {
				continue;
			}
			
			if(year < filter.getDateStart()) {
				break;
			}
			
			BigDecimal value = new BigDecimal(columns.get(1).text().replace(',', '.'));
			String type = columns.get(2).text();
			
			//TODO mecanismo a parte que avisa por e-mail caso venha dominio diferente do conhecido
			BigDecimal liquidValue = calculationService.getLiquidValueWhenJscp(value, type);
				
			if(filter.isGroup()) {
				
				Yield yield = find(yields, year);
				if(yield != null) {
					yield.setValue(yield.getValue().add(value));
					yield.setLiquidValue(yield.getLiquidValue().add(liquidValue));
				}
				else {
					yields.add(new Yield(parse(date), year.toString(), value, liquidValue, "PROVENTOS"));
				}
			}
			else {
				yields.add(new Yield(parse(date), date, value, liquidValue, type));
			}
		}
		
		Result result = calculationService.calculate(yields, filter, futurePrice);
		
		if(result != null) {
			result.setSymbol(symbol);
			result.setYields(yields);
		}
		
		return result;
	}
	
	private Yield find(List<Yield> yields, Integer year) {
		
		return yields.stream().filter(y -> y.getDate().getYear() == year).findFirst().orElse(null);
	}

	private Integer getYear(String date) {		
		return Integer.valueOf(date.substring(6, 10));
	}
	
	public LocalDate parse(String date) {
		return LocalDate.parse(date, dateFormatter);
	}
}

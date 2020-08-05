package br.com.trackinvest.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import br.com.trackinvest.model.Filter;
import br.com.trackinvest.model.Result;
import br.com.trackinvest.model.Yield;

@Controller
public class HomeController {	
	
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	private static final String URL_PRICE = "https://statusinvest.com.br/acoes/%s";
	
	private static final String URL_DIVIDEND = "https://www.fundamentus.com.br/proventos.php?papel=%s&tipo=2";

	@GetMapping("/")
	public String home(Model model) {

		Filter filter = new Filter();
		filter.setDateStart(LocalDate.now().getYear() - 5);
		filter.setDateEnd(LocalDate.now().getYear() - 1);
		filter.setGroup(true);
		filter.setTargetYield(new BigDecimal("6"));
		
		model.addAttribute("filter", filter);
		model.addAttribute("yields", new ArrayList<>());
		
		return "home";
	}

	@PostMapping("/track")
	public String track(Model model, Filter filter) throws IOException, ParseException {
		
		List<Yield> yields = new ArrayList<>();
		Document page = Jsoup.connect(String.format(URL_DIVIDEND, filter.getStock())).get();

		Elements lines = page.select("#resultado tbody tr");
		for (Element line : lines) {
		
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
			
			if(filter.isGroup()) {
				
				Yield yield = find(yields, year);
				if(yield != null) {
					yield.setValue(yield.getValue().add(value));
				}
				else {
					yields.add(new Yield(LocalDate.parse(date, dateFormatter), year.toString(), value));
				}
			}
			else {
				yields.add(new Yield(LocalDate.parse(date, dateFormatter), date, value));
			}
		}
		
		model.addAttribute("filter", filter);
		model.addAttribute("result", calculate(yields, filter));
		model.addAttribute("yields", yields);

		return "home";
	}
	
	private Result calculate(List<Yield> yields, Filter filter) throws IOException {
		
		if(CollectionUtils.isEmpty(yields)) {
			return null;
		}
		
		Result result = new Result();
		
		BigDecimal targetYield = filter.getTargetYield().divide(new BigDecimal("100"), MathContext.DECIMAL128);
		
		BigDecimal total = yields.stream().map(Yield::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
		Map<Integer, List<Yield>> yearGroup = yields.stream().collect(Collectors.groupingBy(y -> y.getDate().getYear()));
		
		result.setTotal(total);
		result.setAverage(total.divide(BigDecimal.valueOf(yearGroup.size()), MathContext.DECIMAL128));
		result.setPrice(getPrice(filter.getStock()));
		result.setAverageYield(result.getAverage().divide(result.getPrice(), MathContext.DECIMAL128).multiply(new BigDecimal("100")));
		result.setLimitPrice(result.getAverage().divide(targetYield, MathContext.DECIMAL128));
		result.setDifference(result.getLimitPrice().subtract(result.getPrice()));
		
		result.setAverage(result.getAverage().setScale(4, RoundingMode.DOWN));
		result.setAverageYield(result.getAverageYield().setScale(2, RoundingMode.DOWN));
		result.setLimitPrice(result.getLimitPrice().setScale(2, RoundingMode.DOWN));
		result.setDifference(result.getDifference().setScale(2, RoundingMode.DOWN));
		
		return result;
	}

	private BigDecimal getPrice(String stock) throws IOException {
		
		Document page = Jsoup.connect(String.format(URL_PRICE, stock)).get();

		Elements elements = page.select(".special .value");
		String value = elements.get(0).text();
		
		return new BigDecimal(value.replace(',', '.'));
	}

	private Yield find(List<Yield> yields, Integer year) {
		
		for (int i = 0; i < yields.size(); i++) {
			
			if(yields.get(i).getDate().getYear() == year) {
				return yields.get(i);  
			}
		}
		
		return null;
	}

	private Integer getYear(String date) {		
		return Integer.valueOf(date.substring(6, 10));
	}
}

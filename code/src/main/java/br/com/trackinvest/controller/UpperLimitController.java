package br.com.trackinvest.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.trackinvest.model.Filter;
import br.com.trackinvest.model.Result;
import br.com.trackinvest.model.Yield;
import br.com.trackinvest.scraping.Scraper;

@Controller
public class UpperLimitController extends BaseController {	
	
	private static final String PAGE_INDEX = "/pages/upper-limit/index";
	
	@Autowired
	private Scraper scraper;
	
	@GetMapping("/")
	public String home(Model model) {

		conditionalAttribute(model, "filter", defaultFilter());
		conditionalAttribute(model, "yields", new ArrayList<>());
		
		return PAGE_INDEX;
	}

	@PostMapping("/track")
	public String track(Model model, RedirectAttributes redirectAttributes, Filter filter) throws IOException, ParseException, InterruptedException, ExecutionException {
		
		List<Yield> yields = new ArrayList<>();

		Future<Elements> futureDividends = scraper.scrapDividend(filter.getStock());
		Future<Elements> futurePrice = scraper.scrapPrice(filter.getStock());
		
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
			
			if(filter.isGroup()) {
				
				Yield yield = find(yields, year);
				if(yield != null) {
					yield.setValue(yield.getValue().add(value));
				}
				else {
					yields.add(new Yield(parse(date), year.toString(), value, "PROVENTOS"));
				}
			}
			else {
				yields.add(new Yield(parse(date), date, value, type));
			}
		}
		
		redirectAttributes.addFlashAttribute("filter", filter);
		redirectAttributes.addFlashAttribute("result", calculate(yields, filter, futurePrice));
		redirectAttributes.addFlashAttribute("yields", yields);
		
		return "redirect:/";
	}
	
	private Result calculate(List<Yield> yields, Filter filter, Future<Elements> futurePrice) throws IOException, InterruptedException, ExecutionException {
		
		if(CollectionUtils.isEmpty(yields)) {
			return null;
		}
		
		Result result = new Result();
		
		BigDecimal targetYield = filter.getTargetYield().divide(new BigDecimal("100"), MathContext.DECIMAL128);
		
		BigDecimal total = yields.stream().map(Yield::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
		Map<Integer, List<Yield>> yearGroup = yields.stream().collect(Collectors.groupingBy(y -> y.getDate().getYear()));
		
		result.setTotal(total);
		result.setAverage(total.divide(BigDecimal.valueOf(yearGroup.size()), MathContext.DECIMAL128));
		result.setPrice(getPrice(futurePrice));
		result.setAverageYield(result.getAverage().divide(result.getPrice(), MathContext.DECIMAL128).multiply(new BigDecimal("100")));
		result.setLimitPrice(result.getAverage().divide(targetYield, MathContext.DECIMAL128));
		result.setDifference(result.getLimitPrice().subtract(result.getPrice()));
		
		result.setAverage(result.getAverage().setScale(4, RoundingMode.DOWN));
		result.setAverageYield(result.getAverageYield().setScale(2, RoundingMode.DOWN));
		result.setLimitPrice(result.getLimitPrice().setScale(2, RoundingMode.DOWN));
		result.setDifference(result.getDifference().setScale(2, RoundingMode.DOWN));
		
		return result;
	}

	private BigDecimal getPrice(Future<Elements> futurePrice) throws IOException, InterruptedException, ExecutionException {
		String value = futurePrice.get().get(0).text();
		return new BigDecimal(value.replace(',', '.'));
	}

	private Yield find(List<Yield> yields, Integer year) {
		
		return yields.stream().filter(y -> y.getDate().getYear() == year).findFirst().orElse(null);
	}

	private Integer getYear(String date) {		
		return Integer.valueOf(date.substring(6, 10));
	}
}

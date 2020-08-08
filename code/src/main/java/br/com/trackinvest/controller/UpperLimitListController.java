package br.com.trackinvest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.trackinvest.model.Stock;
import br.com.trackinvest.stock.ConfigureStockListRepository;

@Controller
public class UpperLimitListController extends BaseController {	
	
	@Autowired
	private ConfigureStockListRepository configureRepository;
	
	private static final String PAGE_LIST = "/pages/upper-limit/list";
	
	@GetMapping("/upper-limit/list")
	public String home(Model model) {
		
		model.addAttribute("filter", defaultFilter());
		model.addAttribute("configuredStocks", configureRepository.findAll());
		return PAGE_LIST;
	}
	
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/upper-limit/stock/add")
	public Long addStock(@RequestParam("symbol") String symbol) {
		
		Stock stock = new Stock();
		stock.setSymbol(symbol.toUpperCase());
		Stock result = configureRepository.save(stock);
		
		return result.getId();
	}
	
	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/upper-limit/stock/delete")
	public void deleteStock(@RequestParam("id") Long id) {
		
		configureRepository.deleteById(id);
	}
}

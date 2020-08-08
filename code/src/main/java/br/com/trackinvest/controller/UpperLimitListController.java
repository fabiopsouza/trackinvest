package br.com.trackinvest.controller;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.trackinvest.model.Filter;
import br.com.trackinvest.model.Result;
import br.com.trackinvest.model.Stock;
import br.com.trackinvest.repository.ConfigureFilterListRepository;
import br.com.trackinvest.repository.ConfigureStockListRepository;
import br.com.trackinvest.repository.ResultRepository;
import br.com.trackinvest.service.TrackService;

@Controller
public class UpperLimitListController extends BaseController {	
	
	@Autowired
	private TrackService service;
	
	@Autowired
	private ResultRepository resultRepository;
	
	@Autowired
	private ConfigureStockListRepository configureRepository;
	
	@Autowired
	private ConfigureFilterListRepository configureFilterRepository;
	
	private static final String PAGE_LIST = "/pages/upper-limit/list";
	
	@GetMapping("/upper-limit/list")
	public String home(Model model) {
		
		Filter filter = configureFilterRepository.findAll().iterator().hasNext() ? 
				configureFilterRepository.findAll().iterator().next() : defaultFilter();
		
		conditionalAttribute(model, "filter", filter);
		model.addAttribute("configuredStocks", configureRepository.findAll());
		model.addAttribute("results", resultRepository.findAllByOrderByDifferenceDesc());
		return PAGE_LIST;
	}
	
	@PostMapping("/upper-limit/list/track")
	public String track(Model model, RedirectAttributes redirectAttributes, Filter filter) 
			throws IOException, InterruptedException, ExecutionException {
		
		configureFilterRepository.deleteAll();
		configureFilterRepository.save(filter);
		
		resultRepository.deleteAll();
		Iterable<Stock> stocks = configureRepository.findAll();
		for (Stock stock : stocks) {
			
			Result result = service.track(stock.getSymbol(), filter);
			resultRepository.save(result);
		}
		
		redirectAttributes.addFlashAttribute("filter", filter);
		
		return "redirect:/upper-limit/list";
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

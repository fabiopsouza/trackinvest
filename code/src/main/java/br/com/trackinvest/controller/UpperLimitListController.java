package br.com.trackinvest.controller;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.trackinvest.model.Filter;
import br.com.trackinvest.model.Result;
import br.com.trackinvest.model.Stock;
import br.com.trackinvest.repository.ConfigureFilterListRepository;
import br.com.trackinvest.repository.ConfigureStockListRepository;
import br.com.trackinvest.repository.ResultRepository;
import br.com.trackinvest.socket.TrackProgressRunnable;

@Controller
public class UpperLimitListController extends BaseController {	
	
	@Autowired
	private TrackProgressRunnable progressRunnable;
	
	@Autowired
	private ResultRepository resultRepository;
	
	@Autowired
	private ConfigureStockListRepository configureRepository;
	
	@Autowired
	private ConfigureFilterListRepository configureFilterRepository;
	
	private static final String PAGE_LIST = "pages/upper-limit/list";
	
	@GetMapping("/upper-limit/list")
	public String home(Model model) {
		
		Filter filter = configureFilterRepository.findAll().iterator().hasNext() ? 
				configureFilterRepository.findAll().iterator().next() : defaultFilter();
		
		conditionalAttribute(model, "filter", filter);
		model.addAttribute("configuredStocks", configureRepository.findAll());
		model.addAttribute("results", resultRepository.findAllByOrderByLiquidAverageYieldDesc());
		return PAGE_LIST;
	}
	
	// Handle socket message
	@MessageMapping("/upper-limit/list/track")
	public void track(Filter filter) 
			throws IOException, InterruptedException, ExecutionException {
		
		progressRunnable.setFilter(filter);
		progressRunnable.run();
	}
	
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/upper-limit/stock/add")
	public Long addStock(@RequestParam("symbol") String symbol) {
		
		if(StringUtils.isBlank(symbol)) {
			return -1l;
		}
		
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
	
	@ResponseStatus(HttpStatus.OK)
	@PostMapping("/upper-limit/stock/select")
	public void selectStock(@RequestParam("id") Long id, @RequestParam("checked") Boolean checked) {
		
		Result result = resultRepository.findById(id).get();
		result.setSelected(checked);
		
		resultRepository.save(result);
	}
}

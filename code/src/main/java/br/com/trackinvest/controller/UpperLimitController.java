package br.com.trackinvest.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.trackinvest.model.Filter;
import br.com.trackinvest.model.Result;
import br.com.trackinvest.service.TrackService;

@Controller
public class UpperLimitController extends BaseController {	
	
	private static final String PAGE_INDEX = "pages/upper-limit/index";
	
	@Autowired
	private TrackService service;
	
	@GetMapping("/")
	public String home(Model model) {

		conditionalAttribute(model, "filter", defaultFilter());
		conditionalAttribute(model, "yields", new ArrayList<>());
		
		return PAGE_INDEX;
	}

	@PostMapping("/track")
	public String track(RedirectAttributes redirectAttributes, Filter filter) 
			throws IOException, ParseException, InterruptedException, ExecutionException {
		
		if(StringUtils.isBlank(filter.getStock())) {
			return "redirect:/";
		}
		
		Result result = service.track(filter);
		
		redirectAttributes.addFlashAttribute("filter", filter);
		
		if(result != null) {
			redirectAttributes.addFlashAttribute("result", result);
			redirectAttributes.addFlashAttribute("yields", result.getYields());
		}
		
		return "redirect:/";
	}
}

package br.com.trackinvest.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.ui.Model;

import br.com.trackinvest.model.Filter;

public abstract class BaseController {

	protected void conditionalAttribute(Model model, String name, Object value) {

		if (model.getAttribute(name) == null) {
			model.addAttribute(name, value);
		}
	}
	
	protected Filter defaultFilter() {
		
		Filter filter = new Filter();
		filter.setGroup(true);
		filter.setDateStart(LocalDate.now().getYear() - 5);
		filter.setDateEnd(LocalDate.now().getYear() - 1);
		filter.setTargetYield(new BigDecimal("6"));
		
		return filter;
	}
}

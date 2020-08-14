package br.com.trackinvest.socket;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.collections.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.trackinvest.model.Filter;
import br.com.trackinvest.model.Result;
import br.com.trackinvest.model.Stock;
import br.com.trackinvest.repository.ConfigureFilterListRepository;
import br.com.trackinvest.repository.ConfigureStockListRepository;
import br.com.trackinvest.repository.ResultRepository;
import br.com.trackinvest.service.TrackService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TrackProgressRunnable extends ProgressMonitor implements Runnable {

	private static String SOCKET_ENDPOINT = "/track-web/upper-limit-list";

	@Getter
	@Setter
	private Filter filter;
	
	@Autowired
	private ResultRepository resultRepository;
	
	@Autowired
	private ConfigureStockListRepository configureRepository;
	
	@Autowired
	private ConfigureFilterListRepository configureFilterRepository;
	
	@Autowired
	private TrackService trackService;
	
	@Override
	@SuppressWarnings("unchecked")
	public void run()
	{
		configureFilterRepository.deleteAll();
		configureFilterRepository.save(filter);
		
		resultRepository.deleteAll();
		Iterable<Stock> list = configureRepository.findAll();
		List<Stock> stocks = IteratorUtils.toList(list.iterator());
		
		int i = 1;
		int total = stocks.size();
		
		log.info("Track {} elements", total);
		
		if(total == 0) {
			updateProgress(SOCKET_ENDPOINT);
		}
		
		for (Stock stock : stocks) {

			try {
				
				log.info("Tracking {}", stock.getSymbol());
				
				Result result = trackService.track(stock.getSymbol(), filter);
				if(result != null) {
					resultRepository.save(result);
				}
				
				updateProgress(SOCKET_ENDPOINT, i++, total);
				
			} catch (IOException | InterruptedException | ExecutionException e) {
				log.error(e.getMessage());
			}
		}
		
		Thread.currentThread().interrupt();
	}	
}

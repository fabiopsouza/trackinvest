package br.com.trackinvest.service;

import java.io.IOException;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
public class ScrapService {

	private static final String URL_PRICE = "https://statusinvest.com.br/acoes/%s";
	
	private static final String URL_DIVIDEND = "https://www.fundamentus.com.br/proventos.php?papel=%s&tipo=2";
	
	@Async
	@Cacheable("dividends")
	public Future<Elements> scrapDividend(String stock) throws IOException 
	{
		Document page = Jsoup.connect(String.format(URL_DIVIDEND, stock)).get();
		return new AsyncResult<Elements>(page.select("#resultado tbody tr"));
	}
	
	@Async
	public Future<Elements> scrapPrice(String stock) throws IOException 
	{
		Document page = Jsoup.connect(String.format(URL_PRICE, stock)).get();
		return new AsyncResult<Elements>(page.select(".special .value"));
	}
}

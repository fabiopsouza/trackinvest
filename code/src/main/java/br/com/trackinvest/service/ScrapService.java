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

	private static final String URL_BRIDGE = "https://scraping-bridge.000webhostapp.com/?url=%s";
	
	private static final String URL_DIVIDEND = "https://www.fundamentus.com.br/proventos.php?papel=%s&tipo=2";
	
	private static final String URL_PRICE = "https://statusinvest.com.br/acoes/%s";
	
	@Async
	@Cacheable("dividends")
	public Future<Elements> scrapDividend(String stock) throws IOException 
	{
		return scrap(URL_DIVIDEND, stock, "#resultado tbody tr");
	}
	
	@Async
	public Future<Elements> scrapPrice(String stock) throws IOException 
	{
		return scrap(URL_PRICE, stock, ".special .value");
	}
	
	public Future<Elements> scrap(String urlParameter, String stock, String selector) throws IOException 
	{
		String url = String.format(urlParameter, stock);
		Document page = Jsoup.connect(String.format(URL_BRIDGE, url)).get();
		return new AsyncResult<Elements>(page.select(selector));
	}
}

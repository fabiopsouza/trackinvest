package br.com.trackinvest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class TrackInvestApp 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(TrackInvestApp.class, args);
    }
}

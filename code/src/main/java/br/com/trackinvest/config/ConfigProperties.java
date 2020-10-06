package br.com.trackinvest.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "trackinvest")
public class ConfigProperties {

	private List<String> jscpDomain;
	
	private String jscpTax;
}

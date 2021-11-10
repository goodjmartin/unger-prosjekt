package no.goodtech.vaadin.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginConfiguration {

	@Bean
	public String loginInformation(@Value("${loginInformation:}") final String loginInfo) {
		return loginInfo;
	}
}

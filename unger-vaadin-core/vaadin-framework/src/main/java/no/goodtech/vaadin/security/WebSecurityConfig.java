package no.goodtech.vaadin.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

	@Value("${security.ssl.requiresSecureRegexMatchers:.*}")
	private String requiresSecureRegexMatchers;

	@Value("${security.enableSSL:false}")
	private boolean enableSSL;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		LOGGER.info(String.format("WebSecurityConfig: enableSSL=%s, requiresSecureRegexMatchers=%s", enableSSL, requiresSecureRegexMatchers));

		// Configure redirect from HTTP to HTTPS
		if (enableSSL) {
			http.requiresChannel().regexMatchers(requiresSecureRegexMatchers.split(",")).requiresSecure();
		}

		// Disable header 'X-Frame-Options: DENY' (default in spring security) ... to get IFrame in Ewos to work
		http.headers().frameOptions().disable();

		// Disable CSRF
		http.csrf().disable();
	}

}
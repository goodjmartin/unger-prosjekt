package no.goodtech.vaadin.security.tabs.user;

import no.goodtech.vaadin.security.Globals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;


@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@Configuration
@Order(3)
@Primary
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${security.ssl.requiresSecureRegexMatchers:.*}")
	private String requiresSecureRegexMatchers;

	@Value("${security.enableSSL:false}")
	private boolean enableSSL;

	@Value(value = "${security.url.main}")
	private String appUrl;

	@Value(value = "${security.url.login}")
	private String loginUrl;

	@Value(value = "${security.url.login-processing}")
	private String loginProcessingUrl;

	@Value("${security.url.logout}")
	private String logOutUrl;


	private static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

	@Bean
	public AuthUserDetailsService authUserDetailsService(){
		return new AuthUserDetailsService();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		super.configure(auth);
		auth.userDetailsService(authUserDetailsService()).and().authenticationProvider(authenticationProvider());
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(authUserDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		LOGGER.info(String.format("SecurityConfig: enableSSL=%s, requiresSecureRegexMatchers=%s", enableSSL, requiresSecureRegexMatchers));
		LOGGER.info(String.format("SAMLSecurityConfig - Starting up with given paths: main=%s, login=%s, logout=%s", appUrl, loginUrl, logOutUrl));

		// Configure redirect from HTTP to HTTPS
		if (enableSSL) {
			http.requiresChannel().regexMatchers(requiresSecureRegexMatchers.split(",")).requiresSecure();
		}

		// Disable header 'X-Frame-Options: DENY' (default in spring security) ... to get IFrame in Ewos to work
		http.headers().frameOptions().disable();

		String entryPoint = Globals.isRequireLogin() ? loginUrl : appUrl;

		// Disable CSRF because Vaadin handles this on by itself
		http.csrf().disable().
				exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint(entryPoint)).accessDeniedPage(entryPoint)
				.and().authorizeRequests()
				.antMatchers("/VAADIN/**", "/PUSH/**", "/UIDL/**", "/login", "/login/**", "/vaadinServlet/**").permitAll();

		if (Globals.isRequireLogin()){
			// Set all urls within appUrl authenticated
			http.authorizeRequests().antMatchers(appUrl, "/**").fullyAuthenticated();
		}else{
			// Set all urls within appUrl free to go
			http.authorizeRequests().antMatchers(appUrl, "/**").permitAll();
		}


		http.formLogin().loginPage(loginUrl).loginProcessingUrl(loginProcessingUrl);
		http.logout().logoutUrl(logOutUrl);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new LegacyPasswordEncoder();
	}
}

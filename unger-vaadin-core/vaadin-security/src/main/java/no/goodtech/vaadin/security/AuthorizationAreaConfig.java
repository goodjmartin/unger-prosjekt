package no.goodtech.vaadin.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthorizationAreaConfig {

	@Bean(name = "authorizationAreaManager", initMethod = "init")
	AuthorizationAreaManager authorizationAreaManager(
			@Value("${factory.authorizeAreas}") String areas
	) {
		return new AuthorizationAreaManager(areas);
	}
}

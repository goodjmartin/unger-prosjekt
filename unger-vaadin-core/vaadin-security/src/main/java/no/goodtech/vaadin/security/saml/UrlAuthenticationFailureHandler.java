package no.goodtech.vaadin.security.saml;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UrlAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(UrlAuthenticationFailureHandler.class);

	public UrlAuthenticationFailureHandler() {
		this("/error");
	}

	public UrlAuthenticationFailureHandler(String failureUrl) {
		super(failureUrl);
		setUseForward(true);
		LOGGER.info("Setting up failure handler with url: " + failureUrl);
	}


	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
		try {
			super.onAuthenticationFailure(request, response, exception);
			if (exception != null && exception.getMessage() != null && !exception.getMessage().isEmpty())
				LOGGER.warn(exception.getMessage());
		} catch (IOException | ServletException e) {
			LOGGER.error(e.getMessage());
		}
	}
}

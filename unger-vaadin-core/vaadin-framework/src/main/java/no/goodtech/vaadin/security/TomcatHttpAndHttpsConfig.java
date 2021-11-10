package no.goodtech.vaadin.security;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@Profile("ssl")
public class TomcatHttpAndHttpsConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(TomcatHttpAndHttpsConfig.class);

	@Value("${security.proxyPort:8080}")
	private int proxyPort;

	@Value("${server.port:8443}")
	private int destinationPort;

	@Value("${server.address:}")
	private String serverAddress;

	@Bean
	public EmbeddedServletContainerFactory servletContainer(){
		TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
		tomcat.addAdditionalTomcatConnectors(httpConnector());
		return tomcat;
	}

	private Connector httpConnector(){
		Connector connector = new Connector(TomcatEmbeddedServletContainerFactory.DEFAULT_PROTOCOL);
		connector.setScheme("http");
		connector.setPort(proxyPort);
		connector.setSecure(false);

		// Bind to specific server address (network card)
		if ((serverAddress != null) && (serverAddress.trim().length() > 0) && connector.getProtocolHandler() instanceof Http11NioProtocol) {
			try {
				LOGGER.info(String.format("Binding to server address: %s", serverAddress));
				((Http11NioProtocol) connector.getProtocolHandler()).setAddress(InetAddress.getByName(serverAddress.trim()));
			} catch (UnknownHostException e) {
				LOGGER.error(String.format("Unable to bin to server address: %s", serverAddress));
			}
		}

		LOGGER.info("Will redirect clients from port {} to {}", proxyPort, destinationPort);
		return connector;
	}

}
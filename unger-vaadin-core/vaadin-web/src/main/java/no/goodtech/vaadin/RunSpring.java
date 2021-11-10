package no.goodtech.vaadin;

import no.goodtech.vaadin.security.Main;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@ImportResource(locations = {"classpath:vaadin-web-server.xml"})
//@EnableVaadin
@ComponentScan("no.goodtech")
@SpringBootApplication(scanBasePackageClasses = {Main.class, RunSpring.class})
public class RunSpring extends SpringBootServletInitializer {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(RunSpring.class, args);

	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RunSpring.class);
	}
}

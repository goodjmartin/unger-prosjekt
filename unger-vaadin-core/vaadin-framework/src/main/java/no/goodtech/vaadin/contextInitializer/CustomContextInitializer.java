package no.goodtech.vaadin.contextInitializer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Finner hvilket directory jar/war filen kjøres fra, og setter mesproperties til mes properties i current directory hvis den
 * eksisterer
 */
public class CustomContextInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {

    /**
     * Leser properties filer og tildeler prioritet.
     * Propertiesfilen som ligger i current directory blir gitt høyest prioritet.
     * Propertiesfilen for PDB får annen prioritet
     * Default-properties fil (tilsvarende hvordan det var satt opp i mes-server xml med liste over properties) får lavest prioritet
     */
    public void initialize(ConfigurableWebApplicationContext ctx) {
        Properties mesProperties = new Properties();

        String fileNameAndPath = System.getProperty("user.dir") + File.separator + "mes.properties";
        try {
            if (ClassLoader.getSystemClassLoader() != null) {
				mesProperties.load(new FileInputStream(fileNameAndPath));
                System.out.println("CustomContextInitializer(): Laster properties fra " + fileNameAndPath);
            } else {
                System.out.println("ERROR: Fikk ikke lastet " + fileNameAndPath + ", pga. classloader var null");          	
            }
        } catch (IOException e) {
            System.out.println("WARN: Fant ikke " + fileNameAndPath + ", bruker standard-verdier i intern config-fil");
        }

        MutablePropertySources propertySources = ctx.getEnvironment().getPropertySources();
        propertySources.addFirst(new PropertiesPropertySource("mainProperties", mesProperties));
    }

}
package no.goodtech.vaadin.global;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.server.VaadinServlet;

/**
 * Gir deg tilgang til bønner håndtert av Spring i Vaadin
 */
public class VaadinSpringContextHelper {

    /**
     * Gir deg Spring-bønna med angitt navn
     * @param name navnet på bønna
     * @return Spring-bønna med angitt navn
     * @throws NoSuchBeanDefinitionException hvis jeg ikke finner bønna
     */
    public static Object getBean(final String name) {
    	final ApplicationContext applicationContext = getApplicationContext();
    	return applicationContext.getBean(name);
    }
    
    /**
     * Gir deg Spring-bønna av angitt type hvis det bare finnes en bønne av denne typen
     * @param type typen til bønna
     * @return Spring-bønna med av denne typen
     * @throws NoSuchBeanDefinitionException hvis jeg ikke finner bønna, eller hvis det er flere bønner av denne typen
     */
	public static <T> T getBean(Class<T> type) {
    	final ApplicationContext applicationContext = getApplicationContext();
    	return (T) applicationContext.getBean(type);
    }

	private static ApplicationContext getApplicationContext() {
		final ServletContext servletContext = VaadinServlet.getCurrent().getServletContext();
    	final ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		return applicationContext;
	}    
}


package no.goodtech.vaadin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.Locale;

/**
 * This class sets up the application environment.
 *
 * Note!
 * This class implements BeanPostProcessor only to ensure it is constructed before any other 'normal' bean.
 */
public class Environment implements BeanPostProcessor {

	public Environment(final String country, final String language) {
		Logger logger = LoggerFactory.getLogger(getClass());

		logger.info("Setting default locale: country={}, laguage={}", country, language);
		Locale.setDefault(new Locale(language, country));
	}

	@Override
	public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
		return bean;
	}

}
